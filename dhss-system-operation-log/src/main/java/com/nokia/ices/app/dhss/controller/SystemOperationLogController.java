package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.system.SystemOperationLog;
import com.nokia.ices.app.dhss.service.SystemOperationLogService;

@RestController
public class SystemOperationLogController {
	private Logger logger = LoggerFactory.getLogger(SystemOperationLogController.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Autowired
	private SystemOperationLogService systemOperationLogService;
	@RequestMapping("api/v1/query-system-operation-log")
	public Page<SystemOperationLog> querySystemOperationLog(
			@RequestParam(value="startDate",required=false)String startDate,
			@RequestParam(value="endDate",required=false)String endDate,
			@RequestParam(value="userName",required=false)String userName,
			Pageable page
			){
		
		Map<String,Object> queryParamMap = new HashMap<>();
		try {
			if(StringUtils.isNotEmpty(startDate)){
				queryParamMap.put("operationTime_GE", SDF.parse(startDate));
			}
			if(StringUtils.isNotEmpty(startDate)){
				queryParamMap.put("operationTime_LT", SDF.parse(endDate));
			}
			if(StringUtils.isNotEmpty(userName)){
				queryParamMap.put("userName_LIKE", userName);
			}
			
			return systemOperationLogService.querySystemOperatonLog(queryParamMap, page);
		} catch (ParseException e) {
			e.getMessage();
			logger.info("Format date string failed.");
			logger.info("Start time:"+startDate);
			logger.info("End time:"+endDate);
			return null;
		}
	}
	@SuppressWarnings("rawtypes")
	@RequestMapping("api/v1/query-system-operation-log-remote")
	public List querySystemOperationLogFromRemoteServer(
			@RequestParam(value="starTime",required=false)String starTime,
			@RequestParam(value="endTime",required=false)String endTime,
			@RequestParam(value="userName",required=false)String userName,
			@RequestParam(value="token",required=false)String token
			){
		Map<String,Object> queryParamMap = new HashMap<>();
		queryParamMap.put("starTime", starTime);
		queryParamMap.put("endTime", endTime);
		if(StringUtils.isNotBlank(userName)&&!userName.equals("null")&&!userName.equals("undefined")){
			queryParamMap.put("username", userName);
		}else{
			queryParamMap.put("username", "");
		}
		queryParamMap.put("token", token);
		queryParamMap.put("appName", "DHSS");//项目名称过滤为DHSS
		
		return systemOperationLogService.querySystemOperatonLogFromRemoteServer(queryParamMap);
	}
	
	
	
	
}
