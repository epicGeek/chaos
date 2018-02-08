package com.nokia.ices.app.dhss.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.config.SubscriberDataSetting;
import com.nokia.ices.app.dhss.bean.BatchTemplate;
import com.nokia.ices.app.dhss.domain.AnalysisTemplateConfig;
import com.nokia.ices.app.dhss.domain.UploadAnalysisTemplate;
import com.nokia.ices.app.dhss.domain.UserDataLog;
import com.nokia.ices.app.dhss.domain.UserDataLogMulti;
import com.nokia.ices.app.dhss.service.SubscriberQueryService;
import com.nokia.ices.app.dhss.service.impl.SubscriberQueryServiceImpl;
import com.nokia.ices.app.dhss.utils.SubDataSaxHandler;
@RestController
public class SubscriberDataQueryController {

	private Logger logger = LoggerFactory.getLogger(SubscriberDataQueryController.class);

	@Autowired
	private SubscriberQueryService subscriberQueryService;
	
	@Autowired
	private  SubscriberDataSetting subscriberDataSetting;

	/**
	 * @throws DocumentException 
	 * @throws TransformerException 
	 * @throws TransformerFactoryConfigurationError 
	 */
	@RequestMapping("/api/v1/subscriber-data/{subscriberId}/{userName}")
	public Map<String, Object> querySubscriberData(@PathVariable String subscriberId,@PathVariable String userName) throws DocumentException, TransformerFactoryConfigurationError, TransformerException {
		if(userName==null||userName.equals("null")||userName.equals("undefined")){
			userName = "user";
		}
		if(userName.contains(" ")){
			String[] userNameEle = userName.split(" ");
			userName = userNameEle[userNameEle.length-1];
		}
		return subscriberQueryService.getSingleSubscriberData(subscriberId,userName);
	}

	@RequestMapping(value = "/api/v1/subscriber-data-mock")
	public Map<String, Object> getSubscriberData(
			@RequestParam(value = "subscriberNumber", required = false) String subscriberNumber) {
		// subscriberDataQueryService.subscriberDataQueryHandler(subscriberNumber);
		return subscriberQueryService.getMockQueryData(subscriberNumber);
	}
	@RequestMapping(value = "/api/v1/read-cached-file")
	public Map<String,Object> readCachedFile(
			@RequestParam(value = "filePath",required =false) String filePath,
			@RequestParam(value = "subscriberNumber",required =false) String subscriberNumber,
			@RequestParam(value = "unitName",required =false) String unitName ){
		return subscriberQueryService.readCachedFile(filePath,subscriberNumber,unitName);
	}
	@RequestMapping(value = "/api/v1/multi-query-input")
	public Map<String,Object> getMultiQueryInput(
			@RequestParam(value = "numberString",required =false) String numberString,
			@RequestParam(value = "userName",required =false) String userName) throws Throwable{
		logger.info("Input:"+numberString);
		if(userName==null||userName.equals("null")||userName.equals("undefined")){
			userName = "user";
		}
		if(userName.contains(" ")){
			String[] userNameEle = userName.split(" ");
			userName = userNameEle[userNameEle.length-1];
		}
		if(numberString==null||numberString.equals("null")||numberString.equals("undefined")){
			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("multiQuerySuccess", false);
			return resultMap;
		}
		List<String> numberList = new ArrayList<>();
		String[] numberArray = numberString.split(",");
		for (String number : numberArray) {
			if(StringUtils.isNotBlank(number)){
				numberList.add(number);
			}
		}
		logger.info("Get origin number record:"+numberList.size() + " pieces");
	    return multiSubDataHandler(userName, numberList);
	}
	
	@RequestMapping(value = "/api/v1/multi-query-upload-template", method = RequestMethod.POST)
	public Map<String,Object> getMultiQueryTemplate(
			@RequestParam("templateFile") MultipartFile multiQueryTemplate,
			@RequestParam("userName") String userName) throws Throwable{
		if(userName==null||userName.equals("null")||userName.equals("undefined")){
			userName = "user";
		}
		if(userName.contains(" ")){
			String[] userNameEle = userName.split(" ");
			userName = userNameEle[userNameEle.length-1];
		}
		List<String> numberList =  subscriberQueryService.getNumberArrayFromUploadExcel(multiQueryTemplate);
		return multiSubDataHandler(userName, numberList);
		
	}

