package com.nokia.boss.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.elasticsearch.action.index.IndexRequest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.nokia.boss.bean.MessageItem;
import com.nokia.boss.service.ElasticSearchBulkService;
import com.nokia.boss.settings.CustomSettings;
import com.nokia.boss.task.LoadStaticData;

@Component
public class AnalyseUtils {

	private static final Logger LOGGER = LogManager.getLogger(AnalyseUtils.class);
	private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
	private static String SOAP_LOG_PATTERN = "#response_time#|User:#user_name#| id:#task_id# |{\"HLRSN\":\"#HLRSN#\",\"operationName\":\"#operation_name#\",#subscriber_number#}";
	public static DateTimeFormatter soapFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss SSS");
	public static DateTimeFormatter indexFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static final String FAILURE = "failure";

	/**
	 * 把GZ文件转换成普通的文本文件类，便于解析。
	 * 
	 * @param gzPath
	 *            GZ包的完整路径
	 * @param soapGwName
	 *            SOAPGW名称
	 * @return
	 * @throws IOException
	 */

	public static File gzToFile(File gzFile, String soapGwName) throws IOException {
		String gzFileName = gzFile.getName();
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new GZIPInputStream(new FileInputStream(gzFile.getAbsolutePath())), "utf-8"));
		String line = null;
		StringBuilder gzText = new StringBuilder();
		while ((line = br.readLine()) != null) {
			gzText.append(line + "\n");
		}
		br.close();
		String tempTransformFileAbsPath = LoadStaticData.getCACHE_DIR().get(soapGwName) + soapGwName + "_"
				+ gzFileName.replace(".gz", ".trans");
		File f = new File(tempTransformFileAbsPath);
		FileWriter fw = new FileWriter(f);
		fw.write(gzText.toString());
		fw.close();
		return f;
	}

	/**
	 * 把SOAP日志解析为两种字符串，都是以LOAD SQL的格式写好。
	 * 
	 * @param originalText
	 * @return
	 */
	public static void originalDataTransformer(StringBuilder originalText, boolean hlrsnTransform,
			ElasticSearchBulkService esbService, Map<String, MessageItem> errMap, CustomSettings customSettings) {
		LOGGER.info("errMap.size():" + errMap.size());
		String[] originDataLines = originalText.toString().split("\n");
		List<String> successDataList = new ArrayList<String>();
		Map<String, String> stateMap = new HashMap<>();
		for (String string : originDataLines) {
			if (string.contains("Callback")) {
				// id->time
				stateMap.put(string.split("\\|")[2].trim().replace("id:", ""), string.substring(0, 23));
			} else {
				successDataList.add(string);
			}
		}
		LOGGER.info("Soap data size:" + successDataList.size());
		LOGGER.info("State data size:" + stateMap.size());
		List<IndexRequest> indexRequestList = new ArrayList<>();
		// Integer startFlag = 0;
		String index = customSettings.getElSearchConfig().getMockIndex();
		String type = customSettings.getElSearchConfig().getSoapType();
		// Integer batch = customSetting.getMockBatch();

		for (String dataLine : successDataList) {
			String dataLine_ = new String(dataLine);
			try {
				int splitFlag = 0;
				String responseTime = null;
				String user = null;
				String taskId = null;
				String restInfo = null;
				while (true) {
					String splitStr = null;
					int j = dataLine.indexOf("|");
					if (j < 0)
						break;
					splitStr = dataLine.substring(0, j);
					dataLine = dataLine.substring(j + 1);
					switch (splitFlag) {
					case 0:
						responseTime = splitStr;
					case 1:
						user = splitStr;
					case 2:
						taskId = splitStr;
					case 3:
						restInfo = splitStr;
					}
					if (splitFlag == 2) {
						restInfo = dataLine;
						break;
					}
					splitFlag++;
				}
				user = user.replace("User: ", "");
				taskId = taskId.replace("id:", "").trim();
				/**
				 * Analyze "JSON" in the rest info.
				 */
				String MSISDN = getJsonValue(restInfo, LoadStaticData.getMSISDN_PATTERN());
				String HLRSN = getJsonValue(restInfo, LoadStaticData.getHLRSN_PATTERN());
				if (hlrsnTransform == true) {
					// 浙江规则开始
					if (HLRSN.startsWith("5")) {
						HLRSN = "35";
					} else if (HLRSN.startsWith("6")) {
						HLRSN = "36";
					} else if (HLRSN.startsWith("7")) {
						HLRSN = "37";
					} else if (HLRSN.startsWith("8")) {
						HLRSN = "38";
					}
					// 浙江规则结束

					// 河南规则开始

					// 河南规则结束
				}
				String IMSI = getJsonValue(restInfo, LoadStaticData.getIMSI_PATTERN());
				String IMPU = getJsonValue(restInfo, LoadStaticData.getIMPU_PATTERN());
				String operationName = getJsonValue(restInfo, LoadStaticData.getOPERATION_NAME_PATTERN());
				String businessType = LoadStaticData.getBusinessType(operationName);

				if (IMSI.equals("") && MSISDN.equals("")) {
					if (IMPU.contains("+86")) {
						MSISDN = IMPU.substring(5, 18);
					}
					else if (IMPU.contains("46000")) {
						LOGGER.info("IMPU:{}",IMPU);
						IMSI = IMPU.substring(4, 19);
					}
				}
				long delay = -1;
				String endTime = stateMap.get(taskId);
				if (endTime != null) {
					String startTime = responseTime;
					delay = delayTimeCalculator(startTime, endTime);
				} else {
					delay = 0;
				}
				IndexRequest indexRequest = new IndexRequest(index);
				// startFlag++;
				indexRequest.index(customSettings.getElSearchConfig().getMockIndex() + "-"
						+ responseTime.substring(0, responseTime.indexOf(" ")));
				indexRequest.type(type);
				Map<String, Object> soapDataMap = new HashMap<>();
				soapDataMap.put("task_id", taskId);
				soapDataMap.put("response_time", DateTime.parse(responseTime, soapFormat).toDateTime());
				soapDataMap.put("user_name", user);
				soapDataMap.put("msisdn", MSISDN);
				soapDataMap.put("imsi", IMSI);
				soapDataMap.put("hlrsn", HLRSN);
				soapDataMap.put("operation_name", operationName);
				soapDataMap.put("business_type", businessType);
				soapDataMap.put("soap_log", getLog(soapDataMap));
				soapDataMap.put("response_status", "success");
				soapDataMap.put("delay", delay);
				if (errMap.containsKey(taskId)) {
					MessageItem mi = errMap.get(taskId);
					soapDataMap.put("response_status", mi.getResponse_status());
					soapDataMap.put("error_log", mi.getError_log());
					soapDataMap.put("error_code", mi.getError_code());
					soapDataMap.put("error_message", mi.getError_message());
					soapDataMap.put("user_password", mi.getUser_password());
					if (StringUtils.isEmpty(IMSI)) {
						soapDataMap.put("imsi", mi.getImsi());
					}
					errMap.remove(taskId);
					LOGGER.debug("1errMap.size():" + errMap.size());
				}
				indexRequest.source(soapDataMap);
				indexRequestList.add(indexRequest);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("ANALYZING ERROR(10001):{}:{}", "Original data", dataLine_);
			}
		}
		if (indexRequestList.size() > 0) {
			esbService.loadDataToEs(indexRequestList);
		}
		
	}

	public static String getLog(Map<String, Object> soapDataMap) {
		String pattern = SOAP_LOG_PATTERN;
		// "#response_time#|User:#user_name#| id:#task_id#
		// |{#HLRSN#,#operation_name#,#subscriber_number#}"
		DateTime dt = (DateTime) soapDataMap.get("response_time");
		String responseTime = dt.toString("yyyy-MM-dd HH:mm:ss,SSS");
		pattern = pattern.replace("#response_time#", responseTime);
		pattern = pattern.replace("#user_name#", soapDataMap.get("user_name").toString());
		pattern = pattern.replace("#task_id#", soapDataMap.get("task_id").toString());
		pattern = pattern.replace("#HLRSN#", soapDataMap.get("hlrsn").toString());
		pattern = pattern.replace("#operation_name#", soapDataMap.get("operation_name").toString());
		String imsi = soapDataMap.get("imsi").toString();
		String msisdn = soapDataMap.get("msisdn").toString();
		String subscriberNumber = "";
		if (imsi.length() >= 0 && msisdn.length() > 0) { // imsi,msisdn都存在，干掉imsi
			subscriberNumber = "\"ISDN\":\"" + msisdn + "\"";
		}
		if (imsi.length() > 0 && msisdn.length() == 0) { // 只有IMSI
			subscriberNumber = "\"IMSI\":\"" + imsi + "\"";
		}
		if (imsi.length() == 0 && msisdn.length() == 0) { // 都没有，随机生成一个msisdn
			subscriberNumber = "\"ISDN\":\"" + getRandomMsisdn() + "\"";
		}
		pattern = pattern.replace("#subscriber_number#", subscriberNumber);
		return pattern;
	}

	public static String getRandomMsisdn() {
		Integer randomIndex = (int) (Math.random() * 100);
		if (randomIndex <= 80) { // 80%有msisdn,实际上绝大多数数据是只有MSISDN
			String msisdn = "8618";
			for (int i = 0; i < 9; i++) {
				msisdn += (int) (Math.random() * 10);
			}
			return msisdn;
		} else {
			return "";
		}

	}

	public static void originalDataTransformerUnicom(StringBuilder originalText, ElasticSearchBulkService esbService,
			Map<String, MessageItem> errMap,CustomSettings customSettings) {
		Map<String, String> taskIdAndHlrsnMap = new HashMap<>();
		String[] originDataLines = originalText.toString().split("\n");
		List<String> successDataList = new ArrayList<String>();
		Map<String, String> stateMap = new HashMap<>();
		for (String string : originDataLines) {
			if (string.contains("Callback")) {
				stateMap.put(string.split("\\|")[1].trim().replace("User: ", ""), string.substring(0, 23));
			} else {
				successDataList.add(string);
			}
		}
		LOGGER.info("Soap data size:" + successDataList.size());
		LOGGER.info("State data size:" + stateMap.size());
		List<IndexRequest> indexRequestList = new ArrayList<>();
		Integer startFlag = 0;
		String index = customSettings.getElSearchConfig().getMockIndex();
		String type = customSettings.getElSearchConfig().getSoapType();
		for (String dataLine : successDataList) {
			String dataLine_ = new String(dataLine);
			try {
				int splitFlag = 0;
				String responseTime = null;
				String restInfo = null;
				while (true) {
					String splitStr = null;
					int j = dataLine.indexOf("|");
					if (j < 0)
						break;
					splitStr = dataLine.substring(0, j);
					dataLine = dataLine.substring(j + 1);
					switch (splitFlag) {
					case 0:
						responseTime = splitStr;
					case 3:
						restInfo = splitStr;
					}
					if (splitFlag == 2) {
						restInfo = dataLine;
						break;
					}
					splitFlag++;
				}
				// String responseTimeWithNoMilliseconds =
				// responseTime.substring(0, 19);
				String MSISDN = getJsonValue(restInfo, LoadStaticData.getMSISDN_PATTERN_CUC());
				String HLRSN = getJsonValue(restInfo, LoadStaticData.getHLRSN_PATTERN());
				String IMSI = getJsonValue(restInfo, LoadStaticData.getIMSI_PATTERN());
				String IMPU = getJsonValue(restInfo, LoadStaticData.getIMPU_PATTERN());
				String operation = getJsonValue(restInfo, LoadStaticData.getOPERATION_PATTERN());// 联通只有operation
																									// 没有operation
																									// name
				String businessType = LoadStaticData.getBusinessType(operation);
				String taskId = getJsonValue(restInfo, LoadStaticData.getTASK_ID_CUC());
				String userName = getJsonValue(restInfo, LoadStaticData.getUSER_CUC());
				taskIdAndHlrsnMap.put(taskId, HLRSN);
				if (IMSI.equals("") && MSISDN.equals("")) {
					if (IMPU.contains("+86")) {
						MSISDN = IMPU.substring(5, 18);
					}
					if (IMPU.contains("46000")) {
						IMSI = IMPU.substring(4, 19);
					}
				}
				long delay = 0;
				String endTime = stateMap.get(taskId);
				if (endTime != null) {
					String startTime = responseTime;
					delay = delayTimeCalculator(startTime, endTime);
				}
				IndexRequest indexRequest = new IndexRequest(index);
				startFlag++;
				indexRequest.index(customSettings.getElSearchConfig().getMockIndex() + "-"
						+ DateTime.parse(responseTime, indexFormat));
				indexRequest.type(type);

				Map<String, Object> soapDataMap = new HashMap<>();
				soapDataMap.put("task_id", taskId);
				soapDataMap.put("response_time", DateTime.parse(responseTime, soapFormat).toDateTime());
				soapDataMap.put("user_name", userName);
				soapDataMap.put("msisdn", MSISDN);
				soapDataMap.put("imsi", IMSI);
				soapDataMap.put("hlrsn", HLRSN);
				soapDataMap.put("operation_name", operation);
				soapDataMap.put("business_type", businessType);
				soapDataMap.put("soap_log", getLog(soapDataMap));
				soapDataMap.put("response_status", "success");
				soapDataMap.put("delay", delay);
				if (errMap.containsKey(taskId)) {
					MessageItem mi = errMap.get(taskId);
					soapDataMap.put("error_log", mi.getError_log());
					soapDataMap.put("error_code", mi.getError_code());
					soapDataMap.put("error_message", mi.getError_message());
					soapDataMap.put("user_password", mi.getUser_password());
					errMap.remove(taskId);
				}
				indexRequest.source(soapDataMap);
				indexRequestList.add(indexRequest);
			} catch (Exception e) {
				LOGGER.error("ANALYZING ERROR(20001):");
				LOGGER.error("can not ananlysis this soap line:{}", dataLine_);
				LOGGER.error(dataLine_);
				e.printStackTrace();
			}

		}
		if (indexRequestList.size() > 0) {
			esbService.loadDataToEs(indexRequestList);
		}
		if (errMap.size() > 0) {
			esbService.updateErr(errMap);
		}

	}

	/**
	 * 计算下发指令的时间延迟
	 * 
	 * @param startReTime
	 * @param endReTime
	 * @return
	 * @throws ParseException
	 */
	public static long delayTimeCalculator(String startReTime, String endReTime) throws ParseException {
		long delayTime = 0;
		try {
			Date startTime = SDF.parse(startReTime);
			Date endTime = SDF.parse(endReTime);
			delayTime = endTime.getTime() - startTime.getTime();
			return delayTime;
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 快速解析BOSS SOAP JSON串的方法。无须引包，增加JSON解析速度。
	 * 
	 * @param restInfo
	 *            BOSS_SOAP日志的JSON串部分。
	 * @param KEY_PATTERN
	 *            JSON串的KEY值。
	 * @return
	 */
	public static String getJsonValue(String restInfo, String KEY_PATTERN) {
		char[] tempCharArray = restInfo.toCharArray();
		int endFlag = -1;
		int startFlag = restInfo.indexOf(KEY_PATTERN);
		if (startFlag != -1) {
			for (int i = (restInfo.indexOf(KEY_PATTERN) + KEY_PATTERN.length()); i < tempCharArray.length; i++) {
				if (tempCharArray[i] == '"') {
					endFlag = i;
					break;
				}
			}
			return restInfo.substring(restInfo.indexOf(KEY_PATTERN) + KEY_PATTERN.length(), endFlag);
		} else {
			return "";
		}
	}

	/**
	 * 解析ERROR XML标签的值，解析后添加到errorInfoMap
	 * 
	 * @param node
	 * @param errorInfoMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void listNodes(Element node, MessageItem mi, boolean hlrsnTransform) {

		if (!(node.getTextTrim().equals(""))) {
			if (node.getName().equalsIgnoreCase("PassWord")) {
				mi.setUser_password(node.getText());
			}
			if (node.getName().equalsIgnoreCase("UserName")) {
				mi.setUser_name(node.getText());
			}
			if (node.getName().equalsIgnoreCase("HLRSN")) {
				String hlrsn = node.getText();
				if (hlrsnTransform == true) {
					if (hlrsn.startsWith("5")) {
						hlrsn = "35";
					} else if (hlrsn.startsWith("6")) {
						hlrsn = "36";
					} else if (hlrsn.startsWith("7")) {
						hlrsn = "37";
					} else if (hlrsn.startsWith("8")) {
						hlrsn = "38";
					}
				}

				mi.setHlrsn(hlrsn);

			}
			if (node.getName().equalsIgnoreCase("IMSI")) {
				mi.setImsi(node.getText());
			}
			if (node.getName().equalsIgnoreCase("ISDN")) {
				mi.setMsisdn(node.getText());
			}
			if (node.getName().equalsIgnoreCase("IMPU")) {
				if (node.getText().contains("+86")) {
					mi.setMsisdn(node.getText().substring(5, 18));
				} else {
					mi.setImsi(node.getText().substring(4, 18));
				}
			}
			if (node.getName().equalsIgnoreCase("ResultCode")) {
				mi.setError_code(node.getText());
			}
			if (node.getName().equalsIgnoreCase("ResultDesc")) {
				mi.setError_message(node.getText());
			}
		}

		Iterator<Element> iterator = node.elementIterator();
		while (iterator.hasNext()) {
			Element e = iterator.next();
			listNodes(e, mi, hlrsnTransform);
		}
	}

	/**
	 * 解析ERROR日志
	 * 
	 * @author Pei Nan
	 * @param errorLog.
	 *            完整的ERROR日志
	 * @return errorInfoMap.
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static Map<String, MessageItem> getErrorInfoMap(StringBuilder errorLog, boolean hlrsnTransform)
			throws DocumentException, IOException {
		String[] errorLogPieces = errorLog.toString().trim().split("\\+{57}");
		List<String> normalErrorListOriginal = new ArrayList<>();
		for (String error : errorLogPieces) {
			if (!error.contains("#HeartBeat#")) {
				normalErrorListOriginal.add(error);
			}
		}
		LOGGER.info("Normal error:" + normalErrorListOriginal.size());
		LOGGER.info("All size:" + errorLogPieces.length);
		Map<String, MessageItem> errMap = new ConcurrentHashMap<String, MessageItem>();
		for (String normalError : normalErrorListOriginal) {
			try {
				MessageItem mi = new MessageItem();
				mi.setError_log(normalError.trim());

				String task_id = normalError.trim().split("={57}")[0].substring(36).trim();
				String response_time_withMilliseconds = normalError.trim().split("={57}")[0].split(" ")[0] + " "
						+ normalError.trim().split("={57}")[0].split(" ")[1];
				mi.setTask_id(task_id);
				mi.setResponse_time(response_time_withMilliseconds);
				// request:
				String requestPhase = normalError.trim().split("={57}")[1].split("-{57}")[0].trim()
						.replace("request:", "").trim();
				Document documentRequest = DocumentHelper.parseText(requestPhase.toString());
				Element rootRequest = documentRequest.getRootElement();
				listNodes(rootRequest, mi, hlrsnTransform);
				// response:
				String responsePhase = normalError.trim().split("={57}")[1].split("-{57}")[1].trim()
						.replace("response:", "").trim();
				Document documentResponse = DocumentHelper.parseText(responsePhase.toString());
				Element rootResponse = documentResponse.getRootElement();
				for (String line : responsePhase.split("\n")) {
					if (line.contains("Response>")) {
						String operation_name = line.trim().replace(">", "").replace("<", "").replace("/", "")
								.replace("Response", "");
						mi.setOperation_name(operation_name);
						mi.setBusiness_type(LoadStaticData.getBusinessType(operation_name));
					}
				}
				listNodes(rootResponse, mi, hlrsnTransform);
				mi.setResponse_status(FAILURE);
				errMap.put(task_id, mi);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("ANALYSE ERROR:{}", normalError);
			}

		}

		return errMap;
	}

	/**
	 * 获取解析目标文件。 如果不是压缩文件，直接取文件。 如果是GZ压缩文件，先转换为文本文件，放在./cache下缓存。
	 * 
	 * @param targetLogFileName
	 * @param soapGwName
	 * @param targetFileDir
	 *            ABS PATH
	 * @return
	 * @throws IOException
	 */
	public static File extractAnalysisTarget(File targetFile, String soapGwName) throws IOException {
		File targetDataFile;
		if (!targetFile.getName().contains(".gz")) {
			LOGGER.info(targetFile.getAbsolutePath() + " is not a gz file.Return itself");
			targetDataFile = new File(targetFile.getAbsolutePath());
		} else {
			LOGGER.info(targetFile.getAbsolutePath() + " is a gz file.Need transform");
			targetDataFile = AnalyseUtils.gzToFile(targetFile, soapGwName);
			LOGGER.info(targetFile.getAbsolutePath() + "has been transformed as:" + targetDataFile.getAbsolutePath());
		}
		return targetDataFile;
	}

	public static Map<String, Object> getErrAnalysisTarget(File errFile, int startLine) throws IOException {
		int endLine = checkErrCompleteLines(errFile);// 应该取到这行
		LOGGER.info("Analysis target:" + errFile.getAbsolutePath());
		LOGGER.info("Start line of the file is: " + startLine);
		LOGGER.info("Last complete line of the file is: " + endLine);
		Map<String, Object> m = new HashMap<>();
		if (endLine == 1) {// 一块完整的都没有
			m.put("targetText", new StringBuilder());
			m.put("endLine", 0);
			return m;
		}
		FileReader fr = new FileReader(errFile);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		StringBuilder errLog = new StringBuilder();
		int i = 0;
		while ((line = br.readLine()) != null) {
			if (i >= startLine && i < endLine) {
				errLog.append(line + "\n");
			}
			i++;
		}
		br.close();
		m.put("targetText", new StringBuilder(errLog.toString().trim()));
		m.put("endLine", endLine);
		return m;
	}

	/**
	 * 取SOAP日志时，startLine参数是上一个周期的endLine。 endLine那行对应的行，应该是最后一行完整的数据。
	 * 如果startLine=0,说明MARK是被重置过的，取完整的文件即可（保证最后一行文本完整，最后一行完整文本的行数为endLine,作为startLine来更新MARK）
	 * 
	 * @param soapLog
	 *            原始文件
	 * @param startLine
	 *            开始行。包括此行的数据。
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> getSoapLogAnalysisTarget(File soapLogFile, int startLine) throws IOException {
		FileReader fr = new FileReader(soapLogFile);
		BufferedReader br = new BufferedReader(fr);
		int allCompleteLines = checkSoapCompleteLines(soapLogFile);// 应该取出最后一个完整行。
		LOGGER.info("Analysis target:" + soapLogFile.getAbsolutePath());
		LOGGER.info("Start line of the file is: " + startLine);
		LOGGER.info("Last complete line of the file is: " + allCompleteLines);
		StringBuilder soapLog = new StringBuilder();
		String lineText = null;
		int i = 0;
		while ((lineText = br.readLine()) != null) {
			i++;
			if (i > startLine && i <= allCompleteLines) {
				soapLog.append(lineText + "\n");
			}
		}
		br.close();
		fr.close();
		Map<String, Object> m = new HashMap<>();
		m.put("targetText", new StringBuilder(soapLog.toString().trim()));
		m.put("endLine", allCompleteLines);
		return m;
	}

	public static int checkErrCompleteLines(File errFile) throws IOException {
		FileReader fr = new FileReader(errFile);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		// ERR LOG里，完整的一段是以57个加号结束的
		int allCompleteLines = checkSoapCompleteLines(errFile);// 同理，认为每个换行符出现，就说明这行已经写完
		String[] allLines = new String[allCompleteLines];
		int i = 0;
		while ((line = br.readLine()) != null) {
			if (i <= allCompleteLines - 1) {
				allLines[i] = line + "\n";
				i++;

			} else {
				break;
			}
		}
		br.close();
		// 从最后一行找五十七个加号
		int lineNumber = 0;
		for (int j = allLines.length - 1; j >= 0; j--) {
			if (allLines[j].contains("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++")) {
				lineNumber = j;
				break;
			}
		}
		return lineNumber + 1;
	}

	public static int checkSoapCompleteLines(File soapFile) throws IOException {
		InputStream in = null;
		int lines = 0;
		try {
			in = new FileInputStream(soapFile);
			byte[] data = new byte[in.available()];
			in.read(data);
			String s = new String(data);
			lines = StringUtils.countOccurrencesOf(s, "\n");
			// 每有一个换行符，就认为有完整的一行
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}

	public static Map<String, MessageItem> errCaseCucLogAnalyser(StringBuilder errCaseUnicomTarget,
			Map<String, String> TASKID_AND_HLRSN_MAP) throws ParseException, IOException {
		String[] singleBlocks = errCaseUnicomTarget.toString().split("\\+{57}");
		List<String> requestBlankErrCaseList = new ArrayList<>();
		List<String> normalErrList = new ArrayList<>();
		for (String singleBlock : singleBlocks) {
			singleBlock = singleBlock.replaceAll("\\+", "");
			String request = singleBlock.split("={57}")[1].split("-{57}")[0].trim().replace("request:", "").trim();
			if (request.equals("//;") || (!request.contains(":"))) {
				requestBlankErrCaseList.add(singleBlock);
			} else if (singleBlock.contains("INCOMPLETE")) {
				requestBlankErrCaseList.add(singleBlock);
			} else {
				normalErrList.add(singleBlock);
			}
		}
		LOGGER.info("Normal error size:" + normalErrList.size());
		LOGGER.info("Blank error size:" + requestBlankErrCaseList.size());
		Map<String, MessageItem> errMap = new ConcurrentHashMap<String, MessageItem>();

		for (String normalErr : normalErrList) {
			MessageItem mi = new MessageItem();
			try {
				String title = normalErr.split("={57}")[0].trim();
				String request = normalErr.split("={57}")[1].split("-{57}")[0].trim().replace("request:", "").trim();
				String response = normalErr.split("-{57}")[1].trim().replace("response:", "").trim();
				if (response.contains("INCOMPLETE")) {
					LOGGER.debug("INCOMPLETE COMMAND:{}", normalErr);
					continue;
				}
				// String responseTimeWithNoMillisecond = title.split(",")[0];
				String taskId = title.substring(36);
				String errorCode = response.split("\n")[0].replaceAll("\\*", "").replaceAll("/", "").trim()
						.replace("DX ERROR:", "").trim();
				String errorMessage = response.split("\n")[1].replaceAll("\\*", "").replaceAll("/", "").trim();
				String operation = request.split(":")[0];
				String mmlRestStr = request.split(":")[1];
				String MSISDN = "0";
				String IMSI = "0";
				if (mmlRestStr.contains("IMSI")) {
					IMSI = mmlRestStr.replace("IMSI=", "").replace(";", "");
				} else if (mmlRestStr.contains("MSISDN")) {
					MSISDN = mmlRestStr.replace("MSISDN=", "").replace(";", "");
				} else {
					LOGGER.debug("{}: is neither MSISDN nor IMSI,command:{}", mmlRestStr, normalErr);
				}
				String businessType = LoadStaticData.getBusinessType(operation);
				String hlrsn = TASKID_AND_HLRSN_MAP.get(taskId);
				if (hlrsn == null) {
					hlrsn = "1";
				}
				mi.setBusiness_type(businessType);
				mi.setHlrsn(hlrsn);
				mi.setMsisdn(MSISDN);
				mi.setImsi(IMSI);
				mi.setOperation_name(operation);
				mi.setResponse_status(FAILURE);
				mi.setResponse_time(title.split(",")[0]);
				mi.setError_log(normalErr.trim());
				mi.setTask_id(taskId);
				mi.setError_code(errorCode);
				mi.setError_message(errorMessage);
				errMap.put(taskId, mi);

			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("Err case analyzing error:{}", normalErr);
			}

		}

		return errMap;
	}

}
