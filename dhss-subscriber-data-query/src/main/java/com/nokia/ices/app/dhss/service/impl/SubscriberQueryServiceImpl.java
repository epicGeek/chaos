package com.nokia.ices.app.dhss.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.SOAPException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.nokia.ices.app.config.SubscriberDataSetting;
import com.nokia.ices.app.dhss.bean.BatchTemplate;
import com.nokia.ices.app.dhss.domain.AnalysisTemplateConfig;
import com.nokia.ices.app.dhss.domain.PgwAccessPoint;
import com.nokia.ices.app.dhss.domain.SubscriberDataDefineRuleTab;
import com.nokia.ices.app.dhss.domain.UploadAnalysisTemplate;
import com.nokia.ices.app.dhss.domain.UserDataLog;
import com.nokia.ices.app.dhss.domain.UserDataLogMulti;
import com.nokia.ices.app.dhss.exception.IllegalSubscriberCodeException;
import com.nokia.ices.app.dhss.repository.AnalysisTemplateConfigRepository;
import com.nokia.ices.app.dhss.repository.UserDataLogMultiRepository;
import com.nokia.ices.app.dhss.repository.UserDataLogRepository;
import com.nokia.ices.app.dhss.service.SubscriberQueryService;
import com.nokia.ices.app.dhss.utils.ExportExcel;
import com.nokia.ices.app.dhss.utils.SoapConnectionHelper;
import com.nokia.ices.app.dhss.utils.SoapMessageHelper;
import com.nokia.ices.app.dhss.utils.SubDataSaxHandler;

/**
 * 
 * @author Pei Nan
 *
 */
@EnableAsync
@Service
public class SubscriberQueryServiceImpl implements SubscriberQueryService {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat sdfName = new SimpleDateFormat("yyyyMMddHHmm");
	private static final Logger logger = LoggerFactory.getLogger(SubscriberQueryServiceImpl.class);
	private static File singleAnalysisTemplate = null;
	private static File batchAnalysisTemplate = null;
	@Autowired
	UserDataLogRepository userDataLogRepository;
	@Autowired
	UserDataLogMultiRepository userDataLogMultiRepository;
	@Autowired
	AnalysisTemplateConfigRepository analysisTemplateConfigRepository;
	@Autowired
	private SubscriberDataSetting subscriberDataSetting;
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public Map<String, Object> getSingleSubscriberData(String subscriberNumber, String userName)
			throws DocumentException {
		// 针对台湾TST项目，由于只有1台PGW（不含备用），通过号码，直接到这台PGW的WEBSERVICE进行查询
		List<PgwAccessPoint> pgwAccessPointList = subscriberDataSetting.getPgwList();
		Map<String, Object> queryResultMap = new HashMap<>();
		
		try {
			// 获取输入号码类型
			String subscriberNumberType = determineQueryType(subscriberNumber);
			subscriberNumber = getQueryNumber(subscriberNumber,subscriberNumberType);
			if(subscriberDataSetting.getShowSoapLog()){
				logger.info("number:"+subscriberNumber + ",type:"+subscriberNumberType);	
			}
			// 获取连接对象
			Map<String, Object> requestObjectMap = SoapConnectionHelper
					.getSubscriberDataRequestObject(subscriberNumberType, subscriberNumber );
			for (PgwAccessPoint pgwAccessPoint : pgwAccessPointList) {
				String webServiceUrl = pgwAccessPoint.getWsUrl();
				String responseInfo = SoapConnectionHelper.getSearchResponseXml(requestObjectMap, webServiceUrl);

				if (responseInfo.startsWith("Error")) {
					// SOAP请求失败，处理方法：进入下一循环
					continue;
				} else {
					// SOAP请求成功，判断返回的报文是否包含有效数据
					String formatedResponseXml = formatXml(responseInfo);
					boolean isFileValidate = isResponseXmlValidate(formatedResponseXml);
					if (isFileValidate) {
						// 写文件,记录入库
						File singleUserXml = writeFile(formatedResponseXml, subscriberNumber);
						UserDataLog userDataLog = new UserDataLog();
						userDataLog.setPath(singleUserXml.getAbsolutePath());
						userDataLog.setCreateTime(new Date());
						userDataLog.setNumberSection(subscriberNumber);
						userDataLog.setUnitName(pgwAccessPoint.getPgwName());
						userDataLog.setCreateName(userName);
						userDataLogRepository.save(userDataLog);

						// 开始解析文件
						String templateAbsPath = getInUseTemplate("single").getAbsolutePath();// in
																								// use
																								// single
																								// template.
						List<SubscriberDataDefineRuleTab> resultList = SoapMessageHelper
								.querySubscriberDataFromXMLPath(templateAbsPath, singleUserXml.getAbsolutePath());

						queryResultMap.put("fromPGW", pgwAccessPoint.getPgwName());
						queryResultMap.put("data", resultList);
						break;
					} else {
						// 判定文件无效，重新查询
						continue;
					}

				}
			}
		} catch (IllegalSubscriberCodeException e) {
			e.getMessage();
			queryResultMap.put("fromPGW", IllegalSubscriberCodeException.MESSAGE);
			queryResultMap.put("data", "");
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			queryResultMap.put("fromPGW", e.getMessage());
			queryResultMap.put("data", "");
		} catch (SOAPException e) {
			e.printStackTrace();
			queryResultMap.put("fromPGW", e.getMessage());
			queryResultMap.put("data", "");
		} catch (IOException e) {
			e.printStackTrace();
			queryResultMap.put("fromPGW", e.getMessage());
			queryResultMap.put("data", "");
		} finally {
			if (!queryResultMap.containsKey("fromPGW")) {
				queryResultMap.put("fromPGW", "This IMSI/MSISDN was not found in any PGW");
				queryResultMap.put("data", "");
			}
		}
		return queryResultMap;
	}

