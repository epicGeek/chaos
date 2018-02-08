package com.nokia.ices.app.dhss.service.impl;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.ParsedRange;
import org.elasticsearch.search.aggregations.bucket.range.Range.Bucket;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregator.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.bean.BossStatisticTimeScope;
import com.nokia.ices.app.dhss.config.CustomSetting;
import com.nokia.ices.app.dhss.service.BossQueryService;

@Service
public class BossQueryServiceImpl implements BossQueryService {
	private static final Logger logger = LogManager.getLogger(BossQueryServiceImpl.class);
	private static final SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
	@Autowired
	CustomSetting customSetting;
	@Autowired
	JdbcTemplate jdbcTemplate;


	public static List<BossStatisticTimeScope> getTimeScopes(DateTime startDate, DateTime endDate, String grain) {
		List<BossStatisticTimeScope> scopeList = new ArrayList<>();
		while (startDate.isBefore(endDate)) {
			BossStatisticTimeScope bsts = new BossStatisticTimeScope();
			bsts.setTimeScopeName(startDate.toString("yyyy-MM-dd HH:mm:ss"));
			bsts.setStartDate(startDate);
			if(grain.equals("15")){
				startDate = startDate.plusMinutes(15);
			}else if(grain.equals("hour")){
				startDate = startDate.plusHours(1);
			}else if(grain.equals("day")){
				startDate = startDate.plusDays(1);
			}
			if(startDate.isBefore(endDate)){
				bsts.setEndDate(startDate);
			}else{
				bsts.setEndDate(endDate);
			}
			logger.info(bsts.toString());
			scopeList.add(bsts);
		}
		return scopeList;
	}

	@Override
	public List<String> getHlrsnList() {
		String sql = "SELECT hlrsn,hss FROM boss_hlrsn_hss_mapping";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
		Boolean isHlrsnTransformedAsHss = customSetting.getHlrsnTransform();
		List<String> hlrsnList = new ArrayList<>();
		for (Map<String, Object> result : resultList) {
			if (isHlrsnTransformedAsHss) {
				hlrsnList.add(result.getOrDefault("hss", "").toString());
			} else {
				hlrsnList.add(result.getOrDefault("hlrsn", "").toString());
			}
		}
		return new ArrayList<>(new TreeSet<>(hlrsnList));
	}