	private Map<String, Object> multiSubDataHandler(String userName, List<String> numberList) throws Throwable {
		Map<String,Object> resultMap = new HashMap<>();
		if(numberList.size()==0){
			logger.info("There is no valid number in the uploaded template!");
			resultMap.put("uploadSuccess", true);
			resultMap.put("multiQuerySuccess", false);
			return resultMap;
		}
		try {
			String mode = subscriberDataSetting.getAnalyzeMode();
			boolean isMultiQuerySuccess = false;
			if(mode.equals("dom")){
				//1. 最开始的方法，使用DOM解析，效率低
				isMultiQuerySuccess = subscriberQueryService.handleMultiSubscriberUser(numberList,userName);
			}else if (mode.equals("pool")){
				//2. 用多线程改进的方法，使用DOM解析，效率高了不少
				isMultiQuerySuccess = handleMultiSubscriberUserTaskPool(numberList,userName);
			}else if(mode.equals("sax")){
				//3.测试SAX解析方法
				isMultiQuerySuccess = subscriberQueryService.handleMultiSubscriberUserBySax(numberList, userName);
			}else{
				//默认为最开始的方法，使用DOM解析，效率低
				isMultiQuerySuccess = subscriberQueryService.handleMultiSubscriberUser(numberList,userName);
			}
			resultMap.put("uploadSuccess", true);
			resultMap.put("multiQuerySuccess", isMultiQuerySuccess);
			return resultMap;
		} catch (Exception e) {
			logger.info("upload template file failure!");
			resultMap.put("uploadSuccess", false);
			resultMap.put("errorInfo", e.getMessage());
			resultMap.put("multiQuerySuccess", false);
			return resultMap;
		}
	}

