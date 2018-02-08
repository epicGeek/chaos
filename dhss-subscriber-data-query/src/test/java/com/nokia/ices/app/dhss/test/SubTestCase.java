package com.nokia.ices.app.dhss.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.nokia.ices.app.config.SubscriberDataSetting;
import com.nokia.ices.app.config.TaskExecutorPool;
import com.nokia.ices.app.dhss.bean.BatchTemplate;
import com.nokia.ices.app.dhss.domain.UserDataLogMulti;
import com.nokia.ices.app.dhss.repository.UserDataLogMultiRepository;
import com.nokia.ices.app.dhss.service.SubscriberQueryService;
import com.nokia.ices.app.dhss.service.impl.SubscriberQueryServiceImpl;
import com.nokia.ices.app.dhss.utils.ExportExcel;
import com.nokia.ices.app.dhss.utils.SubDataSaxHandler;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SubTestCase {
	private static final Logger logger = LoggerFactory.getLogger(SubTestCase.class);
	@Autowired
	SubscriberDataSetting subscriberDataSetting;
	@Autowired
	UserDataLogMultiRepository userDataLogMultiRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	SubscriberQueryService subscriberQueryService;
	//@Test
	public void subTest(){
		String userName = "tester";
		String rootPath = "/var/log";
		String fileName = "file1";
		String[] numbers = generateRandomNumbers(100000);
		UserDataLogMulti userDataLogMulti = new UserDataLogMulti();
		userDataLogMulti.setCreateName(userName);
		System.out.println("username:"+userName);
		userDataLogMulti.setCreateTime(new Date());
		userDataLogMulti.setPath(rootPath + "/" +fileName);
		System.out.println("path:"+rootPath + "/" +fileName);
		String numberSection = SubscriberQueryServiceImpl.makeStringArrayToSerie(numbers);
		userDataLogMulti.setNumberSection(numberSection);
		System.out.println("number section:"+numberSection);
		System.out.println(numberSection.length());
		userDataLogMultiRepository.save(userDataLogMulti);
	}
	//@Test
	public void subTest2(){
		List<Object> args = new ArrayList<>();
		String userName = "tester";
		String rootPath = "/var/log";
		String fileName = "file2";
		String[] numbers = generateRandomNumbers(100000);
		String numberSection = SubscriberQueryServiceImpl.makeStringArrayToSerie(numbers);
		args.add(userName);
		args.add(new Date());
		args.add(numberSection);
		args.add(rootPath+"/"+fileName);
		String sql = "insert into user_data_log_multi (create_name, create_time, number_section, path) values (?, ?, ?, ?)";
	    jdbcTemplate.update(sql, new Object[]{userName,new Date(),numberSection,rootPath+"/"+fileName});
	    System.out.println("jdbc completed");
	}
	//@Test
	public void hehe(){
		logger.info("执行异步任务,无返回值");
		for (int j = 0; j < 20; j++) {
			subscriberQueryService.executeAsyncTask(j);
		}
	}
	
	//@Test
	public void hehe2() throws InterruptedException, ExecutionException{
		System.out.println("执行异步任务，有返回值");
		List<Future<String>> resultList = new ArrayList<>();
		Long start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			try {
				Future<String> future = subscriberQueryService.asyncInvokeReturnFuture(i);
				resultList.add(future);
			} catch (TaskRejectedException e) {
				logger.info("线程池满，等待1S");
				Thread.sleep(1000);
			}
		}
		
		for (Future<String> future : resultList) {
			logger.info(future.get());
		}
		Long end = System.currentTimeMillis();
		logger.info("multipul thread costs:"+(end-start)+"ms");


		
	}
	private String[] generateRandomNumbers(int size) {
		String[] number = new String[size];
		for (int i = 0; i < size; i++) {
			number[i] = UUID.randomUUID().toString();
		}
		return number;
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
	//@Test
	public void singleThreadBatchQuery() throws IOException, DocumentException{
		Long start = System.currentTimeMillis();
		Map<String,String> dataMap = getData(1);
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			String xml = entry.getValue();
			String key = entry.getKey();
			xml = SubscriberQueryServiceImpl.formatXml(xml);
			System.out.println(xml);
			dataMap.put(key, xml);
		}
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
		boolean isMultiQuerySuccess = subscriberQueryService.addBacthUserDataOpt(numbers,dataMap,"daddy",resolveMap);
		Long end = System.currentTimeMillis();
		logger.info("Analyse all batch data,costs {} ms",(end-afterAnalyseSelfTemplate));
	}
	
	//@Test
	public void multipulThreadsBatchQuery() throws Throwable{
		Long start = System.currentTimeMillis();
		Map<String,String> dataMap = getData(50);
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
		subscriberQueryService.analyzeBatchUserDataConcurrenct(numbers,dataMap,"daddy",resolveMap);
		Long end = System.currentTimeMillis();
		logger.info("Analyse all batch data,costs {} ms",(end-afterAnalyseSelfTemplate));
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
	public boolean analyzeBatchUserDataConcurrenct(String[] numbers, Map<String, String> xmlDataMap, String userName,
			Map<String, Object> xmlMap) throws Throwable
 {
		logger.info("Start to analyze batch data.Size:{}"+numbers.length);
		Long tf1 = System.currentTimeMillis();
		List<String> titleNames = getTitleNames(xmlMap);
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
	//@Test
	public void tester() throws Throwable{

		SubscriberQueryServiceImpl s = new SubscriberQueryServiceImpl();
		s.testMulti(1000);
	
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private InputStream generateBigXml(int xmlCount) throws IOException, DocumentException{
		File singleDataDir = new File(subscriberDataSetting.getTestFilesPath());
		File[] files = singleDataDir.listFiles();
		SAXReader saxReader = new SAXReader();
		Document firstDoc = null;
		Element parent = null;
		for (int i = 0; i < xmlCount; i++) {
			Integer randomFlag = (int) (Math.random() * files.length);
			File dataFile = files[randomFlag];
			Document doc = saxReader.read(dataFile);
			if (i == 0) {
				firstDoc = doc;
				parent = (Element) firstDoc.getRootElement();
			} else {
				List<Element> elements = doc.getDocument().getRootElement().elements();
				for (Element element : elements) {
					parent.add(element.detach());
				}
			}
		}
		String combinedXml = firstDoc.asXML();
		InputStream is = new ByteArrayInputStream(combinedXml.getBytes());
		return is;
	}
	//@Test
	public void readXmlBySax() throws Throwable{
		SAXParserFactory parserFactory=SAXParserFactory.newInstance();
		SAXParser parser=parserFactory.newSAXParser();
		Set<BatchTemplate> btSet = subscriberQueryService.getBatchTemplates();
		SubDataSaxHandler sdsh = new SubDataSaxHandler(btSet);
		parser.parse(generateBigXmlString(1), sdsh);
		List<String> titles = sdsh.getTitleList();
		List<String[]> valueList = sdsh.getValueList();
		OutputStream out = subscriberQueryService.makeMultiQueryResultPersistent(new String[]{"111","222"}, valueList, titles, "sax");
		out.close();
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

	@Test
	public void testing(){
		String[] numbers = {"861231312","861312313871","46012312312123","8612313123"};
		System.out.println(makeStringArrayToSerie(numbers));
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
	
}
