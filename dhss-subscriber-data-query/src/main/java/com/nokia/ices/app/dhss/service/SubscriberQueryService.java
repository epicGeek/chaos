package com.nokia.ices.app.dhss.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.dom4j.DocumentException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.bean.BatchTemplate;
import com.nokia.ices.app.dhss.domain.AnalysisTemplateConfig;
import com.nokia.ices.app.dhss.domain.UploadAnalysisTemplate;
import com.nokia.ices.app.dhss.domain.UserDataLog;
import com.nokia.ices.app.dhss.domain.UserDataLogMulti;

public interface SubscriberQueryService {
	
	final static String QUERY_TYPE_IMSI = "imsi";

	final static String QUERY_TYPE_MSISDN = "msisdn";

	public Map<String, Object> getSingleSubscriberData(String subscriberNumber,String userName) throws DocumentException, TransformerFactoryConfigurationError, TransformerException;

	public Map<String, Object> getMockQueryData(String subscriberNumber);

	public List<String> getNumberArrayFromUploadExcel(MultipartFile multiQueryTemplate) throws IOException;
	
	public void downloadTemplate(HttpServletRequest request,HttpServletResponse response);
	
	public void downloadUserDataLog(String filePath,HttpServletRequest request,HttpServletResponse response);
	
	public Page<UserDataLog> getAllUserDataLog(Pageable pageable);
	
	public List<UserDataLog> getAllUserDataLog();

	public Page<UserDataLogMulti> getAllUserDataLogMulti(Pageable pageable);
	
	public List<UserDataLogMulti> getAllUserDataLogMulti();
	
	public Map<String, Object> readCachedFile(String filePath,String subscriberNumber,String unitName);

	public boolean handleMultiSubscriberUser(List<String> numberList,String userName);

	public Map<String,List<AnalysisTemplateConfig>> getAllTemplate();

	public void deleteRecordAndFile(Long id);

	public void uploadAnalysisTemplate(UploadAnalysisTemplate uploadAnalysisTemplate);

	public Map<String,String> upgradeTemplateStates(Long id);

	public Map<String, String> resetTemplateStates(String type);
	
	public File getInUseTemplate(String type);//type = "single" or "batch"
	
	public void executeAsyncTask(Integer i);
	
	public Future<String> asyncInvokeReturnFuture(int i) throws InterruptedException;
	
	public Map<String, Object> resolveUserBatchXml() throws FileNotFoundException, IOException, DocumentException;

	public boolean addBacthUserDataOpt(String[] numbers, Map<String, String> xmlDataMap,String userName,Map<String,Object> xmlMap);
	
	public boolean analyzeBatchUserDataConcurrenct(String[] numbers, Map<String, String> xmlDataMap, String userName,
			Map<String, Object> xmlMap) throws Throwable;
	
	public Future<String[]> getDataValueArray(String number, String xmlData, String userName,
			Map<String, Object> xmlMap) throws FileNotFoundException, IOException, DocumentException;
	
	public OutputStream makeMultiQueryResultPersistent(String[] numbers, List<String[]> vaList,
			List<String> titleNames, String userName) throws Throwable;

	public void testMulti(Integer size) throws Throwable;
	
	
	public Map<String, String> getBatchValidNumberResponseXml(List<String> validNumberList);
	
	// New subscriber data query methods,by SAX.
	public Set<BatchTemplate> getBatchTemplates();

	public boolean handleMultiSubscriberUserBySax(List<String> numberList, String userName) throws Exception ;
	
}