	private boolean handleMultiSubscriberUserTaskPool(List<String> numberList, String userName) throws Throwable {
		// 1.过滤不合法输入号码。
		logger.info("=======Method:DOM4J Task pool======");
		logger.info("1.start to filter illegal number");
		Integer searchLimit = subscriberDataSetting.getSearchLimit();
		if (numberList.size() >= searchLimit) {
			logger.info("Too many sub. numbers:" + searchLimit + ".The search limit is:" + searchLimit);
			return false;
		}
		// 2.对合法号码进行查询
		logger.info("2.request data from PGW web service");
		Long startRequest = System.currentTimeMillis();
		Map<String, String> responseDataMap = subscriberQueryService.getBatchValidNumberResponseXml(numberList);
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
				Map<String, Object> resolveMap = subscriberQueryService.resolveUserBatchXml();
				Long endResolveTemplate = System.currentTimeMillis();
				logger.info("Cost for resolving template:" + (endResolveTemplate - startResolveTemplate) + "ms");
				isMultiQuerySuccess = analyzeBatchUserDataConcurrenct(numbers, responseDataMap, userName, resolveMap);
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

	@RequestMapping(value = "/api/v1/downloadTemplate")
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		subscriberQueryService.downloadTemplate(request, response);
	}
	@RequestMapping(value = "/api/v1/downloadExcelByPath")
	public void downloadExcel(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "path",required = true) String path){
		subscriberQueryService.downloadUserDataLog(path, request, response);
	}
	
	@RequestMapping(value = "/api/v1/get-user-log-new")
	public List<UserDataLog> getAllUserDataLogNew(){
		return subscriberQueryService.getAllUserDataLog();
	}
	
	@RequestMapping(value = "/api/v1/get-user-log-multi-new")
	public List<UserDataLogMulti> getAllUserDataLogMulti(){
		return subscriberQueryService.getAllUserDataLogMulti();
	}
	
	@RequestMapping(value = "/api/v1/all-subscriber-analysis-templates")
	public Map<String,List<AnalysisTemplateConfig>> getInUseTemplate(){
		return subscriberQueryService.getAllTemplate();
	}
	
	@RequestMapping(value = "/api/v1/download-subscriber-analysis-templates")
	public void downloadTemplate(@RequestParam(value = "path",required = true) String path,HttpServletRequest request, HttpServletResponse response)
	{
		subscriberQueryService.downloadUserDataLog(path, request, response);//重用方法
	}
	
	@RequestMapping(value = "/api/v1/delete-record", method = RequestMethod.DELETE)
	public boolean deleteRecordAndFile(@RequestParam(value = "id",required = true) Long id){
		try {
			subscriberQueryService.deleteRecordAndFile(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@RequestMapping(value = "/api/v1/upload-analysis-template", method = RequestMethod.POST)
	public void uploadAnalysisTemplate(
			@RequestParam(value = "uploader",required = true) String uploader,
			@RequestParam("templateFile") MultipartFile multiQueryTemplate
			){
		UploadAnalysisTemplate uploadAnalysisTemplate = new UploadAnalysisTemplate();
		uploadAnalysisTemplate.setUploader(uploader);
		uploadAnalysisTemplate.setMultiQueryTemplate(multiQueryTemplate);
		subscriberQueryService.uploadAnalysisTemplate(uploadAnalysisTemplate);
	}
	
	@RequestMapping(value = "/api/v1/upgrade-template", method = RequestMethod.PATCH)
	public Map<String,String> useThisTemplate(@RequestBody String id){
		return subscriberQueryService.upgradeTemplateStates(Long.valueOf(id));
	}
	
	@RequestMapping(value = "/api/v1/reset-template", method = RequestMethod.POST)
	public Map<String,String> resetTemplate(@RequestBody String type){
		return subscriberQueryService.resetTemplateStates(type);
	}
	@RequestMapping(value = "/api/v1/test-multi",method = RequestMethod.GET)
	public void testMulti(@RequestParam(value = "size",required = false) Integer size) throws Throwable{
		if(size==null || size.equals("undefined")){
			size = 1000;
		}
		Long start = System.currentTimeMillis();
		Map<String,String> dataMap = getData(size);
		Long endOriginData = System.currentTimeMillis();
		logger.info("Got origin data,costs {} ms",(endOriginData-start));
		Map<String,Object> resolveMap = subscriberQueryService.resolveUserBatchXml();
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
	public boolean analyzeBatchUserDataConcurrenct(String[] numbers, Map<String, String> xmlDataMap, String userName,
			Map<String, Object> xmlMap) throws Throwable{
		logger.info("Start to analyze batch data.Size:{}"+numbers.length);
		Long tf1 = System.currentTimeMillis();
		List<String> titleNames = SubscriberQueryServiceImpl.getTitleNames(xmlMap);
		List<Future<String[]>> resultList = new ArrayList<>();
		for (int i = 0; i < numbers.length; i++) {
			try {
				Future<String[]> future = subscriberQueryService.getDataValueArray(numbers[i], xmlDataMap.get(numbers[i]), userName, xmlMap);
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
			OutputStream out = subscriberQueryService.makeMultiQueryResultPersistent(numbers, valueList, titleNames, userName);
			out.close();
			Long tf3 = System.currentTimeMillis();
			logger.info("Wrting excel costs:{}ms",(tf3-tf2));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	@RequestMapping(value = "/api/v2/test-multi",method = RequestMethod.GET)
	private void testMultiv2(@RequestParam(value = "size",required = false) Integer size) throws Throwable{
		if(size==null || size.equals("undefined")){
			size = 1000;
		}
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		SAXParser parser = parserFactory.newSAXParser();
		Set<BatchTemplate> btSet = subscriberQueryService.getBatchTemplates();
		Long start = System.currentTimeMillis();
		InputStream is = generateBigXmlString(size);
		Long endOriginData = System.currentTimeMillis();
		logger.info("Got origin data,costs {} ms",(endOriginData-start));
		SubDataSaxHandler sdsh = new SubDataSaxHandler(btSet);
		parser.parse(is, sdsh);
		List<String> titles = sdsh.getTitleList();
		List<String[]> valueList = sdsh.getValueList();
		Long afterAnalyseSelfTemplate = System.currentTimeMillis();
		logger.info("Analyse batch template done,costs {} ms",(afterAnalyseSelfTemplate-endOriginData));
		OutputStream out = subscriberQueryService.makeMultiQueryResultPersistent(new String[]{"111","222"}, valueList, titles, "sax");
		out.close();
		Long end = System.currentTimeMillis();
		logger.info("Analyse all batch data,costs {} ms",(end-start));

	}
	
	private InputStream generateBigXmlString(int xmlCount) throws IOException{
		File singleDataDir = new File(subscriberDataSetting.getTestFilesPath());
		File[] files = singleDataDir.listFiles();
		StringBuilder combinedXml = new StringBuilder();
		combinedXml.append("<soapenv:Envelope>"+"\n"+"<soapenv:Body>"+"\n"+"<spml:searchResponse>"+"\n");
		for (int i = 0; i < xmlCount; i++) {
			Integer randomFlag = (int) (Math.random() * files.length);
			File dataFile = files[randomFlag];
			String xmlContent = FileUtils.readFileToString(dataFile);
			String response = "";
			try {
				response = xmlContent.substring(xmlContent.indexOf("<objects"), xmlContent.lastIndexOf("</objects>"))+"</objects>";
				combinedXml.append(response+"\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		combinedXml.append("</spml:searchResponse>"+"\n"+"</soapenv:Body>"+"\n"+"</soapenv:Envelope>");
		InputStream is = new ByteArrayInputStream(combinedXml.toString().getBytes());
		return is;
	}
}
