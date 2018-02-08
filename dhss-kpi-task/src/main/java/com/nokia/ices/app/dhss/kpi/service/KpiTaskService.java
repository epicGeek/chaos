package com.nokia.ices.app.dhss.kpi.service;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;

public interface KpiTaskService {

	public List<KpiConfig> getKpiConfigList();

	public boolean connectVertify();

	public Map<String, Integer> loadDataByKpiDefineList(List<KpiConfig> kpiDefineList);

//	public Map<String, Map<String, Object>> getEquipmentUnitMap();
//
//	public Map<String, Map<String, Object>> getEquipmentUnitMapUnitNameKey();

	public Map<String, Map<String, Object>> getEquipmentNeMap();

	public List<Map<String, Object>> getEquipmentUnitMapList();

	public void deleteOldData();

}
