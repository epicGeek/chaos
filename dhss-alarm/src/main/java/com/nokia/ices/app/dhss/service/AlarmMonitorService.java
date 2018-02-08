package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.alarm.AlarmRule;
import com.nokia.ices.app.dhss.domain.alarm.UserAlarmMonitor;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

public interface AlarmMonitorService {

	public void returnDhssList(List<Map<String, Object>> root,String token);

	public void returnNeList(List<Map<String, Object>> root,String dhssName,String token);

	public void returnUnitList(List<Map<String, Object>> root,String neName,String token);

	public List<Map<String,String>> findEquipmentNe(String token);

	public List<EquipmentUnit> findEquipmentUnit(Map<String,Object> paramMap);

	public List<AlarmReceiveRecord> findAlarmReceiveRecord(Map<String,Object> mapParamMap);

	public List<UserAlarmMonitor> findUserAlarmMonitor(Map<String,Object> mapParamMap);

	public UserAlarmMonitor joinCollection(UserAlarmMonitor userAlarm,String token);

	public boolean cancelCollection(UserAlarmMonitor userAlarm,String token);

	public Page<AlarmMonitor> findAlarmMonitorCustom(Map<String,Specification<AlarmMonitor>> map , Pageable pageable);

	public Page<AlarmReceiveHistory> findAlarmMonitorHistory(Map<String, Object> paramMap, Pageable pageable);

	public List<AlarmReceiveHistory> findAlarmMonitorHistory(Map<String, Object> paramMap);

	public void exportData(List<AlarmReceiveHistory> data,HttpServletResponse response) throws ClassNotFoundException;

	public Page<AlarmRule> findAlarmRule(Map<String, Object> paramMap, Pageable pageable);

	public List<AlarmRule> findExportAlarmRule(Map<String, Object> paramMap);

	public void exportAlarmRule(List<AlarmRule> exportData, HttpServletRequest request, HttpServletResponse response);

	public Map<String, String> handleWithUploadFile(MultipartFile multiQueryTemplate);

	public Map<String, String> importHSSFE(MultipartFile multiQueryTemplate);
	public Map<String, String> importNTHLR(MultipartFile multiQueryTemplate);
	public Map<String, String> importOneNDS(MultipartFile multiQueryTemplate);
	
	public AlarmReceiveHistory cancelAlarm(AlarmReceiveRecord record);

	
	public List<String> getUnitList(String token);

}
