package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.domain.alarm.AlarmRule;
import com.nokia.ices.app.dhss.service.AlarmMonitorService;

@RestController
public class AlarmRuleController {
	
	
	@Autowired
	private AlarmMonitorService alarmMonitorService;
	
	@RequestMapping(value = "api/v1/alarm-rule/query", method = RequestMethod.POST)
	public Page<AlarmRule> findAlarmRule(
			@RequestBody AlarmRuleQuery alarmRuleQuery) throws ParseException {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(alarmRuleQuery.getAlarmNo())) {
			paramMap.put("alarmNo_LIKE", alarmRuleQuery.getAlarmNo());
		}
		if (StringUtils.isNotEmpty(alarmRuleQuery.getUnitType())) {
			paramMap.put("unitType_EQ", alarmRuleQuery.getUnitType());
		}
		Pageable pageable = new PageRequest(alarmRuleQuery.getPage(), alarmRuleQuery.getSize(),new Sort(Direction.DESC, "id"));
		return alarmMonitorService.findAlarmRule(paramMap, pageable);
	}
	
	@RequestMapping(value = "api/v1/alarm-rule/export-data-download")
	public void exportData(@RequestParam(value="alarmNo",required=true) String alarmNo,
			@RequestParam(value="unitType",required=true) String unitType,
			HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(alarmNo)&&!alarmNo.equals("undefined")) {
			paramMap.put("alarmNo_LIKE", alarmNo);
		}
		if (StringUtils.isNotEmpty(unitType)&&!unitType.equals("undefined")) {
			paramMap.put("unitType_EQ", unitType);
		}
		List<AlarmRule> exportData = alarmMonitorService.findExportAlarmRule(paramMap);
		alarmMonitorService.exportAlarmRule(exportData,request,response);
	}
	
	@RequestMapping(value = "/api/v1/alarm-rule/upload", method = RequestMethod.POST)
	public Map<String,String> getMultiQueryTemplate(
			@RequestParam("templateFile") MultipartFile multiQueryTemplate) {
		return alarmMonitorService.handleWithUploadFile(multiQueryTemplate);
	}
}
