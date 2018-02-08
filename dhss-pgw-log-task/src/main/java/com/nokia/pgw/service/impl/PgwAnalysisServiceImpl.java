package com.nokia.pgw.service.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.pgw.Entry;
import com.nokia.pgw.service.PgwAnalysisService;
import com.nokia.pgw.service.expt.PgwRemoteDirNotMatchedException;
import com.nokia.pgw.settings.CustomSetting;
import com.nokia.pgw.task.PrepareAction;
import com.nokia.pgw.util.PgwAnalyseUtil;


@Service
public class PgwAnalysisServiceImpl implements PgwAnalysisService {
	private static final Logger LOGGER = LogManager.getLogger(PgwAnalysisServiceImpl.class);
	private static final String PGW_DETAIL_DATA_TABLE_NAME = "pgw_detail_data";
	private static final String PGW_XML_LOG_TABLE_NAME = "pgw_xml_log";
	private static final SimpleDateFormat PARTITION_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DAY_START_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
	private static final SimpleDateFormat DEBUG_LOG_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	private static final SimpleDateFormat PGW_LOG_TIME_STAMP = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat SHELL_NAME_FMT = new SimpleDateFormat("yyyyMMdd_HH");
	private static final String PATTERN_RULE = 
			"+ */\n" +
			"+ instance*/provgw-spml_command.*\n" +
			"- *";
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private CustomSetting customSetting;

	/**
	 * 
	 * ALTER TABLE pgw_xml_log PARTITION BY RANGE (TO_DAYS(response_time))(
	 * PARTITION p_20150502 VALUES less than (TO_DAYS('2017-05-03 00:00:00')) );
	 */
	@Override
	public void handlePartition() {
		createPartition();
		deletePartition();
	}
	
	public void debugLogger(String logInfo) {
		Date d = new Date();

		File debugLog = new File(customSetting.getPgwLogDeployDir() + "debug.log");
		try {
			if (!debugLog.exists()) {
				debugLog.createNewFile();
			}
			FileWriter fw = new FileWriter(debugLog, true);
			fw.write(DEBUG_LOG_TIME_FORMAT.format(d) + " --------> " + logInfo + "\n");
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.info("IO exception happened:cannot write debug log.");
		}

	}

