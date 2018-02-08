package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;
import com.nokia.ices.app.dhss.service.AlarmMonitorService;

@RestController
public class AlarmMonitorHistoryController {

	@Autowired
	AlarmMonitorService alarmMonitorService;
	private static final Logger logger = LoggerFactory.getLogger(AlarmMonitorHistoryController.class);
	
	
	@RequestMapping(value = "api/v1/alarm-receive-history/download", method = RequestMethod.GET)
	public void findAlarmReceiveHistory(HttpServletResponse response,
			@RequestParam(value="alarmUnit")String alarmUnit,
			@RequestParam(value="alarmCell")String alarmCell,
			@RequestParam(value="notifyId")String notifyId,
			@RequestParam(value="alarmNo")String alarmNo,
			@RequestParam(value="alarmLevel")String alarmLevel,
			@RequestParam(value="startTime")String startTime,
			@RequestParam(value="endTime")String endTime) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(alarmUnit)) {
			paramMap.put("alarmCell_LIKE", alarmUnit);
		}
		if (StringUtils.isNotEmpty(alarmCell)) {
			paramMap.put("neName_LIKE", alarmCell);
		}
		if (StringUtils.isNotEmpty(notifyId)) {
			paramMap.put("notifyId_LIKE",notifyId);
		}
		if (StringUtils.isNotEmpty(alarmNo)) {
			paramMap.put("alarmNo_LIKE", alarmNo);
		}
		if (StringUtils.isNotEmpty(alarmLevel)) {
			paramMap.put("alarmLevel_EQ", alarmLevel);
		}
		if (StringUtils.isNotEmpty(startTime)) {
			paramMap.put("receiveCancelTime_GE", format.parse(startTime));
		}
		if (StringUtils.isNotEmpty(endTime)) {
			paramMap.put("receiveCancelTime_LT", format.parse(endTime));
		}
		try {
			alarmMonitorService.exportData(alarmMonitorService.findAlarmMonitorHistory(paramMap),response);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	

	@RequestMapping(value = "api/v1/alarm-receive-history/query", method = RequestMethod.POST)
	public Page<AlarmReceiveHistory> findAlarmReceiveHistory(
			@RequestBody AlarmReceiveHistoryQuery alarmReceiveHistoryQuery) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getAlarmUnit())) {
			paramMap.put("alarmCell_LIKE", alarmReceiveHistoryQuery.getAlarmUnit());
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getAlarmCell())) {
			paramMap.put("neName_LIKE", alarmReceiveHistoryQuery.getAlarmCell());
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getNotifyId())) {
			paramMap.put("notifyId_LIKE", alarmReceiveHistoryQuery.getNotifyId());
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getAlarmNo())) {
			paramMap.put("alarmNo_LIKE", alarmReceiveHistoryQuery.getAlarmNo());
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getAlarmLevel())) {
			paramMap.put("alarmLevel_EQ", alarmReceiveHistoryQuery.getAlarmLevel());
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getStartTime())) {
			paramMap.put("receiveCancelTime_GE", format.parse(alarmReceiveHistoryQuery.getStartTime()));
		}
		if (StringUtils.isNotEmpty(alarmReceiveHistoryQuery.getEndTime())) {
			paramMap.put("receiveCancelTime_LT", format.parse(alarmReceiveHistoryQuery.getEndTime()));
		}
		logger.info(paramMap.toString());

		Pageable pageable = new PageRequest(alarmReceiveHistoryQuery.getPage(), alarmReceiveHistoryQuery.getSize(),new Sort(Direction.DESC, "receiveCancelTime"));

		return alarmMonitorService.findAlarmMonitorHistory(paramMap, pageable);
	}

}
