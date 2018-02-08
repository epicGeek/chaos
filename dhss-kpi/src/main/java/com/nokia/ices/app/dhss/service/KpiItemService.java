package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.controller.KpiParams;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;

public interface KpiItemService {
	
	public List<KpiConfig> findKpiItem(Map<String,Object> paramMap);
	
	public List<KpiMonitorHistory> findKpiMonitorHistory(Map<String,Object> paramMap);
	
	public void kpiStatistics(Map<String,Object> paramMap,List<KpiMonitorHistory> history,KpiConfig kpi);

	public Map<String, Object> kpiStatisticsByMoreGrains(KpiParams kpiParams);

}
