package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.HomeNavItem;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;

public interface HomeService {
	
	public List<String> findDayAlarmCount();
	
	public List<KpiConfig> findKpiItems(Map<String, Object> paramMap);
	

	public List<KpiMonitor> findKpiMonitor(Map<String, Object> paramMap);

	
	public List<HomeNavItem> findHomeNavItem(String token);

	public List<Map<String, Object>> findKpiCount(String kpiCodeString);
	
}