	@Override
	public List<Map<String, Object>> getbusinessList() {

		String sql = "select distinct business_type,business_type_cn from boss_cmd_type where boss_version = ? order by business_type,business_type";
		String bossVersion = customSetting.getBossVersion();
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, bossVersion);
		return resultList;

	}

	@Override
	public List<String> getCommandList() {
		String sql = "select distinct operation_name from boss_cmd_type where boss_version = ? order by operation_name";
		String bossVersion = customSetting.getBossVersion();
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, bossVersion);
		List<String> commandList = new ArrayList<>();
		for (Map<String, Object> map : resultList) {
			commandList.add(map.get("operation_name").toString());
		}
		return commandList;
	}

	@Override
	public List<Map<String, Object>> getErrorCodeList() {
		String sql = "select error_code,error_code_desc from boss_error_code order by error_code";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
		return resultList;
	}

	@Override	
	public Map<String, Object> getBossStatistic(Map<String, Object> paramMap) {
		// get time scope
		DateTime startDate = (DateTime) paramMap.get("startDate");
		DateTime endDate = (DateTime) paramMap.get("endDate");
		if (startDate.isAfter(endDate)) {
			Map<String, Object> m = new HashMap<>();
			m.put("null", true);
			return m;
		}
		RestHighLevelClient client = initClient();
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder = searchSourceBuilder.size(0);
		BoolQueryBuilder bqb = new BoolQueryBuilder();
		// time range here
		RangeQueryBuilder rqb = new RangeQueryBuilder("response_time");
		rqb.gte(startDate);
		rqb.lte(endDate);
		bqb.must(rqb);
		if (paramMap.containsKey("hlrsn")) {
			String hlrsn = paramMap.get("hlrsn").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("hlrsn.keyword", hlrsn);
			bqb.must(tqb);
		}
		if (paramMap.containsKey("businessType")) {
			String businessType = paramMap.get("businessType").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("business_type.keyword", businessType);
			bqb.must(tqb);
		}
		searchSourceBuilder.query(bqb);
		searchRequest.source(searchSourceBuilder);
		RangeAggregationBuilder rab = new RangeAggregationBuilder("group_by_time").field("response_time");
		String grain = paramMap.get("grain").toString();
		List<BossStatisticTimeScope> timeScopes = getTimeScopes(startDate, endDate, grain);
		for (BossStatisticTimeScope bossStatisticTimeScope : timeScopes) {
			
			Range range = new Range(bossStatisticTimeScope.getTimeScopeName(), 
					bossStatisticTimeScope.getStartDate().toString(), 
					bossStatisticTimeScope.getEndDate().toString());
			rab.addRange(range);
		}
		TermsAggregationBuilder tab = new TermsAggregationBuilder("group_by_status", ValueType.STRING).field("response_status.keyword");
		rab.subAggregation(tab);
		searchSourceBuilder.aggregation(rab);
		Map<String,Object> resultMap = new HashMap<>();
		List<Double> ratioList = new ArrayList<>();
		List<Long> countList = new ArrayList<>();
		List<String> xAxis = new ArrayList<>();
		try {
			SearchResponse searchResponse = client.search(searchRequest);
            Aggregations aggs = searchResponse.getAggregations();
            ParsedRange byTime = aggs.get("group_by_time");
            List<? extends Bucket> bucketList = byTime.getBuckets();
            for (Bucket bucket : bucketList) {
            	String timeTag = bucket.getKeyAsString();
            	Aggregations statusAggs = bucket.getAggregations();
            	Terms byStatus = statusAggs.get("group_by_status");
            	Terms.Bucket successBucket = byStatus.getBucketByKey("success");
            	Terms.Bucket failureBucket = byStatus.getBucketByKey("failure");
            	Long docCount = 0L;
            	if(successBucket == null && failureBucket == null){
            		continue;
            	}else if(successBucket == null && failureBucket != null){
            		docCount = failureBucket.getDocCount();
            	}else if(successBucket != null && failureBucket == null){
            		docCount = successBucket.getDocCount();
            	}else if(successBucket != null && failureBucket != null){
            		docCount = successBucket.getDocCount()+failureBucket.getDocCount();
            	}
            	countList.add(docCount);
            	Double successRatio = successBucket.getDocCount()*1.0/docCount*100.0;
            	ratioList.add(successRatio);
            	xAxis.add(timeTag);
            }
            resultMap.put("ratio", ratioList);
            resultMap.put("count", countList);
            resultMap.put("label", xAxis);
		} catch (IOException e) {
			e.printStackTrace();
			resultMap.put("excetion", true);
			resultMap.put("detail", "IO exception happended during the getting the response.");
			return resultMap;
		} catch (NullPointerException e){
			e.printStackTrace();
			resultMap.put("excetion", true);
			resultMap.put("detail", "Null pointer exception happended during the bucket count.");
			return resultMap;
		}
		return resultMap;
	}
	@Override
	public Map<String, Object> getBossDataByCondition(Map<String, Object> paramMap) {

		RestHighLevelClient client = initClient();
		SearchRequest searchRequest = new SearchRequest();
		DateTime startDate = (DateTime) paramMap.get("startDate");
		DateTime endDate = (DateTime) paramMap.get("endDate");
		if (startDate.isAfter(endDate)) {
			logger.info("illegal time scope.return null.");
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("hits", new ArrayList<>());
			resultMap.put("total", 0);
			return resultMap;
		}
		// String[] indices = getIndices(startDate,endDate);
		// searchRequest.indices(indices);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.sort(new FieldSortBuilder("response_time").order(SortOrder.DESC));
		Integer size = (Integer) paramMap.get("size");
		searchSourceBuilder.size(size);
		Integer page = (Integer) paramMap.get("page");
		Integer from = page * size;
		if (from >= 10000) {
			from = 10000 - size;
		}
		searchSourceBuilder.from(from);
		BoolQueryBuilder bqb = new BoolQueryBuilder();
		// time range here
		RangeQueryBuilder rqb = new RangeQueryBuilder("response_time");
		rqb.gte(paramMap.get("startDate"));
		rqb.lte(paramMap.get("endDate"));

		BoolQueryBuilder bqb_ = new BoolQueryBuilder();
		if (paramMap.containsKey("hlrsn")) {
			String hlrsn = paramMap.get("hlrsn").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("hlrsn.keyword", hlrsn);
			bqb_.must(tqb);
		}
		if (paramMap.containsKey("operationName")) {
			String operationName = paramMap.get("operationName").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("operation_name.keyword", operationName);
			bqb_.must(tqb);
		}
		if (paramMap.containsKey("errorCode")) {
			String errorCode = paramMap.get("errorCode").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("error_code.keyword", errorCode);
			bqb_.must(tqb);
		}
		if (paramMap.containsKey("resultType")) {
			String resultType = paramMap.get("resultType").toString();
			TermQueryBuilder tqb = new TermQueryBuilder("response_status.keyword", resultType);
			bqb_.must(tqb);
		}

		// number string analyze
		if (paramMap.containsKey("numberString")) {
			BoolQueryBuilder bqb__ = new BoolQueryBuilder();
			Map<String, List<String>> numberMap = pickMsisdnAndImsi(paramMap.get("numberString").toString());
			List<String> imsiList = numberMap.getOrDefault("imsi", new ArrayList<>());
			List<String> msisdnList = numberMap.getOrDefault("msisdn", new ArrayList<>());
			if (imsiList.size() != 0) {
				for (String imsi : imsiList) {
					TermQueryBuilder imsiTermb = new TermQueryBuilder("imsi.keyword", imsi);
					bqb__.should(imsiTermb);
				}
			}
			if (msisdnList.size() != 0) {
				for (String msisdn : msisdnList) {
					TermQueryBuilder msisdnTermb = new TermQueryBuilder("msisdn.keyword", msisdn);
					bqb__.should(msisdnTermb);
				}
			}
			bqb_.must(bqb__);
		}
		bqb.must(rqb);
		bqb.must(bqb_);
		searchSourceBuilder.query(bqb);
		searchRequest.source(searchSourceBuilder);
		Map<String, Object> resultMap = new HashMap<>();
		try {
			List<Map<String, Object>> resultDataList = new ArrayList<>();
			SearchResponse searchResponse = client.search(searchRequest);
			Long searchHits = searchResponse.getHits().totalHits;
			SearchHit[] searchHitsArray = searchResponse.getHits().getHits();
			for (SearchHit searchHit : searchHitsArray) {
				Map<String, Object> dataMap = searchHit.getSourceAsMap();
				DateTime dt = new DateTime(dataMap.get("response_time"));
				dataMap.put("response_time", dt.toString("yyyy-MM-dd HH:mm:ss,SSS"));
				resultDataList.add(searchHit.getSourceAsMap());
			}
			resultMap.put("hits", resultDataList);
			resultMap.put("total", searchHits);
			return resultMap;
		} catch (IOException e) {
			e.printStackTrace();
			resultMap.put("hits", new ArrayList<>());
			resultMap.put("total", 0);
			return resultMap;
		}
	}

	public static String[] getIndices(DateTime startDate, DateTime endDate) {
		List<String> indexList = new ArrayList<>();
		indexList.add(startDate.toString("yyyy-MM-dd"));
		while (!startDate.toString("yyyy-MM-dd").equals(endDate.toString("yyyy-MM-dd"))) {
			startDate = startDate.plusDays(1);
			indexList.add(startDate.toString("yyyy-MM-dd"));
		}
		String[] indices = new String[indexList.size()];
		for (int i = 0; i < indexList.size(); i++) {
			indices[i] = indexList.get(i);
		}
		return indices;
	}

	private RestHighLevelClient initClient() {
		String elasticsearchHost = customSetting.getElasticSearchHost();
		RestClient lowLevelRestClient = RestClient.builder(new HttpHost(elasticsearchHost, 9200, "http")).build();
		RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
		return client;
	}

	// private BulkProcessor initSimpleBulkProcessor(RestHighLevelClient client)
	// {
	// Settings settings = Settings.EMPTY;
	// ThreadPool threadPool = new ThreadPool(settings); // 构建新的线程池
	// BulkProcessor.Listener listener = new BulkProcessor.Listener() {
	// // 构建bulk listener
	// @Override
	// public void beforeBulk(long executionId, BulkRequest request) {
	// // 重写beforeBulk,在每次bulk request发出前执行,在这个方法里面可以知道在本次批量操作中有多少操作数
	// int numberOfActions = request.numberOfActions();
	// logger.debug("Executing bulk [{}] with {} requests", executionId,
	// numberOfActions);
	// }
	//
	// @Override
	// public void afterBulk(long executionId, BulkRequest request, BulkResponse
	// response) {
	// // 重写afterBulk方法，每次批量请求结束后执行，可以在这里知道是否有错误发生。
	// if (response.hasFailures()) {
	// logger.warn("Bulk [{}] executed with failures", executionId);
	// } else {
	// logger.debug("Bulk [{}] completed in {} milliseconds", executionId,
	// response.getTook().getMillis());
	// }
	// }
	//
	// @Override
	// public void afterBulk(long executionId, BulkRequest request, Throwable
	// failure) {
	// // 重写方法，如果发生错误就会调用。
	// logger.error("Failed to execute bulk", failure);
	// }
	//
	// };
	// BulkProcessor.Builder builder = new
	// BulkProcessor.Builder(client::bulkAsync, listener, threadPool);//
	// 使用builder做批量操作的控制
	// BulkProcessor bulkProcessor = builder.build();
	// // 在这里调用build()方法构造bulkProcessor,在底层实际上是用了bulk的异步操作
	// builder.setBulkActions(500); // 执行多少次动作后刷新bulk.默认1000，-1禁用
	// builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));//
	// 执行的动作大小超过多少时，刷新bulk。默认5M，-1禁用
	// builder.setConcurrentRequests(0);// 最多允许多少请求同时执行。默认是1，0是只允许一个。
	// builder.setFlushInterval(TimeValue.timeValueSeconds(10L));//
	// 设置刷新bulk的时间间隔。默认是不刷新的。
	// builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L),
	// 3)); // 设置补偿机制参数。由于资源限制（比如线程池满），批量操作可能会失败，在这定义批量操作的重试次数。
	// return bulkProcessor;
	//
	// }

	@Override
	public Map<String, List<String>> pickMsisdnAndImsi(String numberString) {
		String mmc = customSetting.getMmc();
		String countryCode = customSetting.getCountryCode();
		List<String> imsi = new ArrayList<>();
		List<String> msisdn = new ArrayList<>();
		for (String number : numberString.split(",")) {
			if (number.startsWith(mmc) && number.length() == 15) {// IMSI
				imsi.add(number);
				logger.info(number + " is an imsi");
				continue;
			} else if (number.startsWith(countryCode)) { // MSISDN
				msisdn.add(number);
				logger.info(number + " is an msisdn.");
				continue;
			} else {
				logger.info(number + " is neither imsi nor msisdn.");
			}

		}
		Map<String, List<String>> numberMap = new HashMap<>();
		numberMap.put("imsi", imsi);
		numberMap.put("msisdn", msisdn);
		return numberMap;
	}

	@Override
	public void downloadExportData(List<Map<String, Object>> dataHits, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] headers = { "Task ID", "Response Time", "Response Status", "User Name", "Error Code",
					"Error Description", "HLRSN", "MSISDN", "IMSI", "Operation Name", "Soap Log", "Error Log" };
			XSSFSheet sheet = workbook.createSheet("BOSS business data");
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			for (int i = 0; i < headers.length; i++) {// 写title
				XSSFCell cell = row.createCell(i);
				XSSFRichTextString text = new XSSFRichTextString(headers[i]);
				cell.setCellStyle(style);
				cell.setCellValue(text);
			}

			int index = 1;
			logger.info("Start to generate BOSS export-report");
			for (Map<String, Object> bossData : dataHits) {
				row = sheet.createRow(index);
				row.createCell(0)
						.setCellValue(bossData.get("task_id") != null ? bossData.get("task_id").toString() : "");
				row.createCell(1).setCellValue(
						bossData.get("response_time") != null ? bossData.get("response_time").toString() : "");
				row.createCell(2).setCellValue(
						bossData.get("response_status") != null ? bossData.get("response_status").toString() : "");
				row.createCell(3)
						.setCellValue(bossData.get("user_name") != null ? bossData.get("user_name").toString() : "");
				row.createCell(4)
						.setCellValue(bossData.get("error_code") != null ? bossData.get("error_code").toString() : "");
				row.createCell(5).setCellValue(
						bossData.get("error_message") != null ? bossData.get("error_message").toString() : "");
				row.createCell(6).setCellValue(bossData.get("hlrsn") != null ? bossData.get("hlrsn").toString() : "");
				row.createCell(7).setCellValue(bossData.get("msisdn") != null ? bossData.get("msisdn").toString() : "");
				row.createCell(8).setCellValue(bossData.get("imsi") != null ? bossData.get("imsi").toString() : "");
				row.createCell(9).setCellValue(
						bossData.get("operation_name") != null ? bossData.get("operation_name").toString() : "");
				row.createCell(10)
						.setCellValue(bossData.get("soap_log") != null ? bossData.get("soap_log").toString() : "");
				row.createCell(11)
						.setCellValue(bossData.get("error_log") != null ? bossData.get("error_log").toString() : "");
				index++;
			}

			String fileName = "BOSS-export-" + sdfFileName.format(new Date()) + ".xls";
			File exportFile = new File(fileName);
			OutputStream out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			logger.info("New PGW export file has been created at:");
			logger.info(exportFile.getAbsolutePath());
			downloadFile(request, response, exportFile, exportFile.getName());
			exportFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, File operationLogFile,
			String operationLogName) throws Exception {
		// 下载日志
		request.setCharacterEncoding("UTF-8");
		InputStream is = null;
		OutputStream os = null;

		try {
			long fileLength = operationLogFile.length();

			response.setContentType("application/octet-stream");

			// 如果客户端为IE
			// System.out.println(request.getHeader("User-Agent"));
			if (request.getHeader("User-Agent").indexOf("Trident") != -1) {
				operationLogName = java.net.URLEncoder.encode(operationLogName, "UTF-8");
			} else {
				operationLogName = new String(operationLogName.getBytes("UTF-8"), "iso-8859-1");
			}

			response.setHeader("Content-disposition", "attachment; filename=" + operationLogName);
			response.setHeader("Content-Length", String.valueOf(fileLength));

			is = new FileInputStream(operationLogFile);
			os = response.getOutputStream();

			byte[] b = new byte[1024];
			int len = 0;
			while ((len = is.read(b)) != -1) {
				os.write(b, 0, len);
			}
			os.flush();
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
	}

	@Override
	public void testDeleteIndex() throws IOException, InterruptedException {

    	// execute shell script
    	//curl -XDELETE 'localhost:9200/mock-index-2018-01-11'
//    	RestClient lowLevelRestClient = RestClient.builder(
//                new HttpHost("172.16.73.52", 9200, "http")).build();
//        RestHighLevelClient client =
//                new RestHighLevelClient(lowLevelRestClient);
		logger.info("Try to delete index");
        File deleteScript = new File("/home/fileshare/bossdata/espn/delete.sh");
        if(!deleteScript.exists()){
        	deleteScript.createNewFile();
        }
        FileWriter fw = new FileWriter(deleteScript);
        String deleteCmd = "curl -XDELETE 'localhost:9200/mock-index-2018-01-13'";
        fw.write(deleteCmd);
        fw.close();
        Process rsyncProcess = Runtime.getRuntime().exec("sh"+" "+deleteScript.getAbsolutePath());
		rsyncProcess.waitFor();
		InputStreamReader in = new InputStreamReader(rsyncProcess.getErrorStream());
		BufferedReader br = new BufferedReader(in);
		String callbackLine = null;
        while ((callbackLine = br.readLine()) != null) {
				System.out.println(callbackLine);
		}
    
		
	}

}