	private String getQueryNumber(String subscriberNumber, String subscriberNumberType) {
		String countryCode = subscriberDataSetting.getCountryCode();
		if(subscriberNumberType.equals(QUERY_TYPE_IMSI)){
			return subscriberNumber;
		}else{  //MSISDN
			if(subscriberNumber.startsWith(countryCode)){
				return subscriberNumber;
			}else{
				return countryCode + subscriberNumber;
			}
		}
		
		
	}

	private boolean isResponseXmlValidate(String responseXml) {
		if (responseXml.contains("errorMessage")) {
			return false;
		}
		return true;
	}

	private File writeFile(String fileContent, String subscriberNumber) throws IOException {

		String singleSubscriberWriteXmlDir = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getSingleQueryResponseXmlDir() + sdf.format(new Date()) + File.separator;
		String fileName = "single-userdata-" + sdfName.format(new Date()) + "-" + subscriberNumber + ".xml";
		File todayDir = new File(singleSubscriberWriteXmlDir);
		if (!todayDir.exists()) {
			todayDir.mkdirs();
		}
		File xmlFile = new File(singleSubscriberWriteXmlDir + fileName);
		xmlFile.createNewFile();
		FileWriter fw = new FileWriter(xmlFile);
		fw.write(fileContent);
		fw.close();
		return xmlFile;
	}

	@Override
	public Map<String, Object> getMockQueryData(String subscriberNumber) {

		String fileAbsPath = "E:/userData.xml";
//		String templateAbsPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
//				+ subscriberDataSetting.getXmlAnalysisFileTemplateAbsPath();
		String templateAbsPath = getInUseTemplate("single").getAbsolutePath();
		List<SubscriberDataDefineRuleTab> resultList = SoapMessageHelper.querySubscriberDataFromXMLPath(templateAbsPath,
				fileAbsPath);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fromPGW", subscriberNumber + " exists on TestPGW");
		resultMap.put("data", resultList);
		return resultMap;
	}

	/**
	 * 判断是imsi还是msisdn
	 * 
	 * @param subscriberNumber
	 * @return
	 */
	private String determineQueryType(String subscriberNumber) throws IllegalSubscriberCodeException {
		String mmc = subscriberDataSetting.getMmc();
		if (subscriberNumber.startsWith(mmc)) {
			return QUERY_TYPE_IMSI;
		}else{
			return QUERY_TYPE_MSISDN;
		}
	}

