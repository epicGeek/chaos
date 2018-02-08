package com.nokia.ices.app.dhss.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.repository.kpi.KpiConfigRepository;
import com.nokia.ices.app.dhss.service.KpiConfigService;

@Service
public class KpiConfigServiceImpl implements KpiConfigService{
	private Map<String,Map<String,String>> kpiCodeAndNameMaps = null;
	@Autowired
	KpiConfigRepository kpiConfigRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Override
	public List<Map<String,Object>> getAllKpiConfig () {
		String sql = "select * from kpi_config";
		List<Map<String,Object>> resultList = new ArrayList<>();
		resultList = jdbcTemplate.queryForList(sql);
		return resultList;
	}
	@Override
	public boolean deleteKpiConfig(Integer kpiConfigId) {
		kpiConfigRepository.delete(Long.valueOf(kpiConfigId));
		return true;
	}
	@Override
	public String createNewKpiCode() {
		String sql = "select max(id) as m from kpi_config";
		List<Map<String,Object>> l = new ArrayList<>();
		l = jdbcTemplate.queryForList(sql);
		Integer maxID = Integer.valueOf(l.get(0).get("m").toString())+1;
		DecimalFormat df = new DecimalFormat("000");
		String kpiCode = "kpi"+df.format(maxID);
		return kpiCode;
	}
	@Override
	public List<Map<String, Object>> kpiNameAndKpiCodeMapRef() {
		String sql  = "select id,kpi_name,kpi_code,kpi_category,kpi_ne_type from kpi_config";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
		return resultList;
	}
	@Override
	public boolean addOrEditConfig(KpiConfig kpiConfig) {
		try {
			kpiConfigRepository.save(kpiConfig);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public Iterable<KpiConfig> getAllKpiConfigFromJpaRepo() {
		Iterable<KpiConfig> resultList = kpiConfigRepository.findAll();
		return resultList;
	}
	@Override
	public Map<String,List<String>> neTypeAndKpiCategory() {
		try {
			String sql = "SELECT kpi_ne_type,kpi_category FROM `kpi_config` GROUP BY kpi_ne_type,kpi_category";
			List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
			Map<String,List<String>> m = new HashMap<>();
			for (Map<String, Object> map : resultList) {
				String neType = map.get("kpi_ne_type").toString();
				String kpiCategory = map.get("kpi_category").toString();
				if(!m.containsKey(neType)){
					List<String> kpiCategoryList = new ArrayList<>();
					m.put(neType, kpiCategoryList);
				}else{
					List<String> kpiCategoryList = m.get(neType);
					kpiCategoryList.add(kpiCategory);
					m.put(neType, kpiCategoryList);
				}
			}
			return m;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<>();
		}
	}
	@Override
	public Map<String,Map<String,String>> getKpiCodeAndNameMapsInstance() {
		if(kpiCodeAndNameMaps!=null){
			return kpiCodeAndNameMaps;
		}else{
			String sql = "select kpi_name,kpi_code from kpi_config";
			List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql);
			Map<String,String> codeToNameMap = new HashMap<>();
			Map<String,String> nameToCodeMap = new HashMap<>();
			for (Map<String, Object> map : resultList) {
				String kpiName = map.get("kpi_name").toString();
				String kpiCode = map.get("kpi_code").toString();
				codeToNameMap.put(kpiCode, kpiName);
				nameToCodeMap.put(kpiName, kpiCode);
			}
			Map<String,Map<String,String>> resultMap = new HashMap<>();
			resultMap.put("codeToName", codeToNameMap);
			resultMap.put("nameToCode", nameToCodeMap);
			kpiCodeAndNameMaps = resultMap;
			return kpiCodeAndNameMaps;
		}
	}
	

}
