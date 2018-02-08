package com.nokia.ices.app.dhss.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.CustomSetting;
import com.nokia.ices.app.dhss.service.BossQueryService;

@RestController
@RequestMapping("/api/v1")
public class BossQueryController {
	private static final Logger LOGGER = LogManager.getLogger(BossQueryController.class);
	private static DateTimeFormatter Date_Time_format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");   
	@Autowired
	BossQueryService bossQueryService;
	@Autowired
	CustomSetting customSetting;
	@RequestMapping(value = "/boss/statistic",method = RequestMethod.GET)
	public Map<String,Object> getBossStatistic(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate,
			@RequestParam (value = "hlrsn", required = false) String hlrsn,
			@RequestParam (value = "grain", required = false) String grain,
			@RequestParam (value = "businessType", required = false) String businessType
			){
		//TODO get statistic data
		Map<String,Object> paramMap = new HashMap<>();
		DateTime startDt = DateTime.parse(startDate, Date_Time_format);
		paramMap.put("startDate", startDt);
		DateTime endDt = DateTime.parse(endDate, Date_Time_format);
		paramMap.put("endDate", endDt);
		paramMap.put("grain", "15");
		if(StringUtils.isNotBlank(hlrsn) && !hlrsn.equals("undefined") && !hlrsn.equals("null")){
			paramMap.put("hlrsn", hlrsn);
		}
		if(StringUtils.isNotBlank(grain) && !grain.equals("undefined") && !grain.equals("null")){
			paramMap.put("grain", grain);	
		}
		if(StringUtils.isNotBlank(businessType) && !businessType.equals("undefined") && !businessType.equals("null")){
			paramMap.put("businessType", businessType);
		}
		
		return bossQueryService.getBossStatistic(paramMap);
	}
	@RequestMapping(value = "/boss/hlrsn",method = RequestMethod.GET)
	public List<String> getHlrsnList(){
		return bossQueryService.getHlrsnList();
	}
	@RequestMapping(value = "/boss/command",method = RequestMethod.GET)
	public List<String> getCommandList(){
		return bossQueryService.getCommandList();
	}
	@RequestMapping(value = "/boss/business",method = RequestMethod.GET)
	public List<Map<String, Object>> getbusinessList(){
		return bossQueryService.getbusinessList();
	}
	@RequestMapping(value = "/boss/errorcode",method = RequestMethod.GET)
	public List<Map<String,Object>> getErrorCodeList(){
		return bossQueryService.getErrorCodeList();
	}
	@RequestMapping(value = "/boss/data",method = RequestMethod.GET)
	public Map<String,Object> getBossDataByCondition(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate,
			@RequestParam (value = "hlrsn", required = false) String hlrsn,
			@RequestParam (value = "operationName", required = false) String operationName,
			@RequestParam (value = "resultType", required = false) String resultType,
			@RequestParam (value = "errorCode", required = false) String errorCode,
			@RequestParam (value = "numberString", required = false) String numberString,
			@RequestParam (value = "page", required = false) Integer page,
			@RequestParam (value = "size", required = false) Integer size
			){
		
		Map<String,Object> paramMap = new HashMap<>();
		DateTime startDt = DateTime.parse(startDate, Date_Time_format);
		paramMap.put("startDate", startDt);
		DateTime endDt = DateTime.parse(endDate, Date_Time_format);
		paramMap.put("endDate", endDt);
		if(page == null){
			LOGGER.info("Page parameter is not received,return 1st page");
			page = 0;
		}
		if(size == null){
			LOGGER.info("Size parameter is not received,set to default :15");
			size = 15;
		}
		paramMap.put("page", page);
		paramMap.put("size", size);
		
		if(StringUtils.isNotBlank(hlrsn) && !hlrsn.equals("undefined") && !hlrsn.equals("null")){
			paramMap.put("hlrsn", hlrsn);
		}
		if(StringUtils.isNotBlank(operationName) && !operationName.equals("undefined") && !operationName.equals("null")){
			paramMap.put("operationName", operationName);
		}
		if(StringUtils.isNotBlank(resultType) && !resultType.equals("undefined") && !resultType.equals("null")){
			paramMap.put("resultType", resultType);
		}
		if(StringUtils.isNotBlank(errorCode) && !errorCode.equals("undefined") && !errorCode.equals("null")){
			paramMap.put("errorCode", errorCode);
		}
		if(StringUtils.isNotBlank(numberString) && !numberString.equals("undefined") && !numberString.equals("null")){
			paramMap.put("numberString", numberString);
		}
		return bossQueryService.getBossDataByCondition(paramMap);
	}
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/boss/export/download",method = RequestMethod.GET)
	public void exportBossDataByCondition(
			@RequestParam (value = "startDate", required = false) String startDate, 
			@RequestParam (value = "endDate", required = false) String endDate,
			@RequestParam (value = "hlrsn", required = false) String hlrsn,
			@RequestParam (value = "operationName", required = false) String operationName,
			@RequestParam (value = "resultType", required = false) String resultType,
			@RequestParam (value = "errorCode", required = false) String errorCode,
			@RequestParam (value = "numberString", required = false) String numberString,
			HttpServletRequest request,HttpServletResponse response
			){
		Map<String,Object> exportParam = new HashMap<>();
		DateTime startDt = DateTime.parse(startDate, Date_Time_format);
		exportParam.put("startDate", startDt);
		DateTime endDt = DateTime.parse(endDate, Date_Time_format);
		exportParam.put("endDate", endDt);
		exportParam.put("page", 0);
		if(StringUtils.isNotBlank(hlrsn) && !hlrsn.equals("undefined") && !hlrsn.equals("null")){
			exportParam.put("hlrsn", hlrsn);
		}
		if(StringUtils.isNotBlank(operationName) && !operationName.equals("undefined") && !operationName.equals("null")){
			exportParam.put("operationName", operationName);
		}
		if(StringUtils.isNotBlank(resultType) && !resultType.equals("undefined") && !resultType.equals("null")){
			exportParam.put("resultType", resultType);
		}
		if(StringUtils.isNotBlank(errorCode) && !errorCode.equals("undefined") && !errorCode.equals("null")){
			exportParam.put("errorCode", errorCode);
		}
		if(StringUtils.isNotBlank(numberString) && !numberString.equals("undefined") && !numberString.equals("null")){
			exportParam.put("numberString", numberString);
		}
		if(customSetting.getExportLimit()>10000){
			exportParam.put("size", 10000);
		}
		List<Map<String,Object>> dataHits = (List<Map<String,Object>>)bossQueryService.getBossDataByCondition(exportParam).get("hits");
		bossQueryService.downloadExportData(dataHits,request,response);
	}
	@RequestMapping(value = "/boss/test/delete")
	public void testToDeleteIndex() throws IOException, InterruptedException{
		bossQueryService.testDeleteIndex();
	}
}
