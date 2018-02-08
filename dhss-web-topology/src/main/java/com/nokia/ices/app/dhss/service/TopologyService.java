package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.domain.topology.AhubConnInfo;

public interface TopologyService {
	
	public Set<String> findDhssList(String token);
	
	public List<Map<String,String>> findallNe(String token,String flag);
	
	public  Iterable<EquipmentUnit> findEquipmentUnitList(String token);

	public List<KpiMonitor> getfindKpiResultList(Map<String, Object> paramMap);

	public List<AlarmReceiveRecord> getfindAlarmResultList(Map<String, Object> paramMap);

	public List<AhubConnInfo> getfindAhubResultList(Map<String, Object> paramMap);

	public List<Map<String, String>> findResource(String token, String flag, String resourceFlag, boolean isPerssion);
	
	public Map<String, Object> findDhssList(String token,String dhss);
	
	public Map<String, Object> findAlarmResult(@PathVariable String params,@PathVariable String token,@PathVariable String dhss);
	
	public EquipmentUnit findUnitById(Long id);
	
	public EquipmentUnit saveEquipmentUnit(EquipmentUnit unit);

}
