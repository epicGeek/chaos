package com.nokia.ices.app.pgw.service.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.pgw.config.PgwLogDataQueryConfig;
import com.nokia.ices.app.pgw.service.PgwLogDataQueryService;

@Service
public class PgwLogDataQueryServiceImpl implements PgwLogDataQueryService {
	private static final Logger logger = LogManager.getLogger(PgwLogDataQueryServiceImpl.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	JdbcTemplate jdbcTemplatePgw;
	private static final SimpleDateFormat sdfFileName = new SimpleDateFormat("yyyyMMddHHmm");
	@Autowired
	PgwLogDataQueryConfig pgwLogDataQueryconfig;
	@Override
	public List<Map<String, Object>> getPgwDataTableByCondition(Map<String, Object> paramMap) {
		if(!paramMap.containsKey("keyword")){
			return getDataWithoutKeyword(paramMap);
		}else{
			return getDataWithKeyword(paramMap);
		}
		
	}
	private List<Map<String, Object>> getDataWithKeyword(Map<String, Object> paramMap) {
		// SELECT * FROM `pgw_detail_data` a,pgw_xml_log b  
		// WHERE a.imsi='466890047417494'   AND a.request_id=b.request_id AND  b.response_log like '%ALLPOS%'
		String sql = "SELECT * FROM `pgw_detail_data` a,pgw_xml_log b where (#number_condition#) ";
		List<Object> sqlParams = new ArrayList<>();
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&& !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			String numberCondition = "";

			for (String imsi : imsiList) {
				numberCondition = numberCondition + "a.imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				numberCondition = numberCondition + " or ";
			}
			for (String msisdn : msisdnList) {
				numberCondition = numberCondition + "a.msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				numberCondition = numberCondition+" or ";
			}
			for (String identifier : identifierList) {
				numberCondition = numberCondition + "a.user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			sql = sql.replace("#number_condition#", numberCondition);
		}
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null && !paramMap.get("instanceName").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}
		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null && !paramMap.get("resultType").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and a.response_time between ? and ? ";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		String keyword = paramMap.get("keyword").toString();
		//sqlParams.add(keyword);
		sql = sql + " and b.response_log like '%"+keyword+"%' ";
		sql = sql + " and a.request_id = b.request_id ";
		sql = sql + " order by a.response_time desc ";
		//Pageable
				Integer pageNumber = Integer.valueOf(paramMap.get("pageNumber").toString());
				Integer pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
				if(pageNumber!=null&&pageSize!=null){
					sql = sql +" limit "+((pageNumber)*pageSize)+","+pageSize;
				}


		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);
		for (Map<String,Object> dataMap : pgwQueryResultList) {
			Date responseTime = (Date)dataMap.get("response_time");
			String responseTimeStr = sdf.format(responseTime);
			dataMap.put("response_time", responseTimeStr);
		}
		logger.info("PGW QUERY SQL:");
		logger.info(sql);
		return pgwQueryResultList;
	}
	private List<Map<String, Object>> getDataWithoutKeyword(Map<String, Object> paramMap) {
		String sql = "select * from pgw_detail_data where 1=1 ";
		List<Object> sqlParams = new ArrayList<>();
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&& !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
//			sql = sql + " and user_number = ?";
//			sqlParams.add(paramMap.get("userNumber"));
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			if(imsiList.size()+msisdnList.size()+identifierList.size()>0){
				sql = sql+" and ";
			}

			for (String imsi : imsiList) {
				sql = sql + "imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String msisdn : msisdnList) {
				sql = sql + "msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String identifier : identifierList) {
				sql = sql + "user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}

		}
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")){
			sql = sql + " and pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null && !paramMap.get("instanceName").toString().equalsIgnoreCase("null")){
			sql = sql + " and instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}

		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null && !paramMap.get("resultType").toString().equalsIgnoreCase("null")){
			sql = sql + " and result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and response_time between ? and ? ";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		sql = sql + " order by response_time desc ";
		//Pageable
		Integer pageNumber = Integer.valueOf(paramMap.get("pageNumber").toString());
		Integer pageSize = Integer.valueOf(paramMap.get("pageSize").toString());
		if(pageNumber!=null&&pageSize!=null){
			sql = sql +" limit "+((pageNumber)*pageSize)+","+pageSize;
		}
		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);
		for (Map<String,Object> dataMap : pgwQueryResultList) {
			Date responseTime = (Date)dataMap.get("response_time");
			String responseTimeStr = sdf.format(responseTime);
			dataMap.put("response_time", responseTimeStr);
		}
		logger.info("PGW QUERY SQL:");
		logger.info(sql);
		return pgwQueryResultList;
	}
	@Override
	public Integer getRealPageableCount(Map<String, Object> paramMap) {
		if(!paramMap.containsKey("keyword")){
			return getCountWithoutKeyword(paramMap);
		}else{
			return getCountWithKeyword(paramMap);
		}
	}
	private Integer getCountWithKeyword(Map<String, Object> paramMap) {

		// SELECT * FROM `pgw_detail_data` a,pgw_xml_log b  
		// WHERE a.imsi='466890047417494'   AND a.request_id=b.request_id AND  b.response_log like '%ALLPOS%'
		String sql = "SELECT count(*) as c FROM `pgw_detail_data` a,pgw_xml_log b where (#number_condition#) ";
		
		List<Object> sqlParams = new ArrayList<>();
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&& !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			String numberCondition = "";

			for (String imsi : imsiList) {
				numberCondition = numberCondition + "a.imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				numberCondition = numberCondition + " or ";
			}
			for (String msisdn : msisdnList) {
				numberCondition = numberCondition + "a.msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				numberCondition = numberCondition+" or ";
			}
			for (String identifier : identifierList) {
				numberCondition = numberCondition + "a.user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(numberCondition.endsWith("or ")){
				numberCondition = numberCondition.substring(0, numberCondition.length()-3);
			}
			
			sql = sql.replace("#number_condition#", numberCondition);
		}
		
		
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null && !paramMap.get("instanceName").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}
		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null && !paramMap.get("resultType").toString().equalsIgnoreCase("null")){
			sql = sql + " and a.result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and a.response_time between ? and ? ";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		String keyword = paramMap.get("keyword").toString();
		// sqlParams.add(keyword);
		sql = sql + " and a.request_id = b.request_id ";
		sql = sql + " and b.response_log like '%"+keyword+"%' ";


		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);

		logger.info("PGW QUERY SQL:");
		logger.info(sql);
		Integer counter = Integer.valueOf(pgwQueryResultList.get(0).get("c").toString());
		return counter;
	
	}
	private Integer getCountWithoutKeyword(Map<String, Object> paramMap) {
		String sql = "select count(*) as c from pgw_detail_data where 1=1 ";
		List<Object> sqlParams = new ArrayList<>();
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")&& !paramMap.get("pgwName").toString().equalsIgnoreCase("undefined")){
			sql = sql + " and pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null&& !paramMap.get("instanceName").toString().equalsIgnoreCase("null")&& !paramMap.get("instanceName").toString().equalsIgnoreCase("undefined")){
			sql = sql + " and instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&& !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
//			sql = sql + " and user_number = ?";
//			sqlParams.add(paramMap.get("userNumber"));
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			if(imsiList.size()+msisdnList.size()+identifierList.size()>0){
				sql = sql+" and ";
			}

			for (String imsi : imsiList) {
				sql = sql + "imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String msisdn : msisdnList) {
				sql = sql + "msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String identifier : identifierList) {
				sql = sql + "user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}

		}
		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null&& !paramMap.get("resultType").toString().equalsIgnoreCase("null")&& !paramMap.get("resultType").toString().equalsIgnoreCase("undefined")){
			sql = sql + " and result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and response_time between ? and ?";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);
		logger.info("PGW Pageable number QUERY SQL:");
		logger.info(sql);
		Integer counter = Integer.valueOf(pgwQueryResultList.get(0).get("c").toString());
		return counter;
	}
	@Override
	public Map<String, List<String>> getDropdownListData() {
		String sql = "select pgw_name as p,instance_name as i from pgw_basic_info";
		List<Map<String,Object>> dropdownDataList = jdbcTemplatePgw.queryForList(sql);
		if(dropdownDataList.size()==0){
			return null;
		}
		Map<String, List<String>> dropdownDataMap = new HashMap<>();
		for (Map<String, Object> pgwNameAndInstanceRefMap: dropdownDataList) {
			String pgwName = pgwNameAndInstanceRefMap.get("p").toString();
			String instanceNameSerie = pgwNameAndInstanceRefMap.get("i").toString();
			List<String> instanceNameList = new ArrayList<>();
			String[] instanceNameArray = instanceNameSerie.split(",");
			for (String instanceName : instanceNameArray) {
				instanceNameList.add(instanceName);
			}
			dropdownDataMap.put(pgwName, instanceNameList);
		}
		return dropdownDataMap;
	}
	@Override
	public String getPgwXmlLogByRequestId(String requestId) {
		if(StringUtils.isBlank(requestId)){
			return "";
		}
		String sql = "select response_log as r from pgw_xml_log where 1=1 and request_id = ? limit 1";
		String pgwXmlText = "";
		List<Map<String,Object>> resultList = jdbcTemplatePgw.queryForList(sql, requestId);
		if(resultList.size()==0){
			return "";
		}else{
			pgwXmlText = resultList.get(0).toString()==null?"":resultList.get(0).get("r").toString();
			return pgwXmlText;
		}

	}
	@Override
	public void exportPgwReport(Map<String, String> paramMap, HttpServletRequest request,
			HttpServletResponse response) {
		String isExportLog = paramMap.get("isExportLog");
		if(!isExportLog.equals("true")){
			logger.info("start to export pgw report with 'NO LOG'");
			Long start = System.currentTimeMillis();
			List<Map<String,Object>> dataList = getBasicData(paramMap);
			Long mid = System.currentTimeMillis();
			exportData(dataList, request, response,paramMap);
			Long end = System.currentTimeMillis();
			logger.info("Query time:"+(mid - start) + "ms");
			logger.info("Export time:"+(end - mid) + "ms");
			logger.info("All time:"+(end - start) + "ms");
		}else{
			logger.info("start to export pgw report with 'LOG'");
			Long start = System.currentTimeMillis();
			List<Map<String,Object>> dataList = getBasicDataWithLog(paramMap);
			Long mid = System.currentTimeMillis();
			exportDataWithLog(dataList, request, response,paramMap);
			Long end = System.currentTimeMillis();
			logger.info("Query time:"+(mid - start) + "ms");
			logger.info("Export time:"+(end - mid) + "ms");
			logger.info("All time:"+(end - start) + "ms");
		}


	}
	private List<Map<String, Object>> getBasicDataWithLog(Map<String, String> paramMap) {

		List<Object> sqlParams = new ArrayList<>();
		String sql = "SELECT\n" +
				"	pgw_detail_data.*, pgw_xml_log.response_log\n" +
				"FROM\n" +
				"	pgw_detail_data\n" +
				"LEFT JOIN pgw_xml_log ON pgw_detail_data.request_id = pgw_xml_log.request_id\n" +
				"WHERE\n" +
				"	1 = 1";
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")){
			sql = sql + " and pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null && !paramMap.get("instanceName").toString().equalsIgnoreCase("null")){
			sql = sql + " and instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&&!paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
//			sql = sql + " and user_number = ?";
//			sqlParams.add(paramMap.get("userNumber"));
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			if(imsiList.size()+msisdnList.size()+identifierList.size()>0){
				sql = sql+" and ";
			}

			for (String imsi : imsiList) {
				sql = sql + "imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String msisdn : msisdnList) {
				sql = sql + "msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String identifier : identifierList) {
				sql = sql + "user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}

		}
		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null && !paramMap.get("resultType").toString().equalsIgnoreCase("null")){
			sql = sql + " and result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and pgw_detail_data.response_time between ? and ? ";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		sql = sql + "order by response_time desc ";
		Integer limit = pgwLogDataQueryconfig.getExportRecordLimit();
		sql = sql + "limit " +limit;
		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);
		return pgwQueryResultList;

	}
	private boolean exportDataWithLog(List<Map<String, Object>> dataList, HttpServletRequest request,
			HttpServletResponse response, Map<String, String> paramMap) {

		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] headers = { "Response time", "PGW name", "Instance name", "User name", "Request ID", "Result type", "Error code","Error Message"
					,"Execution time" , "Identifier" ,"IMSI","MSISDN", "Operation","response log" };
			XSSFSheet sheet = workbook.createSheet("PGW-SPML-command log data");
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
//			style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			for (int i = 0; i < headers.length; i++) {//写title
				XSSFCell cell = row.createCell(i);
				XSSFRichTextString text = new XSSFRichTextString(headers[i]);
				cell.setCellStyle(style);
				cell.setCellValue(text);
			}

			int index = 1;
			logger.info("Start to generate PGW export-report");
			for (Map<String,Object> pgwData : dataList) {
				row = sheet.createRow(index);
				row.createCell(0).setCellValue(pgwData.get("response_time")!=null?pgwData.get("response_time").toString():"");
				row.createCell(1).setCellValue(pgwData.get("pgw_name")!=null?pgwData.get("pgw_name").toString():"");
				row.createCell(2).setCellValue(pgwData.get("instance_name")!=null?pgwData.get("instance_name").toString():"");
				row.createCell(3).setCellValue(pgwData.get("user_name")!=null?pgwData.get("user_name").toString():"");
				row.createCell(4).setCellValue(pgwData.get("request_id")!=null?pgwData.get("request_id").toString():"");
				row.createCell(5).setCellValue(pgwData.get("result_type")!=null?pgwData.get("result_type").toString():"");
				row.createCell(6).setCellValue(pgwData.get("error_code")!=null?pgwData.get("error_code").toString():"");
				row.createCell(7).setCellValue(pgwData.get("error_message")!=null?pgwData.get("error_message").toString():"");
				row.createCell(8).setCellValue(pgwData.get("execution_time")!=null?pgwData.get("execution_time").toString():"");
				row.createCell(9).setCellValue(pgwData.get("user_number")!=null?pgwData.get("user_number").toString():"");
				row.createCell(10).setCellValue(pgwData.get("imsi")!=null?pgwData.get("imsi").toString():"");
				Object msisdn = pgwData.get("msisdn");
				if(msisdn == null || msisdn.toString().equals("0")){
					msisdn = "";
				}
				row.createCell(11).setCellValue(msisdn.toString());
				row.createCell(12).setCellValue(pgwData.get("operation")!=null?pgwData.get("operation").toString():"");
				String responseLog = pgwData.get("response_log")!=null?pgwData.get("response_log").toString():"";
				row.createCell(13).setCellValue(responseLog);
				index++;
			}

			String fileName = "PGW-SPML-DHSS-export-with-log" + sdfFileName.format(new Date()) + ".xls";
			File exportFile = new File(fileName);
			OutputStream out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			logger.info("New PGW export file has been created at:");
			logger.info(exportFile.getAbsolutePath());
			downloadFile(request, response ,exportFile,exportFile.getName());
			exportFile.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}



	}
	private List<Map<String,Object>> getBasicData(Map<String, String> paramMap) {
		List<Object> sqlParams = new ArrayList<>();
		String sql = "select * from pgw_detail_data where 1=1 ";
		if(paramMap.containsKey("pgwName") && paramMap.get("pgwName")!=null && !paramMap.get("pgwName").toString().equalsIgnoreCase("null")){
			sql = sql + " and pgw_name = ?";
			sqlParams.add(paramMap.get("pgwName"));
		}
		if(paramMap.containsKey("instanceName") && paramMap.get("instanceName")!=null && !paramMap.get("instanceName").toString().equalsIgnoreCase("null")){
			sql = sql + " and instance_name = ?";
			sqlParams.add(paramMap.get("instanceName"));
		}
		if(paramMap.containsKey("pgwQueryString") && paramMap.get("pgwQueryString")!=null && !paramMap.get("pgwQueryString").toString().equalsIgnoreCase("null")&&!paramMap.get("pgwQueryString").toString().equalsIgnoreCase("undefined")){
//			sql = sql + " and user_number = ?";
//			sqlParams.add(paramMap.get("userNumber"));
			String pgwQueryString = paramMap.get("pgwQueryString").toString();
			List<String> imsiList = inputNumberCategory(pgwQueryString).get("imsi");
			List<String> msisdnList = inputNumberCategory(pgwQueryString).get("msisdn");
			List<String> identifierList = inputNumberCategory(pgwQueryString).get("identifier");
			if(imsiList.size()+msisdnList.size()+identifierList.size()>0){
				sql = sql+" and ";
			}

			for (String imsi : imsiList) {
				sql = sql + "imsi = ? or ";
				sqlParams.add(imsi);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String msisdn : msisdnList) {
				sql = sql + "msisdn = ? or ";
				sqlParams.add(msisdn);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}
			if(identifierList.size()>0&&msisdnList.size()>0&&imsiList.size()>0){
				sql = sql+" or ";
			}
			for (String identifier : identifierList) {
				sql = sql + "user_number = ? or ";
				sqlParams.add(identifier);
			}
			if(sql.endsWith("or ")){
				sql = sql.substring(0, sql.length()-3);
			}

		}
		if(paramMap.containsKey("resultType") && paramMap.get("resultType")!=null && !paramMap.get("resultType").toString().equalsIgnoreCase("null")){
			sql = sql + " and result_type = ?";
			if(paramMap.get("resultType").toString().equalsIgnoreCase("success")){
				sqlParams.add("success");
			}else{
				sqlParams.add("failure");
			}
		}
		String startDate = paramMap.get("startDate").toString();
		String endDate = paramMap.get("endDate").toString();
		if(paramMap.containsKey("startDate")&&paramMap.containsKey("startDate")){
			if(startDate!=null&&endDate!=null){
				sql = sql + " and response_time between ? and ? ";
			}
			sqlParams.add(startDate);
			sqlParams.add(endDate);
		}
		sql = sql + "order by response_time desc ";
		Integer limit = pgwLogDataQueryconfig.getExportRecordLimit();
		sql = sql + "limit " +limit;
		List<Map<String,Object>> pgwQueryResultList = new ArrayList<>();
		Object[] paramArray = new Object[sqlParams.size()];
		paramArray = sqlParams.toArray();
		pgwQueryResultList = jdbcTemplatePgw.queryForList(sql,paramArray);
		return pgwQueryResultList;
	}

	private void downloadFile(HttpServletRequest request, HttpServletResponse response, File operationLogFile,
			String operationLogName) throws Exception {
		// TODO Auto-generated method stub
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

	public Boolean exportData(List<Map<String,Object>> data, HttpServletRequest request, HttpServletResponse response,Map<String, String> paramMap) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			String[] headers = { "Response time", "PGW name", "Instance name", "User name", "Request ID", "Result type", "Error code","Error Message"
					,"Execution time" , "Identifier" ,"IMSI","MSISDN", "Operation" };
			XSSFSheet sheet = workbook.createSheet("PGW-SPML-command log data");
			XSSFRow row = sheet.createRow(0);
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(new XSSFColor(new Color(0xffffff00)));
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style.setAlignment(HorizontalAlignment.CENTER);
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			for (int i = 0; i < headers.length; i++) {//写title
				XSSFCell cell = row.createCell(i);
				XSSFRichTextString text = new XSSFRichTextString(headers[i]);
				cell.setCellStyle(style);
				cell.setCellValue(text);
			}

			int index = 1;
			logger.info("Start to generate PGW export-report");
			for (Map<String,Object> pgwData : data) {
				row = sheet.createRow(index);
				row.createCell(0).setCellValue(pgwData.get("response_time")!=null?pgwData.get("response_time").toString():"");
				row.createCell(1).setCellValue(pgwData.get("pgw_name")!=null?pgwData.get("pgw_name").toString():"");
				row.createCell(2).setCellValue(pgwData.get("instance_name")!=null?pgwData.get("instance_name").toString():"");
				row.createCell(3).setCellValue(pgwData.get("user_name")!=null?pgwData.get("user_name").toString():"");
				row.createCell(4).setCellValue(pgwData.get("request_id")!=null?pgwData.get("request_id").toString():"");
				row.createCell(5).setCellValue(pgwData.get("result_type")!=null?pgwData.get("result_type").toString():"");
				row.createCell(6).setCellValue(pgwData.get("error_code")!=null?pgwData.get("error_code").toString():"");
				row.createCell(7).setCellValue(pgwData.get("error_message")!=null?pgwData.get("error_message").toString():"");
				row.createCell(8).setCellValue(pgwData.get("execution_time")!=null?pgwData.get("execution_time").toString():"");
				row.createCell(9).setCellValue(pgwData.get("user_number")!=null?pgwData.get("user_number").toString():"");
				row.createCell(10).setCellValue(pgwData.get("imsi")!=null?pgwData.get("imsi").toString():"");
				Object msisdn = pgwData.get("msisdn");
				if(msisdn == null || msisdn.toString().equals("0")){
					msisdn = "";
				}
				row.createCell(11).setCellValue(msisdn.toString());
				row.createCell(12).setCellValue(pgwData.get("operation")!=null?pgwData.get("operation").toString():"");
				index++;
			}

			String fileName = "PGW-SPML-DHSS-export-" + sdfFileName.format(new Date()) + ".xls";
			File exportFile = new File(fileName);
			OutputStream out = new FileOutputStream(exportFile.getAbsolutePath());
			workbook.write(out);
			out.close();
			workbook.close();
			logger.info("New PGW export file has been created at:");
			logger.info(exportFile.getAbsolutePath());
			downloadFile(request, response ,exportFile,exportFile.getName());
			exportFile.delete();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private Map<String,List<String>> inputNumberCategory(String pgwQueryString){
		String mmc = pgwLogDataQueryconfig.getMmc();
		String countryCode = pgwLogDataQueryconfig.getCountryCode();
		String notStandardCountryCode = pgwLogDataQueryconfig.getNotStandardCountryCode();
		Map<String,List<String>> m = new HashMap<>();
		List<String> imsiList = new ArrayList<>();
		List<String> msisdnList = new ArrayList<>();
		List<String> identifierList = new ArrayList<>();
		String[] numbers = pgwQueryString.split(",");
		for (String number : numbers) {
			if(number.startsWith(mmc)){
				imsiList.add(number);
				logger.info("IMSI input:"+number);
				continue;
			}
			if(number.startsWith(countryCode)&&(number.length()==12 || number.length()==13)){
				msisdnList.add(number);//标准MSISDN
				logger.info("MSISDN input:"+number);
				continue;
			}
			if(number.startsWith(notStandardCountryCode)&&number.length()==10){
				msisdnList.add(number);//非标准的MSISDN，ADDORSET操作类型出现的MSISDN
				logger.info("Not standard MSISDN input:"+number);
				continue;
			}
			identifierList.add(number);
			logger.info("identifier input:"+number);
		}
		m.put("imsi", imsiList);
		m.put("msisdn", msisdnList);
		m.put("identifier", identifierList);
		return m;
	}
	@Override
	public Integer getExportLimit() {
		return pgwLogDataQueryconfig.getExportRecordLimit();
	}
	@Override
	public Boolean isSearchLogMode() {
		return pgwLogDataQueryconfig.getSearchLogMode();
	}
	@Override
	public Map<String,String> getOldestDataTime() throws ParseException {
		String sql = 
				"SELECT PARTITION_NAME,TABLE_ROWS\n" +
				"FROM INFORMATION_SCHEMA.PARTITIONS\n" +
				"WHERE TABLE_NAME = 'pgw_detail_data' order by PARTITION_NAME";
		List<Map<String,Object>> resultList = jdbcTemplatePgw.queryForList(sql);
		String oldestPartition = null;
		for (Map<String, Object> map : resultList) {
			String partitionName = map.get("PARTITION_NAME").toString();
			Integer tableRows = Integer.valueOf(map.get("TABLE_ROWS").toString());
			if(tableRows > 0){
				oldestPartition = partitionName.replace("p_", "");//20160607
				oldestPartition = oldestPartition.substring(0, 4)+"-"+oldestPartition.substring(4, 6)+"-"+oldestPartition.substring(6);
				break; //2017-09-12 00:00
			}
		}
		Map<String,String> m = new HashMap<>();
		m.put("oldest", oldestPartition);
		return m;
	}
}