	public static String formatXml(String unformatedXml) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		StringReader in = new StringReader(unformatedXml);
		Document doc = reader.read(in);
		OutputFormat formater = OutputFormat.createPrettyPrint();
		formater.setEncoding("UTF-8");
		StringWriter out = new StringWriter();
		XMLWriter writer = new XMLWriter(out, formater);
		writer.write(doc);
		writer.close();
		String formatedXml = out.toString();
		return formatedXml;
	}

	@Override
	public Map<String, Object> readCachedFile(String filePath, String subscriberNumber, String unitName) {
//		String templateAbsPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
//				+ subscriberDataSetting.getXmlAnalysisFileTemplateAbsPath();
		String templateAbsPath = getInUseTemplate("single").getAbsolutePath();
		List<SubscriberDataDefineRuleTab> resultList = SoapMessageHelper.querySubscriberDataFromXMLPath(templateAbsPath,
				filePath);

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fromPGW", unitName);
		resultMap.put("data", resultList);
		return resultMap;
	}

	@Override
	public List<String> getNumberArrayFromUploadExcel(MultipartFile multiQueryTemplate) {
		List<String> numberList = new ArrayList<>();
		try {
			numberList = readXls(multiQueryTemplate.getInputStream());
			for (int i = 0; i < numberList.size(); i++) {
				if (numberList.get(i).trim().length() == 0 || numberList.get(i) == null) {
					numberList.remove(i);
				}
			}
			return numberList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	@Override
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		String templateAbsPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getDownloadExcelAbsPath();
		File templateExcel = new File(templateAbsPath);
		downloadUtils(templateExcel, request, response);
	}

	@Override
	public Page<UserDataLog> getAllUserDataLog(Pageable pageable) {
		Page<UserDataLog> page = userDataLogRepository.findAllByOrderByCreateTimeDesc(pageable);
		return page;
	}

	@Override
	public Page<UserDataLogMulti> getAllUserDataLogMulti(Pageable pageable) {
		Page<UserDataLogMulti> page = userDataLogMultiRepository.findAllByOrderByCreateTimeDesc(pageable);
		return page;
	}

	private void downloadUtils(File downloadFile, HttpServletRequest request, HttpServletResponse response) {
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(downloadFile.getAbsolutePath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void downloadUserDataLog(String filePath, HttpServletRequest request, HttpServletResponse response) {
		File f = new File(filePath);
		downloadUtils(f, request, response);
	}

	@SuppressWarnings("resource")
	private static List<String> readXls(InputStream is) throws IOException {
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		List<String> list = new ArrayList<String>();
		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
			HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
			if (hssfSheet == null) {
				continue;
			}
			// 循环行Row
			for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
				HSSFRow hssfRow = hssfSheet.getRow(rowNum);
				if (hssfRow != null) {
					HSSFCell no = hssfRow.getCell(0);
					String value = getValue(no);
					if (" ".equals(value)) {
						continue;
					}
					list.add(value);
				}
			}
		}
		return list;
	}

	@SuppressWarnings("deprecation")
	private static String getValue(HSSFCell hssfCell) {

		if (null != hssfCell) {
			if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				// 返回布尔类型的值
				return String.valueOf(hssfCell.getBooleanCellValue());
			} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				// 返回数值类型的值

				hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
				String value = String.valueOf(hssfCell.getStringCellValue());
				return value;
				// return String.valueOf(hssfCell.getNumericCellValue());
			} else {
				// 返回字符串类型的值
				return String.valueOf(hssfCell.getStringCellValue());
			}
		}

		return null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean handleMultiSubscriberUserBySax(List<String> numberList, String userName)
			throws IOException, ParserConfigurationException, SAXException {
		// 1.过滤不合法输入号码。
		logger.info("~~~~~~~~~~~Method: SAX ~~~~~~~~~~~~~");
		logger.info("1.start to filter illegal number");
		Integer searchLimit = subscriberDataSetting.getSearchLimit();
		if (numberList.size() >= searchLimit) {
			logger.info("Too many sub. numbers:" + searchLimit + ".The search limit is:" + searchLimit);
			return false;
		}
		// 2.对合法号码进行查询
		logger.info("2.request data from PGW web service");
		Long startRequest = System.currentTimeMillis();
		Map<String, Object> responseMap = getResponseDataInputStream(numberList);
		File combinedFile = (File) responseMap.get("file");
		List<String> notFoundList = (List<String>) responseMap.get("notFoundList");
		// InputStream is = (InputStream)responseMap.get("InputStream");
		Long endRequest = System.currentTimeMillis();
		logger.info("Cost for requesting data from PGW web service:" + (endRequest - startRequest) / 1000.0 + "s");
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		Set<BatchTemplate> btSet = getBatchTemplates();
		SubDataSaxHandler sdsh = new SubDataSaxHandler(btSet);
		parser.parse(combinedFile, sdsh);
		List<String> titles = sdsh.getTitleList();
		List<String[]> valueList = sdsh.getValueList();
		int valueArraySize = titles.size();
		for (String notFound : notFoundList) {
			String[] values = new String[valueArraySize];
			values[0] = notFound + " NOT FOUND!";
			valueList.add(values);
		}
		OutputStream out = null;
		try {
			out = makeMultiQueryResultPersistent(numberList, valueList, titles, userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.close();
		return true;
	}


	@Override
	public boolean handleMultiSubscriberUser(List<String> numberList, String userName) {
		logger.info("--Method: DOM4J,Single--");
		logger.info("1.start to judge number size");
		Integer searchLimit = subscriberDataSetting.getSearchLimit();
		if (numberList.size() >= searchLimit) {
			logger.info("Too many sub. numbers:" + searchLimit + ".The search limit is:" + searchLimit);
			return false;
		}
		logger.info("2.request data from PGW web service");
		Long startRequest = System.currentTimeMillis();
		Map<String, String> responseDataMap = getBatchValidNumberResponseXml(numberList);
		Long endRequest = System.currentTimeMillis();
		logger.info("Cost for requesting data from PGW web service:" + (endRequest - startRequest) / 1000.0 + "s");
		if (responseDataMap.containsKey("false")) {
			logger.info("error happened while getting response xml from pgw webservice!");
			return false;
		}
		String[] numbers = new String[responseDataMap.size()];
		Set<String> numberSet = responseDataMap.keySet();
		Iterator<String> ii = numberSet.iterator();

		while (ii.hasNext()) {
			for (int i = 0; i < numberSet.size(); i++) {
				numbers[i] = ii.next();
			}
		}
		// 3.解析XML，写入excel,excel信息入库
		boolean isMultiQuerySuccess = false;
		if ((numbers.length != 0) && (null != numbers)) {
			try {
				logger.info("4.Resolve batch template");
				Long startResolveTemplate = System.currentTimeMillis();
				Map<String, Object> resolveMap = resolveUserBatchXml();
				Long endResolveTemplate = System.currentTimeMillis();
				logger.info("Cost for resolving template:" + (endResolveTemplate - startResolveTemplate) + "ms");
				isMultiQuerySuccess = addBacthUserDataOpt(numbers, responseDataMap, userName, resolveMap);
				logger.info("Multipul sub. data query done.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}

		}
		return isMultiQuerySuccess;
	}

	@Override
	public boolean addBacthUserDataOpt(String[] numbers, Map<String, String> xmlDataMap, String userName,
			Map<String, Object> xmlMap) {
		OutputStream out = null;
		try {
			List<String[]> vaList = new ArrayList<String[]>();
			List<String> titleNames = new ArrayList<String>();
			logger.info("Start to analyze multipul response xml!");
			Long startAnalyze = System.currentTimeMillis();
			getMultiResponseXml(numbers, xmlDataMap, xmlMap, vaList, titleNames);
			Long endAnalyse = System.currentTimeMillis();
			logger.info("Analyse costs:" + (endAnalyse - startAnalyze) / 1000.0 + "s");
			out = makeMultiQueryResultPersistent(numbers, vaList, titleNames, userName);
			Long endPersistent = System.currentTimeMillis();
			logger.info("Data persistent process completed.Costs:" + (endPersistent - endAnalyse) + "ms");
			return true;
		} catch (Throwable e) {
			logger.info("error happened when writing data to an excel !");
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}
	}

	private void getMultiResponseXml(String[] numbers, Map<String, String> xmlDataMap, Map<String, Object> xmlMap,
			List<String[]> vaList, List<String> titleNames)
					throws FileNotFoundException, IOException, DocumentException {
		int size = numbers.length;
		for (int i = 0; i < size; i++) {
			String[] values = new String[xmlMap.size()];
			String formatXml = xmlDataMap.get(numbers[i]);
			Iterator<Entry<String, Object>> it = xmlMap.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				Object val_obj = entry.getValue();
				String titleName = (String) entry.getKey();
				if (i == 0) {
					titleNames.add(titleName);
				}
				if (formatXml != null && !formatXml.contains("error")) {
					String title_val = resolveSoapXml(formatXml, val_obj, titleName);
					values[index] = title_val;
					index++;
				} else {
					values[0] = "'" + numbers[i] + "' can not be found.";
					index++;
				}
			}
			vaList.add(values);
		}
	}
	@Override
	public boolean analyzeBatchUserDataConcurrenct(String[] numbers, Map<String, String> xmlDataMap, String userName,
			Map<String, Object> xmlMap) throws Throwable
 {
		logger.info("Start to analyze batch data.Size:{}"+numbers.length);
		Long tf1 = System.currentTimeMillis();
		List<String> titleNames = getTitleNames(xmlMap);
		List<Future<String[]>> resultList = new ArrayList<>();
		for (int i = 0; i < numbers.length; i++) {
			try {
				Future<String[]> future = getDataValueArray(numbers[i], xmlDataMap.get(numbers[i]), userName, xmlMap);
				resultList.add(future);
			} catch (TaskRejectedException e) {
				logger.info("Thread pool is full,wait for 1s");
				Thread.sleep(1000);
			}
		}
		List<String[]> valueList = new ArrayList<>();
		for (Future<String[]> future : resultList) {
			valueList.add(future.get());
		}
		Long tf2 = System.currentTimeMillis();
		logger.info("Batch analyse costs:{}ms",(tf2-tf1));
		try {
			logger.info("Start to write excel");
			OutputStream out = makeMultiQueryResultPersistent(numbers, valueList, titleNames, userName);
			out.close();
			Long tf3 = System.currentTimeMillis();
			logger.info("Wrting excel costs:{}ms",(tf3-tf2));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static List<String> getTitleNames(Map<String, Object> xmlMap) {
		List<String> titleNames = new ArrayList<>();
		Iterator<Entry<String, Object>> it = xmlMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			String titleName = (String) entry.getKey();
			titleNames.add(titleName);
		}
		return titleNames;
	}
	@Async
	public Future<String> getRandomArray(){
		logger.info("Random value");
		String strA = String.valueOf(Math.random());
		return new AsyncResult<String>(strA);
	}
	@Async
	@Override
	public Future<String[]> getDataValueArray(String number, String xmlData, String userName,
			Map<String, Object> xmlMap) throws FileNotFoundException, IOException, DocumentException {
			String[] values = new String[xmlMap.size()];
			Iterator<Entry<String, Object>> it = xmlMap.entrySet().iterator();
			int index = 0;
			while (it.hasNext()) {
				//logger.info("get data...");
				Entry<String, Object> entry = it.next();
				Object val_obj = entry.getValue();
				String titleName = (String) entry.getKey();
				if (xmlData != null && !xmlData.contains("error")) {
					String title_val = resolveSoapXml(xmlData, val_obj, titleName);
					values[index] = title_val;
					index++;
				} else {
					values[0] = "'" + number + "' can not be found.";
					index++;
				}
			}
			return new AsyncResult<String[]>(values);
	}
	
	public OutputStream makeMultiQueryResultPersistent(List<String> numbers, List<String[]> vaList,
			List<String> titleNames, String userName) throws Exception {
		String[] numberArray = new String[5];
		for (int i = 0 ; i < 5 ; i++) {
			numberArray[i] = numbers.get(i);
		}
		return makeMultiQueryResultPersistent(numberArray, vaList, titleNames, userName);
	}
	@Override
	public OutputStream makeMultiQueryResultPersistent(String[] numbers, List<String[]> vaList,
			List<String> titleNames, String userName) throws Exception {
		logger.info("Start to export data in EXCEL file.");
		OutputStream out;
		SimpleDateFormat sdfDir = new SimpleDateFormat("yyyy/MM/dd");
		String rootPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getMultiExcelFileSaveDir() + "/" + sdfDir.format(new Date());
		File operationDir = new File(rootPath);
		if ((!operationDir.exists()) && (!operationDir.isDirectory())) {
			operationDir.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = userName + "_bacthUser_" + sdf.format(new Date()) + ".xlsx";
		out = new FileOutputStream(operationDir.getAbsoluteFile() + "/" + fileName);
		ExportExcel<String[]> excel = new ExportExcel<String[]>();
		String[] headers = (String[]) titleNames.toArray(new String[titleNames.size()]);
		String[] headersConvert = new String[headers.length];
		Map<String, String> convertMap = titleNameConvertMap();
		for (int i = 0; i < headers.length; i++) {
			headersConvert[i] = convertMap.get(headers[i]);
			if (headersConvert[i] == null) {
				headersConvert[i] = headers[i];
			}
		}
		excel.exportExcel(userName + "_number", headersConvert, vaList, out, null);
		logger.info("Excel is exported @" + (rootPath + "/" + fileName));
		String sql = "insert into user_data_log_multi(create_name,create_time,number_section,path)values(?,?,?,?)";
		jdbcTemplate.update(sql,new Object[]{userName,new Date(),makeStringArrayToSerie(numbers),rootPath + "/" + fileName});
//		UserDataLogMulti userDataLogMulti = new UserDataLogMulti();
//		userDataLogMulti.setCreateName(userName);
//		userDataLogMulti.setCreateTime(new Date());
//		userDataLogMulti.setPath(rootPath + "/" + fileName);
//		userDataLogMulti.setNumberSection(makeStringArrayToSerie(numbers));
//		userDataLogMultiRepository.save(userDataLogMulti);
		logger.info("username:" + userName);
		logger.info("path:" + rootPath + "/" + fileName);
		logger.info("number section:" + makeStringArrayToSerie(numbers));

		return out;
	}

	public static String makeStringArrayToSerie(String[] numbers) {
		String serie = "";
		for(int i = 0 ; i < numbers.length ; i++){
			serie += numbers[i]+",";
			if(i >= 5){
				serie = serie.substring(0,serie.length()-1) + "...(total:"+numbers.length+")";
				return serie;
			}
		}
		return serie.substring(0,serie.length()-1);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> resolveUserBatchXml() throws FileNotFoundException, IOException, DocumentException {
		Map<String, Object> data_map = new LinkedHashMap<String, Object>();

		// String filePath =
		// subscriberDataSetting.getSubscriberBatchAnalysisTemplateAbsPath();
		String filePath = getInUseTemplate("batch").getAbsolutePath();// in use
																		// batch
																		// template
		FileInputStream fileIn = new FileInputStream(filePath);
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		Element node = document.getRootElement();
		List<Element> elementAll = node.elements();
		for (Element e : elementAll) {
			String name = e.getName();
			String path = e.attributeValue("path");
			String[] atts;
			if (e.hasContent()) {
				atts = new String[4];
				List<Element> listElement = e.elements();
				for (Element element : listElement) {
					String property = element.attributeValue("property");
					String valueDes = element.attributeValue("valueDes");
					atts[0] = path;
					if (StringUtils.isNotBlank(valueDes)) {
						atts[3] = valueDes;
					}
					if (StringUtils.isNotBlank(property)) {
						atts[1] = property;
						atts[2] = element.getTextTrim();
					} else {
						name = e.getName() + "-" + element.getTextTrim();
						data_map.put(name, atts);
					}
				}
			} else {
				data_map.put(name, path);
			}
		}

		return data_map;
	}

	@SuppressWarnings("unchecked")
	private String resolveSoapXml(String localPath, Object val_obj, String titleName)
			throws FileNotFoundException, IOException, DocumentException {
		// READ USER BATCH XML

//		String userBatchAbsFile = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
//				+ subscriberDataSetting.getSubscriberBatchAnalysisTemplateAbsPath();
		// String userBatchAbsFile =
		// subscriberDataSetting.getSubscriberBatchAnalysisTemplateAbsPath();
		FileInputStream userBatchAbsFileIn = new FileInputStream(getInUseTemplate("batch").getAbsolutePath());
		SAXReader userBatchSaxReader = new SAXReader();
		Document userBatchDoc = userBatchSaxReader.read(userBatchAbsFileIn);

		// READ USER DATA XML
		InputStream fileIn = new ByteArrayInputStream(localPath.getBytes());
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		String result = "";
		DefaultElement oj;
		if ((val_obj instanceof String)) {
			String _path = val_obj.toString();
			List<Element> allBatchNodes = userBatchDoc.getRootElement().elements();
			for (Element element : allBatchNodes) {
				String pathAttrInTemplate = element.attributeValue("path");
				if (pathAttrInTemplate.equals(_path)) {
					// System.out.println(element.getName()+" is a single
					// path");
					String valueDes = element.attributeValue("valueDes");
					if (valueDes != null && valueDes.contains(":")) {
						// System.out.println(element.getName()+" NEED
						// CONVERT!");
						List<Element> listAll = document.selectNodes("/" + _path);
						for (Element e : listAll) {
							result = e.getTextTrim();
							if (!valueDes.contains(",")) {
								String key = valueDes.split(":")[0];
								String value = valueDes.split(":")[1];
								if (result.equals(key)) {
									result = value;
								}
							} else {
								String[] rules = valueDes.split(",");
								for (String rule : rules) {
									String key = rule.split(":")[0];
									String value = rule.split(":")[1];
									if (result.equals(key)) {
										result = value;
										break;
									}
								}
							}
						}
					} else {
						// System.out.println(element.getName()+" need no
						// convert");
						List<Element> listAll = document.selectNodes("/" + _path);
						for (Element e : listAll) {
							result += e.getTextTrim() + ",";

						}
					}
				}
			}

			// List<Element> listAll = document.selectNodes("/" + _path);
			// for (Element e : listAll){
			// result = e.getTextTrim();
			//
			// }

		} else {
			String[] arrData = (String[]) val_obj;
			String _path = arrData[0];
			String property = arrData[1];
			String include_val = arrData[2];
			String valuedes = arrData[3];
			List<Element> listAll = document.selectNodes("/" + _path);
			for (Element e : listAll) {
				String parentName = e.getName();
				if ((StringUtils.isNotBlank(include_val)) && (StringUtils.isNotBlank(property))) {
					List<DefaultElement> listDef = e.elements(property);
					if ((null != listDef) && (listDef.size() > 0)) {
						oj = listDef.get(0);
						String basiVal = oj.getTextTrim();
						if (!include_val.endsWith(basiVal)) {
							continue;
						} else {
							if (titleName.contains("-isdnNumber")) {
								result = e.elementText("isdnNumber");
								break;
							} else if (titleName.contains("-status")) {
								if (valuedes != null && valuedes.contains(":")) {
									result = e.elementText("status");
									if (valuedes.contains(",")) {// multi-convert
										String[] convertRules = valuedes.split(",");
										for (String convertRule : convertRules) {
											String Key = convertRule.split(":")[0];
											String value = convertRule.split(":")[1];
											if (Key.equals(result)) {
												result = value;
												break;
											}
										}
									} else {// single-convert
										String[] convertPieceArray = valuedes.split(":");
										if (result.equals(convertPieceArray[0])) {
											result = convertPieceArray[1];
											break;
										}

									}

								}
							}

						}
					}
				} else {
					List<Element> listAlls = e.elements();
					for (Element ee : listAlls) {
						String name = ee.getName();
						if (titleName.equalsIgnoreCase(parentName + "-" + name)) {
							String val = ee.getTextTrim();
							if (StringUtils.isNotBlank(valuedes)) {
								String[] desVal = valuedes.split(",");
								for (String _val : desVal) {
									String key = _val.split(":")[0];
									String value = _val.split(":")[1];
									if (val.equals(key)) {
										val = value;
									}
								}
							}

							result = result + val + ",";
							break;
						}
					}
				}
			}
		}

		if (StringUtils.isNotBlank(result) && result.contains(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	private Map<String, Object> getResponseDataInputStream(List<String> validNumberList) throws IOException {
		List<PgwAccessPoint> pgwAccessPointList = subscriberDataSetting.getPgwList();
		StringBuilder combinedXml = new StringBuilder();
		combinedXml.append("<soapenv:Envelope>"+"\n"+"<soapenv:Body>"+"\n"+"<spml:searchResponse>"+"\n");
		List<String> notFoundList = new ArrayList<>();
		Boolean showLog = subscriberDataSetting.getShowSoapLog();
		
		for (String subscriberNumber : validNumberList) {
			try {
				String subscriberNumberType = determineQueryType(subscriberNumber);
				subscriberNumber = getQueryNumber(subscriberNumber,subscriberNumberType);
				Map<String, Object> requestObjectMap = SoapConnectionHelper
						.getSubscriberDataRequestObject(subscriberNumberType, subscriberNumber);
				for (PgwAccessPoint pgwAccessPoint : pgwAccessPointList) {
					int index = 1;
					String webServiceUrl = pgwAccessPoint.getWsUrl();
					String responseInfo = SoapConnectionHelper.getSearchResponseXml(requestObjectMap, webServiceUrl);
					responseInfo = formatXml(responseInfo);
					if(showLog){
						logger.info("Index of the PGW WSs:"+index);
						logger.info("From PGW WS:");
						logger.info(webServiceUrl);
						logger.info("Xml data:");
						logger.info(responseInfo);
					}
					if(responseInfo.contains("result=\"success\"")){
						if(showLog){
							logger.info("This is a successful response.");
						}
						String response = responseInfo.substring(responseInfo.indexOf("<objects"), responseInfo.lastIndexOf("</objects>"))+"</objects>";
						combinedXml.append(response+"\n");
						break;
					}else{
						if(showLog){
						logger.info("This is NOT a successful response,try to search on next one PGW.");
						}
						if(index == pgwAccessPointList.size()){
							//最后一次也没找到
							notFoundList.add(subscriberNumber);
						}else{
							index++;
						}
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		combinedXml.append("</spml:searchResponse>"+"\n"+"</soapenv:Body>"+"\n"+"</soapenv:Envelope>");
		SimpleDateFormat sdfDir = new SimpleDateFormat("yyyy/MM/dd");
		String rootPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getMultiExcelFileSaveDir() + "/" + sdfDir.format(new Date());
		File operationDir = new File(rootPath);
		if ((!operationDir.exists()) && (!operationDir.isDirectory())) {
			operationDir.mkdirs();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "combined_" +sdf.format(new Date()) + ".xml";
		File combinedXmlFile = new File(rootPath+"/"+fileName);
		FileUtils.write(combinedXmlFile, combinedXml);
		//InputStream is = new ByteArrayInputStream(combinedXml.toString().getBytes());
		Map<String,Object> responseMap = new HashMap<>();
		//responseMap.put("InputStream", is);
		logger.info("~~~~~ New combined xml written at: ~~~~~~~");
		logger.info(combinedXmlFile.getAbsolutePath());
		responseMap.put("notFoundList", notFoundList);
		responseMap.put("file", combinedXmlFile);
		return responseMap;
	}
	@Override
	public Map<String, String> getBatchValidNumberResponseXml(List<String> validNumberList) {
		Boolean showLog = subscriberDataSetting.getShowSoapLog();
		List<PgwAccessPoint> pgwAccessPointList = subscriberDataSetting.getPgwList();
		Map<String, String> subscriberDataMap = new HashMap<>();
		for (String subscriberNumber : validNumberList) {
			try {	
				String subscriberNumberType = determineQueryType(subscriberNumber);
				subscriberNumber = getQueryNumber(subscriberNumber,subscriberNumberType); //自动为开头不是86的非IMSI号码加上86
				Map<String, Object> requestObjectMap = SoapConnectionHelper
						.getSubscriberDataRequestObject(subscriberNumberType, subscriberNumber);
				for (PgwAccessPoint pgwAccessPoint : pgwAccessPointList) {
					
					String webServiceUrl = pgwAccessPoint.getWsUrl();
					String responseInfo = SoapConnectionHelper.getSearchResponseXml(requestObjectMap, webServiceUrl);
					
					if(showLog){
						logger.info("From PGW WS:");
						logger.info(webServiceUrl);
						logger.info("RESPONSE:");
						logger.info(responseInfo);
					}
					if (responseInfo.startsWith("Error")) {
						// SOAP请求失败，处理方法：进入下一循环
						continue;
					} else {
						String formatedResponseXml = formatXml(responseInfo);
						boolean isFileValidate = isResponseXmlValidate(formatedResponseXml);
						if (isFileValidate) {
							subscriberDataMap.put(subscriberNumber, formatedResponseXml);
							break;
						} else {
							subscriberDataMap.put(subscriberNumber, null);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				subscriberDataMap.put("false", null);
			}

		}
		return subscriberDataMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> titleNameConvertMap() throws FileNotFoundException, DocumentException {
//		String filePath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
//				+ subscriberDataSetting.getSubscriberBatchAnalysisTemplateAbsPath();
		String filePath = getInUseTemplate("batch").getAbsolutePath();
		FileInputStream fileIn = new FileInputStream(filePath);
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(fileIn);
		Element node = document.getRootElement();
		List<Element> elementAll = node.elements();
		Map<String, String> convertMap = new HashMap<>();
		for (Element element : elementAll) {
			List<Element> nodes = element.selectNodes("*");
			if (nodes.size() == 0) {
				// 无子节点
				String standardName = element.attributeValue("zhName");
				if (StringUtils.isNotBlank(standardName)) {
					convertMap.put(element.getName(), standardName);
				} else {
					convertMap.put(element.getName(), element.getName());
				}
			} else {
				for (Element childEle : nodes) {
					if (!childEle.getPath().contains("include")) {
						convertMap.put(element.getName() + "-" + childEle.getStringValue(),
								childEle.attributeValue("zhName"));
					}
				}
			}
		}
		System.out.println(convertMap);
		return convertMap;
	}

	@Override
	public List<UserDataLog> getAllUserDataLog() {
		return userDataLogRepository.findAll(new Sort(Direction.DESC, "createTime"));
	}

	@Override
	public List<UserDataLogMulti> getAllUserDataLogMulti() {
		return userDataLogMultiRepository.findAll(new Sort(Direction.DESC, "createTime"));
	}

	@Override
	public Map<String, List<AnalysisTemplateConfig>> getAllTemplate() {
		List<AnalysisTemplateConfig> allTemplates = analysisTemplateConfigRepository.findAll();
		for (AnalysisTemplateConfig analysisTemplateConfig : allTemplates) {
			if (analysisTemplateConfig.getIsInUse().equals("no")
					&& analysisTemplateConfig.getIsOriginFile().equals("yes")) {
				analysisTemplateConfigRepository.delete(analysisTemplateConfig);
			}
		}
		List<AnalysisTemplateConfig> inUseTemplateList = analysisTemplateConfigRepository
				.findAllByIsInUseOrderByUploadTimeDesc("yes");
		inUseTemplateList = makeInUseListResonable(inUseTemplateList);
		List<AnalysisTemplateConfig> notInUseTemplateList = analysisTemplateConfigRepository
				.findAllByIsInUseOrderByUploadTimeDesc("no");
		Map<String, List<AnalysisTemplateConfig>> m = new HashMap<>();
		m.put("inUse", inUseTemplateList);
		m.put("notInUse", notInUseTemplateList);
		return m;
	}

	private List<AnalysisTemplateConfig> makeInUseListResonable(List<AnalysisTemplateConfig> inUseTemplateList) {
		List<AnalysisTemplateConfig> singleList = new ArrayList<>();
		List<AnalysisTemplateConfig> batchList = new ArrayList<>();
		List<AnalysisTemplateConfig> resonableList = new ArrayList<>();
		for (AnalysisTemplateConfig analysisTemplateConfig : inUseTemplateList) {
			if (analysisTemplateConfig.getTemplateType().equals("single")) {
				singleList.add(analysisTemplateConfig);
			}
			if (analysisTemplateConfig.getTemplateType().equals("batch")) {
				batchList.add(analysisTemplateConfig);
			}
		}
		if (batchList.size() == 1 && singleList.size() == 1) {
			return inUseTemplateList;
		} else {
			if (batchList.size() == 0) {
				AnalysisTemplateConfig originalBatchConfig = originalTemplateConstructor("batch");
				originalBatchConfig.setIsInUse("yes");
				analysisTemplateConfigRepository.save(originalBatchConfig);
			}
			if (singleList.size() == 0) {
				AnalysisTemplateConfig originalSingleConfig = originalTemplateConstructor("single");
				originalSingleConfig.setIsInUse("yes");
				analysisTemplateConfigRepository.save(originalSingleConfig);
			}
			if (batchList.size() > 1) {
				for (AnalysisTemplateConfig analysisTemplateConfig : batchList) {
					if (batchList.size() != 1) {
						analysisTemplateConfigRepository.delete(analysisTemplateConfig);
					}
				}
			}
			if (singleList.size() > 1) {
				for (AnalysisTemplateConfig analysisTemplateConfig : singleList) {
					if (singleList.size() != 1) {
						analysisTemplateConfigRepository.delete(analysisTemplateConfig);
					}
				}
			}
			resonableList.addAll(singleList);
			resonableList.addAll(batchList);
			return resonableList;
		}

	}

	@Override
	public void deleteRecordAndFile(Long id) {
		AnalysisTemplateConfig a = analysisTemplateConfigRepository.findOne(id);
		analysisTemplateConfigRepository.delete(a);

	}

	@Override
	public void uploadAnalysisTemplate(UploadAnalysisTemplate uploadAnalysisTemplate) {
		AnalysisTemplateConfig a = new AnalysisTemplateConfig();
		MultipartFile multiQueryTemplate = uploadAnalysisTemplate.getMultiQueryTemplate();
		String filePath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getUploadAnalysisTemplateDirWithDate()
				+ multiQueryTemplate.getOriginalFilename();
		File f = new File(filePath);
		a.setIsInUse("no");
		a.setTemplateFilePath(f.getAbsolutePath().replaceAll("\\\\", "/"));
		a.setUploader(uploadAnalysisTemplate.getUploader());
		a.setUploadTime(new Date());
		a.setIsOriginFile("no");
		try {
			FileUtils.writeByteArrayToFile(f, multiQueryTemplate.getBytes());
			try {
				String templateContext = FileUtils.readFileToString(f);
				if (templateContext.contains("</dhss:subscriber-data-parse-rule>")) {
					a.setTemplateType("single");
				} else if (templateContext.contains("</userBatch>")) {
					a.setTemplateType("batch");
				} else {
					a.setTemplateType("unknown");
				}
				try {
					String comment = templateContext
							.substring(templateContext.indexOf("<!--"), templateContext.indexOf("-->"))
							.replace("<!--", "").trim();
					a.setComment(comment);
				} catch (Exception e) {
					a.setComment("no comment");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				a.setTemplateType("unknown");

			}
			analysisTemplateConfigRepository.save(a);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, String> upgradeTemplateStates(Long id) {
		AnalysisTemplateConfig templateTryToUse = analysisTemplateConfigRepository.findOne(id);
		Map<String, String> m = new HashMap<>();
		if (!templateTryToUse.getTemplateType().equals("single")
				&& !templateTryToUse.getTemplateType().equals("batch")) {
			m.put("severity", "error");
			m.put("summary", "Type exception");
			m.put("detail", "File type is not proper.Please select 'single' or 'batch' type.");
			return m;
		}
		List<AnalysisTemplateConfig> otherConfigList = analysisTemplateConfigRepository
				.findAllByTemplateType(templateTryToUse.getTemplateType());
		for (AnalysisTemplateConfig analysisTemplateConfig : otherConfigList) {
			analysisTemplateConfig.setIsInUse("no");
		}
		templateTryToUse.setIsInUse("yes");
		analysisTemplateConfigRepository.save(otherConfigList);
		analysisTemplateConfigRepository.save(templateTryToUse);
		m.put("severity", "success");
		m.put("summary", "Change success");
		m.put("detail", "Template file has changed as:" + templateTryToUse.getTemplateFilePath() + "\ntype:"
				+ templateTryToUse.getTemplateType());
		return m;

	}

	private AnalysisTemplateConfig originalTemplateConstructor(String type) {
		AnalysisTemplateConfig originConfig = new AnalysisTemplateConfig();
		originConfig.setTemplateType(type);
		originConfig.setUploader("ICES DHSS team");
		originConfig.setComment("Original " + type + " analysis template");
		originConfig.setIsOriginFile("yes");
		File templateFile = null;
		if (type.equals("single")) {
			templateFile = getOriginalSingleInstance();
		}
		if (type.equals("batch")) {
			templateFile = getOriginalBatchInstance();
		}
		originConfig.setTemplateFilePath(templateFile.getAbsolutePath().replaceAll("\\\\", "/"));
		originConfig.setUploadTime(new Date(templateFile.lastModified()));
		originConfig.setIsInUse("no");
		return originConfig;
	}

	@Override
	public Map<String, String> resetTemplateStates(String type) {
		Map<String, String> m = new HashMap<>();
		try {
			AnalysisTemplateConfig originConfig = originalTemplateConstructor(type);
			List<AnalysisTemplateConfig> otherConfigList = analysisTemplateConfigRepository.findAllByTemplateType(type);
			for (AnalysisTemplateConfig analysisTemplateConfig : otherConfigList) {
				analysisTemplateConfig.setIsInUse("no");
			}
			originConfig.setIsInUse("yes");
			analysisTemplateConfigRepository.save(originConfig);
			m.put("severity", "success");
			m.put("summary", "Reset success");
			m.put("detail", "Template file has reset as:" + originConfig.getTemplateFilePath() + "\ntype:" + type);
		} catch (Exception e) {
			e.printStackTrace();
			m.put("severity", "error");
			m.put("summary", "Reset failure");
			m.put("detail", "Template file reset failed!Origin " + type
					+ " template file may be lost.Please contact DHSS team for support.");
		}

		return m;
	}

	private File getOriginalSingleInstance() {

		String templateAbsPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getXmlAnalysisFileTemplateAbsPath();
		if (singleAnalysisTemplate == null) {
			singleAnalysisTemplate = new File(templateAbsPath);
		}
		return singleAnalysisTemplate;
	}

	private File getOriginalBatchInstance() {
		String templateAbsPath = subscriberDataSetting.getDhssSubscriberBaseDir() + "/"
				+ subscriberDataSetting.getSubscriberBatchAnalysisTemplateAbsPath();

		if (batchAnalysisTemplate == null) {
			batchAnalysisTemplate = new File(templateAbsPath);
		}
		return batchAnalysisTemplate;
	}

	@Override
	public File getInUseTemplate(String type) {
		List<AnalysisTemplateConfig> inUseTemplateList = getAllTemplate().get("inUse");
		File inUseFile = null;
		for (AnalysisTemplateConfig analysisTemplateConfig : inUseTemplateList) {
			if (analysisTemplateConfig.getTemplateType().equals(type)) {
				inUseFile = new File(analysisTemplateConfig.getTemplateFilePath());
			}
		}
		return inUseFile;
	}


	@Override
	@Async
	public void executeAsyncTask(Integer i) {
		logger.info("执行异步任务：" + i);
	}

	@Override
	@Async
	public Future<String> asyncInvokeReturnFuture(int i) throws InterruptedException {
		logger.info("input is " + i);
		int execute = ((int) (Math.random() * 3) + 2) * 100;
		logger.info("execute for " + execute + "s");
		Thread.sleep(execute);
		Future<String> future = new AsyncResult<String>("success:" + i);
		return future;
	}
	@Override
	public void testMulti(Integer size) throws Throwable {

		Long start = System.currentTimeMillis();
		Map<String,String> dataMap = getData(1000);
		Long endOriginData = System.currentTimeMillis();
		logger.info("Got origin data,costs {} ms",(endOriginData-start));
		Map<String,Object> resolveMap = resolveUserBatchXml();
		Long afterAnalyseSelfTemplate = System.currentTimeMillis();
		logger.info("Analyse batch template done,costs {} ms",(afterAnalyseSelfTemplate-endOriginData));
		String[] numbers = new String[dataMap.size()];
		Set<String> numberSet = dataMap.keySet();
		Iterator<String> ii = numberSet.iterator();
		while (ii.hasNext()) {
			for (int i = 0; i < numberSet.size(); i++) {
				numbers[i] = ii.next();
			}
		}
		analyzeBatchUserDataConcurrenct(numbers,dataMap,"test-user",resolveMap);
		Long end = System.currentTimeMillis();
		logger.info("Analyse all batch data,costs {} ms",(end-afterAnalyseSelfTemplate));
	
		
	}
	private Map<String,String> getData(int dataCount) throws IOException{
		File singleDataDir = new File(subscriberDataSetting.getTestFilesPath());
		File[] files = singleDataDir.listFiles();
		Map<String,String> dataMap = new HashMap<>();
		for (int i = 0; i < dataCount; i++) {
			Integer randomFlag = (int)(Math.random()*files.length);
			File dataFile = files[randomFlag];
			String formatedXml = FileUtils.readFileToString(dataFile);
			dataMap.put(String.valueOf(i), formatedXml);
		}
		return dataMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<BatchTemplate> getBatchTemplates() {
		SAXReader sax = new SAXReader();
		String filePath = getInUseTemplate("batch").getAbsolutePath();
		Set<BatchTemplate> btSet = new LinkedHashSet<>();
		try {
			Document document = sax.read(filePath);
			Element root = document.getRootElement();
			List<Element> elementAll = root.elements();
			for (Element element : elementAll) {
				BatchTemplate bt = resolveBatchConfig(element);
				btSet.add(bt);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return btSet;
	}

	private BatchTemplate resolveBatchConfig(Element element) {
		BatchTemplate bt = new BatchTemplate();
		String qName = element.getName();
		bt.setqName(qName);
		String zhName = element.attributeValue("zhName");
		if(StringUtils.isNotBlank(zhName) && !zhName.equals("null")){
			bt.setZhName(zhName);
		}else{
			bt.setZhName(qName);
		}
		bt.setxPath(element.attributeValue("path"));
		String valueDesc = element.attributeValue("valueDes");
		if (StringUtils.isNotBlank(valueDesc) && !valueDesc.equals("null")) {
			bt.setHasValueDesc(true);
			String[] keyValuePairs = valueDesc.split(",");
			Map<String, String> keyValueMap = new LinkedHashMap<>();
			for (String keyValuePair : keyValuePairs) {
				String[] valueTos = keyValuePair.split(":");
				keyValueMap.put(valueTos[0], valueTos[1]);
			}
			bt.setValueDecsList(keyValueMap);
		}
		return bt;
	}


}
