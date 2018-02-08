package com.nokia.ices.app.pgw.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.pgw.service.PgwLogDataQueryService;

@RestController
public class PgwLogDataQueryController {
	
	@Autowired
	PgwLogDataQueryService pgwQueryService;
	
	private static final Logger LOGGER = LogManager.getLogger(PgwLogDataQueryController.class);
	
	@RequestMapping("api/v1/pgw-log/query-data-table")
	public List<Map<String,Object>> getPgwData(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate, 
			@RequestParam (value = "resultType", required = false) String resultType, 
			@RequestParam (value = "pgwName", required = false) String pgwName, 
			@RequestParam (value = "instanceName", required = false) String instanceName ,
			@RequestParam (value = "pgwQueryString", required = false) String pgwQueryString ,
			@RequestParam (value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam (value = "pageSize", required = false) Integer pageSize,
			@RequestParam (value = "keyword", required = false) String keyword 
			){
		Map<String,Object> paramMap = new HashMap<>();
		if(StringUtils.isNotBlank(startDate)){
			paramMap.put("startDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			paramMap.put("endDate", endDate);
		}
		if(StringUtils.isNotBlank(resultType)){
			paramMap.put("resultType", resultType);
		}
		if(StringUtils.isNotBlank(pgwName)){
			paramMap.put("pgwName", pgwName);
		}
		if(StringUtils.isNotBlank(instanceName)){
			paramMap.put("instanceName", instanceName);
		}
//		if(StringUtils.isNotBlank(userNumber)){
//			paramMap.put("userNumber", userNumber);
//		}
		if(StringUtils.isNotBlank(pgwQueryString)){
			paramMap.put("pgwQueryString", pgwQueryString);
		}
		if(StringUtils.isNotBlank(keyword) && !keyword.equalsIgnoreCase("undefined")){
			paramMap.put("keyword", keyword);
		}
		LOGGER.info("QUERY PARAMS:");
		LOGGER.info(paramMap.toString());
		
		paramMap.put("pageNumber", pageNumber);
		paramMap.put("pageSize", pageSize);
		return pgwQueryService.getPgwDataTableByCondition(paramMap);
	}
	@RequestMapping("api/v1/pgw-log/query-data-table-count")
	public Integer getPgwDataPageableCount(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate, 
			@RequestParam (value = "resultType", required = false) String resultType, 
			@RequestParam (value = "pgwName", required = false) String pgwName, 
			@RequestParam (value = "instanceName", required = false) String instanceName ,
//			@RequestParam (value = "userNumber", required = false) String userNumber ,
			@RequestParam (value = "pgwQueryString", required = false) String pgwQueryString ,
			@RequestParam (value = "keyword", required = false) String keyword 
			){
		Map<String,Object> paramMap = new HashMap<>();
		if(StringUtils.isNotBlank(startDate)){
			paramMap.put("startDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			paramMap.put("endDate", endDate);
		}
		if(StringUtils.isNotBlank(resultType)){
			paramMap.put("resultType", resultType);
		}
		if(StringUtils.isNotBlank(pgwName)){
			paramMap.put("pgwName", pgwName);
		}
		if(StringUtils.isNotBlank(instanceName)){
			paramMap.put("instanceName", instanceName);
		}
//		if(StringUtils.isNotBlank(userNumber)){
//			paramMap.put("userNumber", userNumber);
//		}
		if(StringUtils.isNotBlank(pgwQueryString)){
			paramMap.put("pgwQueryString", pgwQueryString);
		}
		if(StringUtils.isNotBlank(keyword) && !keyword.equalsIgnoreCase("undefined")){
			paramMap.put("keyword", keyword);
		}
		return pgwQueryService.getRealPageableCount(paramMap);
	}
	@RequestMapping("api/v1/pgw-log/pgw-xml-text")
	public Map<String,Object> getPgwXmlText(
			@RequestParam (value = "requestId", required = false) String requestId
			){
		String xmlText = pgwQueryService.getPgwXmlLogByRequestId(requestId);
		Map<String,Object> map = new HashMap<>();
		map.put("value", xmlText);
		return map;
	}
	@RequestMapping("api/v1/pgw-log/dropdown-data")
	public Map<String,List<String>> getDropdownListData(){
		//key: pgw name  value: instance name(split by comma)
		Map<String,List<String>> dropdownListMap = new HashMap<>();
		dropdownListMap = pgwQueryService.getDropdownListData();
		return dropdownListMap;
	}
	

	
	@RequestMapping(value="/api/v1/pgw-log/pgw-export-report-download")
    public void exportPgwReport(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate, 
			@RequestParam (value = "resultType", required = false) String resultType, 
			@RequestParam (value = "pgwName", required = false) String pgwName, 
			@RequestParam (value = "instanceName", required = false) String instanceName ,
//			@RequestParam (value = "userNumber", required = false) String userNumber ,
			@RequestParam (value = "pgwQueryString", required = false) String pgwQueryString ,
			@RequestParam (value = "isExportLog", required = false) Boolean isExportLog,
			@RequestParam (value = "keyword", required = false) String keyword ,
			HttpServletRequest request,HttpServletResponse response)throws Exception{
		Map<String,String> paramMap = new HashMap<>();
		if(StringUtils.isNotBlank(startDate)){
			paramMap.put("startDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			paramMap.put("endDate", endDate);
		}
		if(StringUtils.isNotBlank(resultType)){
			paramMap.put("resultType", resultType);
		}
		if(StringUtils.isNotBlank(pgwName)){
			paramMap.put("pgwName", pgwName);
		}
		if(StringUtils.isNotBlank(instanceName)){
			paramMap.put("instanceName", instanceName);
		}
//		if(StringUtils.isNotBlank(userNumber)){
//			paramMap.put("userNumber", userNumber);
//		}
		if(StringUtils.isNotBlank(pgwQueryString)){
			paramMap.put("pgwQueryString", pgwQueryString);
		}
		if(isExportLog==null||isExportLog.equals("null")||isExportLog.equals("undefined")){
			paramMap.put("isExportLog", "false");
		}else{
			paramMap.put("isExportLog", isExportLog.toString());
		}
		if(StringUtils.isNotBlank(keyword) && !keyword.equalsIgnoreCase("undefined")){
			paramMap.put("keyword", keyword);
		}
		pgwQueryService.exportPgwReport(paramMap,request,response);
	}
	@RequestMapping(value="/api/v1/pgw-log/export-confirm")
    public Integer showExportLimit(){
		return pgwQueryService.getExportLimit();
	}
	
	@RequestMapping(value = "/api/v1/pgw-log/search-log-mode")
	public Boolean isSearchLogMode(){
		Boolean isSearchLogMode = pgwQueryService.isSearchLogMode();
		System.out.println("is search log mode:"+isSearchLogMode);
		return isSearchLogMode;
	}
	
	@RequestMapping(value = "/api/v1/pgw-log/oldest-data-time")
	public Map<String,String> getOldestDataTime() throws ParseException{
		return pgwQueryService.getOldestDataTime();
	}
	@RequestMapping(value = "/api/v1/greetings")
	public String greetings() throws ParseException{
		return "Hello,I'm PGW";
	}
}
