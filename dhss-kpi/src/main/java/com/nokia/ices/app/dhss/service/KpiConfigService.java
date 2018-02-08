package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;

public interface KpiConfigService {

	public List<Map<String,Object>> getAllKpiConfig();
	public boolean deleteKpiConfig(Integer kpiConfigId);
	public String createNewKpiCode();
	public List<Map<String,Object>> kpiNameAndKpiCodeMapRef();
	public boolean addOrEditConfig(KpiConfig kpiConfig);
	public Iterable<KpiConfig> getAllKpiConfigFromJpaRepo();
	public Map<String,List<String>> neTypeAndKpiCategory();
	public Map<String,Map<String,String>> getKpiCodeAndNameMapsInstance();
}