	private void deletePartition() {
		String sql = "alter table #tableName# drop partition #partitionName#";
		Integer saveDays = customSetting.getSaveDays();
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, -saveDays);
		Date oldestDataTime = now.getTime();
		String partitionName = "p_" + PARTITION_NAME_FORMAT.format(oldestDataTime);
		String firstSql = sql.replace("#tableName#", PGW_DETAIL_DATA_TABLE_NAME);
		firstSql = firstSql.replace("#partitionName#", partitionName);
		String secondSql = sql.replace("#tableName#", PGW_XML_LOG_TABLE_NAME);
		secondSql = secondSql.replace("#partitionName#", partitionName);
		LOGGER.info(Entry.getLOGGER_HEAD()+"Delete old partition:"+partitionName);
		LOGGER.info(Entry.getLOGGER_HEAD()+firstSql);
		LOGGER.info(Entry.getLOGGER_HEAD()+secondSql);
		try {
			jdbcTemplate.execute(firstSql);
			jdbcTemplate.execute(secondSql);
			LOGGER.info("Delete completed.");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(Entry.getLOGGER_HEAD()+e.getMessage());
			LOGGER.info(Entry.getLOGGER_HEAD()+"Partition delete failed.");
		}
	}

	private void createPartition() {
		String sql = "alter table #tableName# add partition (partition #partitionName# values less than(TO_DAYS('#tomorrowStartTime#')))";
		Calendar now = Calendar.getInstance();
		Date todayDate = now.getTime();
		now.add(Calendar.DATE, +1);
		Date tomorrowDate = now.getTime();
		String partitionName = "p_" + PARTITION_NAME_FORMAT.format(todayDate);
		String lessThanDate = DAY_START_TIME_FORMAT.format(tomorrowDate);
		String firstSql = sql.replace("#tableName#", PGW_DETAIL_DATA_TABLE_NAME);
		firstSql = firstSql.replace("#partitionName#", partitionName);
		firstSql = firstSql.replace("#tomorrowStartTime#", lessThanDate);
		String secondSql = sql.replace("#tableName#", PGW_XML_LOG_TABLE_NAME);
		secondSql = secondSql.replace("#partitionName#", partitionName);
		secondSql = secondSql.replace("#tomorrowStartTime#", lessThanDate);
		LOGGER.info(Entry.getLOGGER_HEAD()+"Creating a new partition:"+partitionName);
		LOGGER.info(Entry.getLOGGER_HEAD()+firstSql);
		LOGGER.info(Entry.getLOGGER_HEAD()+secondSql);
		try {
			jdbcTemplate.execute(firstSql);
			jdbcTemplate.execute(secondSql);
			LOGGER.info(Entry.getLOGGER_HEAD()+"New partition has been created.");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(Entry.getLOGGER_HEAD()+"Partition creation failed.");
			LOGGER.info(Entry.getLOGGER_HEAD()+"SQL:");
			LOGGER.info(Entry.getLOGGER_HEAD()+firstSql);
			LOGGER.info(Entry.getLOGGER_HEAD()+secondSql);
		}
	}

	@Override
	public List<String> getRsyncInfo(String rsyncCmd) {
		List<String> rsyncInfo = new ArrayList<>();
		LOGGER.info(Entry.getLOGGER_HEAD()+"Try to execute rsync command:");
		LOGGER.info(Entry.getLOGGER_HEAD()+rsyncCmd);
		try {
			Process rsyncProcess = Runtime.getRuntime().exec(rsyncCmd);
			rsyncProcess.waitFor();
			InputStreamReader in = new InputStreamReader(rsyncProcess.getErrorStream());
			BufferedReader br = new BufferedReader(in);
			String callbackLine = null;
			if ((callbackLine = br.readLine()) != null) {
				LOGGER.info(Entry.getLOGGER_HEAD()+"Error info :");
				LOGGER.info(Entry.getLOGGER_HEAD()+"=======================================");
				while ((callbackLine = br.readLine()) != null) {
					rsyncInfo.add(callbackLine);
					LOGGER.info(Entry.getLOGGER_HEAD()+callbackLine);
				}
				LOGGER.info(Entry.getLOGGER_HEAD()+"=======================================");
				in.close();
				br.close();
			}

			in = new InputStreamReader(rsyncProcess.getInputStream());
			br = new BufferedReader(in);
			callbackLine = null;
			LOGGER.info(Entry.getLOGGER_HEAD()+"Callback info :");
			LOGGER.info(Entry.getLOGGER_HEAD()+"=======================================");
			while ((callbackLine = br.readLine()) != null) {
				if (callbackLine.contains(".gz")) {
					rsyncInfo.add(callbackLine);
					LOGGER.info(Entry.getLOGGER_HEAD()+callbackLine);
				}
			}
			LOGGER.info(Entry.getLOGGER_HEAD()+"=======================================");
		} catch (IOException e) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Rsync execution failed:IO FAILED");
			e.printStackTrace();
		} catch (InterruptedException e) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Rsync execution failed:RSYNC CMD INTERRUPTED.");
			e.printStackTrace();
		}
		return rsyncInfo;
	}

	@Override
	public void loadDataToDB() {
		File loadDetailFileDir = new File(PrepareAction.getLoaderDetailFileDir());
		File loadXmlFileDir = new File(PrepareAction.getLoaderXMLFileDir());
		File[] loadDetailFiles = loadDetailFileDir.listFiles();
		if (loadDetailFiles.length == 0) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"No detail data need to be loaded into DB");
		} else {
			for (File loadDetailFile : loadDetailFiles) {
				String loadDetailDataSQL = PrepareAction.getDetailDataLoadSQL().replace("#fileAbsPath#",
						loadDetailFile.getAbsolutePath());
				LOGGER.info(Entry.getLOGGER_HEAD()+"Load detail data file:" + loadDetailFile.getAbsolutePath());
				try {
					LOGGER.info(Entry.getLOGGER_HEAD()+"Execute detail data load SQL:");
					LOGGER.info(Entry.getLOGGER_HEAD()+loadDetailDataSQL);
					jdbcTemplate.execute(loadDetailDataSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		File[] loadXmlDataFiles = loadXmlFileDir.listFiles();
		if (loadXmlDataFiles.length == 0) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"No xml loader data need to be loaded into DB");
		} else {
			for (File loadXmlDataFile : loadXmlDataFiles) {
				String loadXMLDataSQL = PrepareAction.getXMLDataLoadSQL().replace("#fileAbsPath#",
						loadXmlDataFile.getAbsolutePath());
				LOGGER.info(Entry.getLOGGER_HEAD()+"Load xml data file:" + loadXmlDataFile.getAbsolutePath());
				try {
					LOGGER.info(Entry.getLOGGER_HEAD()+"Execute xml log data load SQL:");
					LOGGER.info(Entry.getLOGGER_HEAD()+loadXMLDataSQL);
					jdbcTemplate.execute(loadXMLDataSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Mainland method
	 */
	@Override
	public List<String> getAllRsyncCommandForMainland() {
		List<String> rsyncCmdList = new ArrayList<>();
		List<Map<String, Object>> deviceBasicInfoList = deviceBasicInfo();
		String yesterday = new DateTime().minusDays(1).toString("yyyy_MM_dd");
		for (Map<String, Object> deviceBasicInfoMap : deviceBasicInfoList) {
			String rsyncCmdPattern = customSetting.getRsyncCmdPattern();
			Boolean isDryRunMode = customSetting.isDryRunMode();
			String remotePgwLogBaseDir = customSetting.getRemotePgwLogBaseDir();
			LOGGER.info(Entry.getLOGGER_HEAD()+deviceBasicInfoMap);
			String password = deviceBasicInfoMap.get("password").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#password#", password);
			String userName = deviceBasicInfoMap.get("userName").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#userName#", userName);
			String pgwIp = deviceBasicInfoMap.get("pgwIp").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#pgwIp#", pgwIp);
			remotePgwLogBaseDir = remotePgwLogBaseDir.replace("#yyyy_MM_dd#", yesterday);
			rsyncCmdPattern = rsyncCmdPattern.replace("#pgwLogRemoteDir#", remotePgwLogBaseDir);
			String pgwName = deviceBasicInfoMap.get("pgwName").toString();
			String rsyncDataLocalDir = PrepareAction.getRsyncDataFileDir() + pgwName;
			File rsyncDataLocalDirFile = new File(rsyncDataLocalDir);
			if (!rsyncDataLocalDirFile.exists()) {
				LOGGER.info(Entry.getLOGGER_HEAD()+rsyncDataLocalDir);
				LOGGER.info(Entry.getLOGGER_HEAD()+"rsync-data dir not exist.mkdirs.");
				rsyncDataLocalDirFile.mkdirs();
			} else {
				LOGGER.info(Entry.getLOGGER_HEAD()+rsyncDataLocalDir);
				LOGGER.info(Entry.getLOGGER_HEAD()+"rsync-data dir exist.");
			}
			rsyncCmdPattern = rsyncCmdPattern.replace("#rsyncLocalDataDir#", rsyncDataLocalDir);
			if (isDryRunMode == true) {
				rsyncCmdPattern = rsyncCmdPattern.replace("#--dry-run#", "--dry-run");
			} else {
				rsyncCmdPattern = rsyncCmdPattern.replace(" #--dry-run# ", " ");
			}
			boolean isAccurateMode = customSetting.isAccurateSyncMode();
			if(isAccurateMode==true){
				String pgwRuleAbsPath = customSetting.getAccurateMatchRuleFileDir();
				rsyncCmdPattern = rsyncCmdPattern.replace("#pgw-rule#", pgwRuleAbsPath);
			}else{
				rsyncCmdPattern = rsyncCmdPattern.replace(" --include-from=#pgw-rule# --exclude-from=#pgw-rule# ", " ");
			}
			LOGGER.info(Entry.getLOGGER_HEAD()+"Generated a new rsync command:");
			LOGGER.info(Entry.getLOGGER_HEAD()+rsyncCmdPattern);
			rsyncCmdList.add(rsyncCmdPattern);
		
		}
		return rsyncCmdList;
	}
	/**
	 * Taiwan method
	 */
	@Override
	public List<String> getAllRsyncCommand() {

		List<String> rsyncCmdList = new ArrayList<>();
		List<Map<String, Object>> deviceBasicInfoList = deviceBasicInfo();
		for (Map<String, Object> deviceBasicInfoMap : deviceBasicInfoList) {
			String rsyncCmdPattern = customSetting.getRsyncCmdPattern();
			Boolean isDryRunMode = customSetting.isDryRunMode();
			String remotePgwLogBaseDir = customSetting.getRemotePgwLogBaseDir();
			LOGGER.info(Entry.getLOGGER_HEAD()+deviceBasicInfoMap);
			String password = deviceBasicInfoMap.get("password").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#password#", password);
			String userName = deviceBasicInfoMap.get("userName").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#userName#", userName);
			String pgwIp = deviceBasicInfoMap.get("pgwIp").toString();
			rsyncCmdPattern = rsyncCmdPattern.replace("#pgwIp#", pgwIp);
			String pgwName = deviceBasicInfoMap.get("pgwName").toString();
			remotePgwLogBaseDir = remotePgwLogBaseDir.replace("#pgw-dir#", pgwName+"_provision_log");
			rsyncCmdPattern = rsyncCmdPattern.replace("#pgwLogRemoteDir#", remotePgwLogBaseDir);
			
			String rsyncDataLocalDir = PrepareAction.getRsyncDataFileDir() + pgwName;
			File rsyncDataLocalDirFile = new File(rsyncDataLocalDir);
			if (!rsyncDataLocalDirFile.exists()) {
				LOGGER.info(Entry.getLOGGER_HEAD()+rsyncDataLocalDir);
				LOGGER.info(Entry.getLOGGER_HEAD()+"rsync-data dir not exist.mkdirs.");
				rsyncDataLocalDirFile.mkdirs();
			} else {
				LOGGER.info(Entry.getLOGGER_HEAD()+rsyncDataLocalDir);
				LOGGER.info(Entry.getLOGGER_HEAD()+"rsync-data dir exist.");
			}
			rsyncCmdPattern = rsyncCmdPattern.replace("#rsyncLocalDataDir#", rsyncDataLocalDir);
			if (isDryRunMode == true) {
				rsyncCmdPattern = rsyncCmdPattern.replace("#--dry-run#", "--dry-run");
			} else {
				rsyncCmdPattern = rsyncCmdPattern.replace(" #--dry-run# ", " ");
			}
			boolean isAccurateMode = customSetting.isAccurateSyncMode();
			if(isAccurateMode==true){
				String pgwRuleAbsPath = customSetting.getAccurateMatchRuleFileDir();
				rsyncCmdPattern = rsyncCmdPattern.replace("#pgw-rule#", pgwRuleAbsPath);
			}else{
				rsyncCmdPattern = rsyncCmdPattern.replace(" --include-from=#pgw-rule# --exclude-from=#pgw-rule# ", " ");
			}
			LOGGER.info(Entry.getLOGGER_HEAD()+"Generated a new rsync command:");
			LOGGER.info(Entry.getLOGGER_HEAD()+rsyncCmdPattern);
			rsyncCmdList.add(rsyncCmdPattern);
		}
		return rsyncCmdList;
	}

	@Override
	public List<Map<String, Object>> deviceBasicInfo() {
		String configuredInfo = customSetting.getPgwBasicInfo();
		String[] basicInfoPieces = configuredInfo.split(",");
		List<Map<String, Object>> pgwBasicInfoList = new ArrayList<>();
		for (String aBasicInfoPiece : basicInfoPieces) {
			Map<String, Object> aBasicInfoMap = new HashMap<>();
			String[] basicInfoArray = aBasicInfoPiece.split("-");
			String pgwName = basicInfoArray[0];
			aBasicInfoMap.put("pgwName", pgwName);
			String pgwIp = basicInfoArray[1];
			aBasicInfoMap.put("pgwIp", pgwIp);
			String userName = basicInfoArray[2];
			aBasicInfoMap.put("userName", userName);
			String password = basicInfoArray[3];
			aBasicInfoMap.put("password", password);
			pgwBasicInfoList.add(aBasicInfoMap);
		}
		return pgwBasicInfoList;
	}

	public Map<String, String> analysisPgwLogLineFront(String pgwLogLine) {
		Map<String, String> frontHalfTextAnalayseMap = new HashMap<>();
		try {
			String frontHalfLine = pgwLogLine.substring(0, pgwLogLine.indexOf("<"));
			frontHalfTextAnalayseMap.put("responseFrontHalf", frontHalfLine);
			String responseTime = frontHalfLine.substring(0, frontHalfLine.indexOf(","));
			frontHalfTextAnalayseMap.put("responseTime", responseTime);
			String[] frontHalfLineElements = frontHalfLine.split(" ");
			String pgwName = frontHalfLineElements[2];
			frontHalfTextAnalayseMap.put("pgwName", pgwName);
			String instanceName = frontHalfLineElements[3];
			frontHalfTextAnalayseMap.put("instanceName", instanceName);
			String userName = frontHalfLineElements[4];
			frontHalfTextAnalayseMap.put("userName", userName);
			String executionContent = frontHalfLineElements[5];
			frontHalfTextAnalayseMap.put("executionContent", executionContent);
			String pgwLogXmlPart = pgwLogLine.substring(pgwLogLine.indexOf("<"));
			frontHalfTextAnalayseMap.put("pgwLogXmlPart", pgwLogXmlPart);
		} catch (Exception e) {
			e.printStackTrace();
			debugLogger(e.getMessage());
			debugLogger(pgwLogLine);
		}
		return frontHalfTextAnalayseMap;
	}

	public Map<String, String> analysisPgwLogLineXml(String pgwLogLineBackHalf) {
		SAXReader reader = new SAXReader();
		StringReader in = new StringReader(pgwLogLineBackHalf);
		Map<String, String> pgwLogBackHalfAnalysisMap = new HashMap<>();
		try {
			Document doc = reader.read(in);
			OutputFormat formater = OutputFormat.createPrettyPrint();
			formater.setEncoding("UTF-8");
			StringWriter out = new StringWriter();
			XMLWriter writer = new XMLWriter(out, formater);
			writer.write(doc);
			writer.close();
			String formatedXml = out.toString();
			pgwLogBackHalfAnalysisMap.put("formatedXml", formatedXml);
			Element root = doc.getRootElement();
			String requestID = root.attribute("requestID").getText();
			pgwLogBackHalfAnalysisMap.put("requestID", requestID);
			String executionTime = root.attribute("executionTime").getText();
			pgwLogBackHalfAnalysisMap.put("executionTime", executionTime);
			String resultType = root.attribute("result").getText();
			pgwLogBackHalfAnalysisMap.put("resultType", resultType);
			String identifier = getIdentifier(pgwLogLineBackHalf);
			pgwLogBackHalfAnalysisMap.put("userNumber", identifier);
			String imsi = getImsi(pgwLogLineBackHalf);
			pgwLogBackHalfAnalysisMap.put("imsi", imsi);
			String msisdn = getMsisdn(pgwLogLineBackHalf);
			pgwLogBackHalfAnalysisMap.put("msisdn", msisdn);
			String errorCode = root.attributeValue("errorCode") != null ? root.attributeValue("errorCode") : "";
			pgwLogBackHalfAnalysisMap.put("errorCode", errorCode);
			String errorMessage = root.elementText("errorMessage") != null ? root.elementText("errorMessage") : "";
			pgwLogBackHalfAnalysisMap.put("errorMessage", errorMessage);
			String operation = getOperation(root);
			pgwLogBackHalfAnalysisMap.put("operation", operation);
		} catch (DocumentException e) {
			e.printStackTrace();
			LOGGER.info(Entry.getLOGGER_HEAD()+pgwLogLineBackHalf);
			debugLogger(e.getMessage());
			debugLogger(Entry.getLOGGER_HEAD()+pgwLogLineBackHalf);
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.info(Entry.getLOGGER_HEAD()+pgwLogLineBackHalf);
			debugLogger(e.getMessage());
			debugLogger(pgwLogLineBackHalf);
		} catch (Exception e) {
			e.printStackTrace();
			debugLogger(e.getMessage());
			debugLogger(pgwLogLineBackHalf);
		}
		return pgwLogBackHalfAnalysisMap;
	}

	private String getImsi(String xml) {
		//IMSI解析方法：
		//1.<imsi>标签
		//2.<identifier>标签,值为imsi
		//3.changeId请求 newID标签
		if(xml==null){
			return "0";
		}
		String imsi = "0";
		try { //获取IMSI标签
			imsi = xml.substring(xml.indexOf("<imsi>"), xml.indexOf("</imsi>"));
			imsi = imsi.replace("<imsi>", "");
			if(imsi.length()>1){
				return imsi;
			}else{
				imsi = "0";
			}
		} catch (Exception e) {
			imsi = "0";
		}
		imsi = getIdentifier(xml);
		if(!imsi.equals("0")&&imsi.startsWith(customSetting.getMmc())){
			return imsi;
		}else{
			imsi = "0";
		}
		if(xml.contains("changeId")&&xml.contains("alias=\"imsi\"")&&imsi.equals("0")){
			try {//changeId时，获取用户号码
				imsi = xml.substring(xml.indexOf("<newId"),xml.indexOf("</newId>"));
				imsi = imsi.substring(imsi.indexOf("\">")).replace("\">", "");
				if(imsi.length()>1){
					return imsi;
				}
			} catch (Exception e) {
				imsi = "0";
			}	
		}
		return imsi;
	}
	private static String getIdentifier(String xml){
		//identifier 查询方法：
		//1.<identifier></identifier>标签
		//2.<identifier alias="imsi"></identifier>标签
		//3.<identifier alias="imsi" xsi:type="subscriber:SubscriberIdentifier">标签
		if(xml==null){
			return "0";
		}
		String identifier = "0";
		try {
			identifier = xml.substring(xml.indexOf("<identifier"),xml.indexOf("</identifier>"));
			identifier = identifier.substring(identifier.indexOf(">")).replace(">", "");
			if(identifier.length()>1){
				return identifier;
			}else{
				return "0";
			}
			
		} catch (Exception e) {
			return "0";
		}
		
	}
	private static String getMsisdn(String xml){
		// msisdn解析方法：
		// 1.<alias name="msisdn" value="886986999999"/>
		// 2.<msisdn></msisdn>
		if(xml==null){
			return "0";
		}
		String msisdn = "0";
		try {
			msisdn = xml.substring(xml.indexOf("<msisdn>"), xml.indexOf("</msisdn>"));
			msisdn = msisdn.replace("<msisdn>", "");
		} catch (Exception e) {
			msisdn = "0";
		}
		if (msisdn.length() > 1) {
			return msisdn;
		}
		try {
			msisdn = xml.substring(xml.indexOf("<alias name=\"msisdn\" value=\""), xml.indexOf("\"/>"))
					.replace("<alias name=\"msisdn\" value=\"", "");
		} catch (Exception e) {
			msisdn = "0";
		}
		if (msisdn.length() > 1) {
			return msisdn;
		}
		// changeId操作时，alias = "msisdn"
		if (xml.contains("changeId") && xml.contains("alias=\"msisdn\"") && msisdn.equals("0")) {
			try {// changeId时，获取用户号码
				msisdn = xml.substring(xml.indexOf("<newId"), xml.indexOf("</newId>"));
				msisdn = msisdn.substring(msisdn.indexOf("\">")).replace("\">", "");
				if (msisdn.length() > 1) {
					return msisdn;
				}
			} catch (Exception e) {
				msisdn = "0";
			}

		}
		if (msisdn.length() > 1) {
			return msisdn;
		} else {
			return "0";
		}

	}
	private static String getOperation(Element root){
		String operation = "";
		Element modificationNode = root.element("modification");
		Element operationNode = root.element("operation");
		if (modificationNode != null) {
			operation = modificationNode.attribute("operation").getText();
			return operation;
		} else if(operationNode != null) {
			operation = operationNode.attribute("type").getText();
			return operation;
		}else{
			operation = root.getName().replace("Response", "");
			if(!StringUtils.isNotBlank(operation)){
				operation = "";
			}
			return operation;
		}
	
	}
	@Override
	public void analysisTargetFile() {
		File uncompressedDir = new File(PrepareAction.getUncompressedFileDir());
		File[] uncompressedFiles = uncompressedDir.listFiles();
		for (File uncompressedFile : uncompressedFiles) {
			try {
				LOGGER.info(Entry.getLOGGER_HEAD()+"Analysis target:" + uncompressedFile.getAbsolutePath());
				analysisSingleFile(uncompressedFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void analysisSingleFile(File pgwLog) throws IOException, DocumentException{

		FileReader fr = new FileReader(pgwLog);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		List<Map<String, String>> dataList = new ArrayList<>();
		Map<String, String> requestIdAndXmlMap = new HashMap<>();
		while ((line = br.readLine()) != null) {
			boolean isResponseLine = judgeResponse(line);
			boolean isRequestLine = judgeRequest(line);
			if (isRequestLine == true){
				requestIdAndXmlMap.putAll(getRequestIdAndXmlMap(line));
			}
			if (isResponseLine == true) {
				try {
					Map<String, String> dataMap = new HashMap<>();
					dataMap = analysisPgwLogLineFront(line);
					String xmlPart = dataMap.get("pgwLogXmlPart").toString();
					dataMap.putAll(analysisPgwLogLineXml(xmlPart));
					dataList.add(dataMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		br.close();
		for (Map<String, String> dataMap : dataList) {
			String requestId = dataMap.get("requestID");
			String responseFrontHalfLine = dataMap.get("responseFrontHalf");
			String formatedRequestXml = requestIdAndXmlMap.get(requestId);
			String formatedResponseXml = dataMap.get("formatedXml");
			if(StringUtils.isNotBlank(requestId)&&StringUtils.isNotBlank(formatedRequestXml)&&StringUtils.isNotBlank(formatedResponseXml)){
				String outPutXml = "Request LOG:\n"+formatedRequestXml+"\nResponse LOG:\n"+responseFrontHalfLine+"\n"+formatedResponseXml;
				dataMap.put("formatedXml", outPutXml);
			}
			String identifier = dataMap.get("userNumber");
			if(identifier.equals("0")){//在response里，没有解析出identifier,就从request里查询
				identifier = getIdentifier(formatedRequestXml);
				dataMap.put("userNumber", identifier);
			}
			String imsi = dataMap.get("imsi");
			if(imsi.equals("0")){//在response里，没有解析出imsi,就从request里查询
				imsi = getImsi(formatedRequestXml);
				dataMap.put("imsi", imsi);
			}
			String msisdn = dataMap.get("msisdn");
			if(msisdn.equals("0")){//在response里，没有解析出msisdn,就从request里查询
				msisdn = getMsisdn(formatedRequestXml);
				dataMap.put("msisdn", msisdn);
			}
			if(identifier.equals(imsi)||identifier.equals(msisdn)){
				dataMap.put("userNumber","");
			}
			imsi = dataMap.get("imsi");
			identifier = dataMap.get("userNumber");
			// identifier有数据，msisdn没数据（或者imsi没数据）,并且identifier的数据看起来是msisdn(或者imsi)，赋值给他们
			if(imsi.equals("0") && identifier.startsWith(customSetting.getMmc())){
				dataMap.put("imsi",identifier);
			}
			msisdn = dataMap.get("msisdn");
			if(msisdn.equals("0") && identifier.startsWith(customSetting.getCountryCode())){
				dataMap.put("msisdn",identifier);
			}
		}
		
		LOGGER.info(Entry.getLOGGER_HEAD()+"Data analysis completed.Data record number:" + dataList.size());
		writeLoaderFile(pgwLog.getName(), dataList);
	}

	private Map<String, String> getRequestIdAndXmlMap(String line) throws DocumentException, IOException {
		Map<String, String> requestIdAndXmlMap = new HashMap<>();
		String requestFrontHalfLine = line.substring(0, line.indexOf("<"));
		String lineXmlPart = line.substring(line.indexOf("<"));
		SAXReader reader = new SAXReader();
		StringReader in = new StringReader(lineXmlPart);
		Document doc = reader.read(in);
		OutputFormat formater = OutputFormat.createPrettyPrint();
		formater.setEncoding("UTF-8");
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, formater);
		writer.write(doc);
		writer.close();
		String formatedXml = out.toString();
		Element root = doc.getRootElement();
		String requestID = root.attribute("requestID").getText();
		requestIdAndXmlMap.put(requestID, requestFrontHalfLine+"\n"+formatedXml);
		
		return requestIdAndXmlMap;
	}

	private boolean judgeResponse(String line) {
		if (line.contains("<spml:modifyResponse") 
		 || line.contains("<spml:addResponse")
		 || line.contains("<spml:deleteResponse") 
		 || line.contains("<spml:extendedResponse") 
		 || line.contains("<spml:changeIdResponse")) {
			return true;
		} else {
			return false;
		}
	}
	private boolean judgeRequest(String line) {
		if (line.contains("<spml:modifyRequest") 
		 || line.contains("<spml:addRequest")
		 || line.contains("<spml:deleteRequest") 
		 || line.contains("<spml:extendedRequest") 
		 || line.contains("<spml:changeIdRequest")) {
			return true;
		} else {
			return false;
		}
	}
	
	private void writeLoaderFile(String pgwLogName, List<Map<String, String>> dataList) {
		LOGGER.info(Entry.getLOGGER_HEAD()+"Data analysis completed.Start to write loader file.");
		try {
			writeDetailDataFile(pgwLogName, dataList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writeXmlLogFile(pgwLogName, dataList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info(Entry.getLOGGER_HEAD()+"All loader files written.");
	}

	private void writeXmlLogFile(String pgwLogName, List<Map<String, String>> dataList) throws IOException {
		LOGGER.info(Entry.getLOGGER_HEAD()+"Writing a new xml log table loader file...");
		StringBuilder loader = new StringBuilder();
		for (Map<String, String> dataMap : dataList) {
			String responseTime = dataMap.get("responseTime");
			String requestID = dataMap.get("requestID");
			String xmlLog = dataMap.get("formatedXml");
			loader.append(responseTime + PgwAnalyseUtil.getFieldTerminator());
			loader.append(requestID + PgwAnalyseUtil.getFieldTerminator());
			loader.append(xmlLog + PgwAnalyseUtil.getLineTerminator());
		}
		File loaderFile = new File(PrepareAction.getLoaderXMLFileDir() + pgwLogName + ".xmlloader");
		LOGGER.info(Entry.getLOGGER_HEAD()+"Write data as:"+loaderFile.getAbsolutePath());
		loaderFile.createNewFile();
		FileWriter fw = new FileWriter(loaderFile);
		fw.write(loader.toString());
		fw.close();
		LOGGER.info(Entry.getLOGGER_HEAD()+"Writing completed!Path:" + loaderFile.getAbsolutePath() + ",size:"
				+ loaderFile.length() / 1024.0 / 1024.0 + "Mb");
	}

	private void writeDetailDataFile(String pgwLogName, List<Map<String, String>> dataList) throws IOException {
		LOGGER.info(Entry.getLOGGER_HEAD()+"Writing a new detail data table loader file...");
		StringBuilder loader = new StringBuilder();
		for (Map<String, String> dataMap : dataList) {
			String responseTime = dataMap.get("responseTime");
			String requestID = dataMap.get("requestID");
			String pgwName = dataMap.get("pgwName");
			String instanceName = dataMap.get("instanceName");
			String userName = dataMap.get("userName");
			String imsi = dataMap.get("imsi");
			String msisdn = dataMap.get("msisdn");
			String executionTime = dataMap.get("executionTime");
			String executionContent = dataMap.get("executionContent");
			String resultType = dataMap.get("resultType");
			String operation = dataMap.get("operation");
			String errorCode = dataMap.get("errorCode");
			String errorMessage = dataMap.get("errorMessage");
			String identifier = dataMap.get("userNumber");
			loader.append(responseTime + PgwAnalyseUtil.getFieldTerminator());
			loader.append(requestID + PgwAnalyseUtil.getFieldTerminator());
			loader.append(pgwName + PgwAnalyseUtil.getFieldTerminator());
			loader.append(instanceName + PgwAnalyseUtil.getFieldTerminator());
			loader.append(userName + PgwAnalyseUtil.getFieldTerminator());
			loader.append(executionTime + PgwAnalyseUtil.getFieldTerminator());
			loader.append(executionContent + PgwAnalyseUtil.getFieldTerminator());
			loader.append(resultType + PgwAnalyseUtil.getFieldTerminator());
			loader.append(operation + PgwAnalyseUtil.getFieldTerminator());
			loader.append(identifier + PgwAnalyseUtil.getFieldTerminator());
			loader.append(imsi + PgwAnalyseUtil.getFieldTerminator());
			loader.append(msisdn + PgwAnalyseUtil.getFieldTerminator());
			loader.append(errorCode + PgwAnalyseUtil.getFieldTerminator());
			loader.append(errorMessage + PgwAnalyseUtil.getLineTerminator());
		}
		File loaderFile = new File(PrepareAction.getLoaderDetailFileDir() + pgwLogName + ".detail");
		LOGGER.info(Entry.getLOGGER_HEAD()+"Write data as:"+loaderFile.getAbsolutePath());
		loaderFile.createNewFile();
		FileWriter fw = new FileWriter(loaderFile);
		fw.write(loader.toString());
		fw.close();
		LOGGER.info(Entry.getLOGGER_HEAD()+"Writing completed!Path:" + loaderFile.getAbsolutePath() + ",size:"
				+ loaderFile.length() / 1024.0 / 1024.0 + "Mb");
	}

	@Override
	public void clearTempFile() {
		File uncompressedDir = new File(PrepareAction.getUncompressedFileDir());
		File xmlLoaderDir = new File(PrepareAction.getLoaderXMLFileDir());
		File detailLoaderDir = new File(PrepareAction.getLoaderDetailFileDir());
		File rsyncShellDir = new File(PrepareAction.getRsyncShellDir());
		File[] uncompressedFiles = uncompressedDir.listFiles();
		File[] xmlLoaderFiles = xmlLoaderDir.listFiles();
		File[] detailLoaderFiles = detailLoaderDir.listFiles();
		File[] rsyncShellFiles = rsyncShellDir.listFiles();
		for (File file : uncompressedFiles) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Delete uncompressed file:" + file.getAbsolutePath());
			file.delete();
		}
		for (File file : xmlLoaderFiles) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Delete xmlLoader file:" + file.getAbsolutePath());
			file.delete();
		}
		for (File file : detailLoaderFiles) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Delete detailLoader file:" + file.getAbsolutePath());
			file.delete();
		}
		for (File file : rsyncShellFiles) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"Delete shell file:" + file.getAbsolutePath());
			file.delete();
		}
	}

	@Override
	public void handleHalfAutoMode() {
		
	}

	private static File twPgwLogConverter(File twPgwLog) throws IOException {
		List<String> lines = FileUtils.readLines(twPgwLog);
		String dateStrInFirstLine = lines.get(0).split(" ")[0];
		Date primaryDate = null;
		try {
			primaryDate = PGW_LOG_TIME_STAMP.parse(dateStrInFirstLine);
		} catch (ParseException e) {
			primaryDate = new Date();
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(primaryDate);
		c.add(Calendar.DATE, +1);
		Date dayAfterPrimaryDate = c.getTime();
		String dayAfterPrimaryDateStr = PGW_LOG_TIME_STAMP.format(dayAfterPrimaryDate);
		String logContext = FileUtils.readFileToString(twPgwLog);
		logContext = logContext.replaceAll("\n", "");
		logContext = logContext.replaceAll("\r", "");
		logContext = logContext.replaceAll(dateStrInFirstLine+" ", "\n"+dateStrInFirstLine+" ");//根据日志时间戳
		logContext = logContext.replaceAll(dayAfterPrimaryDateStr+" ", "\n"+dayAfterPrimaryDateStr+" ");//根据日志时间戳
		File convertedLog = new File(twPgwLog.getAbsoluteFile()+"_cvt");
		FileUtils.writeStringToFile(convertedLog, logContext);
		LOGGER.info(Entry.getLOGGER_HEAD()+"Convert file:"+twPgwLog.getName()+" --> "+convertedLog.getName());
		twPgwLog.delete();
		return convertedLog;
		
	}

	private Map<String,File> writeShell(List<String> rsyncCmdList) throws IOException {
		Date d = new Date();
		String timeInName = SHELL_NAME_FMT.format(d);
		Map<String,File> shellCmdMap = new HashMap<>();
		for (int i = 0;i<rsyncCmdList.size();i++) {
			File shellFile = new File(PrepareAction.getRsyncShellDir()+"/"+timeInName+"_"+i+".sh");
			FileWriter fw = new FileWriter(shellFile);
			fw.write(rsyncCmdList.get(i));
			fw.close();
			shellCmdMap.put(rsyncCmdList.get(i), shellFile);
		}
		return shellCmdMap;
	}
	@Override
	public void rsyncCommandDryRunModeTester() throws IOException, InterruptedException {
		LOGGER.info(Entry.getLOGGER_HEAD()+"********************");
		LOGGER.info(Entry.getLOGGER_HEAD()+"*                  *");
		LOGGER.info(Entry.getLOGGER_HEAD()+"*   DHSS-PGW-LOG   *");
		LOGGER.info(Entry.getLOGGER_HEAD()+"*   DRY-RUN MODE   *");
		LOGGER.info(Entry.getLOGGER_HEAD()+"*                  *");
		LOGGER.info(Entry.getLOGGER_HEAD()+"********************");
		List<String> rsyncCommnadList = getAllRsyncCommand();
		Map<String,File> shellCmdMap = writeShell(rsyncCommnadList);
		List<String> resultList = executeRsyncShellAndgetRsyncDataFileDir(shellCmdMap);
		LOGGER.info(Entry.getLOGGER_HEAD()+"should analysis:");
		for (String resultDir : resultList) {
			LOGGER.info(Entry.getLOGGER_HEAD()+resultDir);
		}
	}
	@Override
	public List<File> getPgwSpmlFileList() throws IOException, InterruptedException, PgwRemoteDirNotMatchedException {
		
		String remoteDir = customSetting.getRemotePgwLogBaseDir();
		List<String> allRsyncCommandList = new ArrayList<>();
		if(remoteDir.equals("/tmp/#pgw-dir#/")){
			LOGGER.info("PGW Remote dir -> Taiwan remote dir :/tmp/#pgw-dir#/");
			allRsyncCommandList = getAllRsyncCommand();//获取命令,台湾目前的路径
		}else if(remoteDir.equals("/srv/backup/#yyyy_MM_dd#/")){
			LOGGER.info("PGW Remote dir -> Mainland remote dir :/tmp/#pgw-dir#/");
			allRsyncCommandList = getAllRsyncCommandForMainland();//获取命令,台湾目前的路径
		}else{
			LOGGER.info("PGW Remote dir -> Mainland remote dir :/tmp/#pgw-dir#/");
			throw new PgwRemoteDirNotMatchedException();
		}
		
		Map<String,File> shellCmdMap = writeShell(allRsyncCommandList);//获取命令-脚本文件Map
		List<String> resultList = executeRsyncShellAndgetRsyncDataFileDir(shellCmdMap);
		String uncompressedDir = PrepareAction.getUncompressedFileDir();
		List<File> uncompressedRsyncFile = new ArrayList<>();
		for (String targetFileAbsPath : resultList) {
			File anUncompressFile = PgwAnalyseUtil.uncompressGzFile(targetFileAbsPath, uncompressedDir);//解压到目标路径
			uncompressedRsyncFile.add(anUncompressFile);
		}
		//转换文件
		List<File> analysisLogs = new ArrayList<>();
		for (File originTwLog : uncompressedRsyncFile) {
			File convertedLog = twPgwLogConverter(originTwLog);
			analysisLogs.add(convertedLog);
		}
		return analysisLogs;
	
	}
	private List<String> executeRsyncShellAndgetRsyncDataFileDir(Map<String,File> shellCmdMap) throws IOException, InterruptedException{
		Set<String> rsyncCmdSet = shellCmdMap.keySet();
		List<String> callbackInfo = new ArrayList<>();
		for (String rsyncCmd : rsyncCmdSet) {
			LOGGER.info(Entry.getLOGGER_HEAD()+"command:\n"+rsyncCmd);
			LOGGER.info(Entry.getLOGGER_HEAD()+"Shell context:\n"+FileUtils.readFileToString(shellCmdMap.get(rsyncCmd)));
			LOGGER.info(Entry.getLOGGER_HEAD()+"Shell abs path:"+shellCmdMap.get(rsyncCmd).getAbsolutePath());
			File shellFile = shellCmdMap.get(rsyncCmd);
			Process p = Runtime.getRuntime().exec("sh "+shellFile.getAbsolutePath());
			p.waitFor();
			String line = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			LOGGER.info(Entry.getLOGGER_HEAD()+"Input stream");
			LOGGER.info(Entry.getLOGGER_HEAD()+"===============================================");
			while(null!=(line=br.readLine())){
				LOGGER.info(Entry.getLOGGER_HEAD()+line);
				if(line.startsWith("instance")&&line.contains("provgw-spml_command.log")&&!line.contains("delete")&&line.contains(".gz")){
					String frontHalfFilePath = rsyncCmd.split(":")[1].split(" ")[1];
					if(!frontHalfFilePath.endsWith("/")){
						frontHalfFilePath+="/";
					}
					callbackInfo.add(frontHalfFilePath+line);
				}
			}
			LOGGER.info(Entry.getLOGGER_HEAD()+"===============================================");
			br.close();
			LOGGER.info(Entry.getLOGGER_HEAD()+"Error stream");
			LOGGER.info(Entry.getLOGGER_HEAD()+"***********************************************");
			BufferedReader br_ = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			line = null;
			while(null!=(line=br_.readLine())){
				LOGGER.info(line);
			}
			br_.close();
			LOGGER.info(Entry.getLOGGER_HEAD()+"***********************************************");
			p.waitFor();
			p.destroy();
			LOGGER.info(Entry.getLOGGER_HEAD()+"Shell for rsync command executed completely.Destroy this process.");
		}
		return callbackInfo;

	}
	@Override
	public void manualMode() {
		File manualDir = new File(customSetting.getManualDir());
		if(!manualDir.exists()){
			LOGGER.info("Man dir :"+manualDir+" doesn't exist!");
			return;
		}
		// uncompress all file.
		LOGGER.info("Manual MODE:Start to uncompress files.");
		File[] manualFileList = manualDir.listFiles();
		List<File> uncompressedList = new ArrayList<>();
		for (File manFile : manualFileList) {
			if(manFile.getName().contains("cvt")){
				manFile.delete();
				continue;
			}
			if(manFile.getName().contains("provgw-spml_command.log")){
				if(manFile.getName().endsWith(".gz")){
					try {
						File f = PgwAnalyseUtil.uncompressGzFile(manFile,manFile.getParent()+"/");
						uncompressedList.add(f);
						LOGGER.info("Manual MODE: uncompress progress:"+manFile.getAbsolutePath()+" -> "+f.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					uncompressedList.add(manFile);
					LOGGER.info("Manual MODE:add uncompress file:"+manFile.getAbsolutePath());

				}
			}
		}
		// convert all file
		List<File> manCvtFileList = new ArrayList<>();
		for (File manFile : uncompressedList) {
			try {
				File f = twPgwLogConverter(manFile);
				manCvtFileList.add(f);
				LOGGER.info("Manual MODE:Convert progress:"+manFile.getAbsolutePath()+" -> "+f.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//analysis and load to DB
		for (File manCvtFile : manCvtFileList) {
			try {
				analysisSingleFile(manCvtFile);
			} catch (IOException | DocumentException e) {
				e.printStackTrace();
			}
		}
		loadDataToDB();
	}

	@Override
	public void checkMatchPatternFile() throws IOException {
		File patternFile = new File(customSetting.getAccurateMatchRuleFileDir());
		if(!patternFile.exists()){
			LOGGER.info(Entry.getLOGGER_HEAD() + " pattern file not exist.Create a new one.");
			patternFile.createNewFile();
			FileUtils.writeStringToFile(patternFile, PATTERN_RULE);
			LOGGER.info(Entry.getLOGGER_HEAD() + " pattern file created.");
		}else{
			LOGGER.info(Entry.getLOGGER_HEAD() + " pattern file exists.");
		}
		String patternRule = FileUtils.readFileToString(patternFile);
		LOGGER.info(patternRule);
	}
}
