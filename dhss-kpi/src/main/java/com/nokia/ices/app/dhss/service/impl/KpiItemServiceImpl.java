package com.nokia.ices.app.dhss.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.controller.KpiParams;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.repository.kpi.KpiConfigRepository;
import com.nokia.ices.app.dhss.repository.kpi.KpiMonitorHistoryRepository;
import com.nokia.ices.app.dhss.service.KpiConfigService;
import com.nokia.ices.app.dhss.service.KpiItemService;

@Service
public class KpiItemServiceImpl implements KpiItemService{
	private static final Logger LOGGER = LogManager.getLogger(KpiItemServiceImpl.class);

	private static String GRAIN_SQL = 
			"SELECT\n" +
			"	kpi_name,\n" +
			"	kpi_unit,\n" +
			"	CONCAT(\n" +
			"		min(period_start_time),\n" +
			"		\"~\",\n" +
			"		max(period_start_time)\n" +
			"	) as time,\n" +
			"	FORMAT(if( avg(kpi_total) = 0 ,0 ,avg(kpi_value) / avg(kpi_total) * 100),2) as kpi_ratio,\n" +
			"	ROUND(avg(kpi_value),2) as kpi_value,\n" +
			"	#GRAIN#\n" +
			"FROM\n" +
			"	kpi_monitor_history\n" +
			"WHERE\n" +
			"	period_start_time > ?\n" +
			"and period_start_time < ?\n" +
			"#QUERY_CONDITION# \n"+
			"GROUP BY\n" +
			"	year(period_start_time),#GRAIN#\n" +
			"ORDER BY\n" +
			"  min(period_start_time)";
	@Autowired
	private KpiConfigRepository kpiConfigRepository;
	@Autowired
	private KpiConfigService kpiConfigService;
	@Autowired
	private KpiMonitorHistoryRepository kpiMonitorHistoryRepository;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Override
	public List<KpiConfig> findKpiItem(Map<String,Object> paramMap) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<KpiConfig> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, KpiConfig.class);
		return kpiConfigRepository.findAll(spec);
	}

	@Override
	public List<KpiMonitorHistory> findKpiMonitorHistory(Map<String, Object> paramMap) {
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<KpiMonitorHistory> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, KpiMonitorHistory.class);
		Sort sort = new Sort(Direction.DESC, "periodStartTime");
		return kpiMonitorHistoryRepository.findAll(spec,sort);
	}

	@Override
	public void kpiStatistics(Map<String, Object> paramMap, List<KpiMonitorHistory> history,KpiConfig kpi) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		Map<String, List<KpiMonitorHistory>> map = new HashMap<String, List<KpiMonitorHistory>>();
		
		TreeSet<String> dateSet = new TreeSet<String>();
		
		String unit = "";
		
		for (KpiMonitorHistory kpiResult : history) {
			String key = format.format(kpiResult.getPeriodStartTime());
			dateSet.add(key);
			if(map.get(key) == null){
				map.put(key, new ArrayList<KpiMonitorHistory>());
			}
			map.get(key).add(kpiResult);
//			unit = kpiResult.getKpiUnit();
		}
		
		
		DecimalFormat df = new DecimalFormat("######0.00"); // 保留两位小数
		List<Object> dataList = new ArrayList<Object>();
		List<Object> countList = new ArrayList<Object>();
		for (String string : dateSet) {
			Integer requestCount = 0;
			Integer successCount = 0;
			List<KpiMonitorHistory> dateList = map.get(string);
			for (KpiMonitorHistory kpiMonitorHistory : dateList) {
				requestCount += kpiMonitorHistory.getKpiTotal() == null ? 0 : kpiMonitorHistory.getKpiTotal().intValue();
				successCount += (kpiMonitorHistory.getKpiValue() == null ? 0 : kpiMonitorHistory.getKpiValue().intValue());
			}
			Object value = 0D;
			switch (kpi.getKpiUnit()) {
				case "ratio":
					if (requestCount == 0) {
						value = 100D;
					} else {
						value = Double.parseDouble(df.format(
								Double.parseDouble(successCount.toString()) / Double.parseDouble(requestCount.toString()) * 100));
					}
					unit = "%";
					break;
//				case "fail_count":
//					value = requestCount - successCount;
//					break;
				case "value":
					value = successCount;
					break;
			}
			dataList.add(value);
			countList.add(successCount);
		}
		paramMap.put("unit", unit);
		paramMap.put("data", dataList);
		paramMap.put("successData", countList);
		paramMap.put("header", kpi.getKpiName());
		paramMap.put("labels", dateSet);
	}
	
	@Override
	public Map<String, Object> kpiStatisticsByMoreGrains(KpiParams kpiParams) {
		String grain = "";
		if(kpiParams.getGrain().equals("day")){
			grain = "date(period_start_time)";
		}else if(kpiParams.getGrain().equals("week")){
			grain = "week(period_start_time,1)";
		}else if(kpiParams.getGrain().equals("month")){
			grain = "month(period_start_time)";
		}
		String sql = GRAIN_SQL.replaceAll("#GRAIN#", grain);
		List<Object> queryParams = new ArrayList<>();
		queryParams.add(kpiParams.getStartDate());
		queryParams.add(kpiParams.getEndDate());
		String condition = "";
		if(kpiParams.getLocation() != null){
			condition += "	and \n" +
					"	ne_site = ?\n" ;
			queryParams.add(kpiParams.getLocation());
			
		}
		if(kpiParams.getNeType() != null){
			condition += "	and \n" +
					"	ne_type = ?\n" ;
			queryParams.add(kpiParams.getNeType());
			
		}
		if(kpiParams.getNeName() != null){
			condition += "	and \n" +
					"	ne_name = ?\n" ;
			queryParams.add(kpiParams.getNeName());
			
		}
		if(kpiParams.getKpiCode() != null){
			condition += "	and \n" +
					"	kpi_code = ?\n" ;
			queryParams.add(kpiParams.getKpiCode());
			
		}
		sql = sql.replace("#QUERY_CONDITION#", condition);
		LOGGER.info(sql);
		List<Map<String,Object>> resultList = jdbcTemplate.queryForList(sql,queryParams.toArray());
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String unit = "";
		List<Object> dataList = new ArrayList<Object>();
		List<Object> countList = new ArrayList<Object>();
		TreeSet<String> dateSet = new TreeSet<String>();
		for (Map<String,Object> dataMap : resultList) {
			if(dataMap.get("kpi_unit")!=null){
				unit = dataMap.get("kpi_unit").toString().equals("ratio")?"%":"";
			}else{
				unit = "";
			}
			dateSet.add(dataMap.get("time").toString());
			if(unit.equals("%")){
				dataList.add(Double.valueOf(dataMap.get("kpi_ratio")!=null?dataMap.get("kpi_ratio").toString():"0.0"));
			}else if(unit.equals("")){
				dataList.add(dataMap.get("kpi_value"));
			}
			countList.add(dataMap.get("kpi_value"));
		}
		resultMap.put("unit", unit);
		resultMap.put("data", dataList);
		resultMap.put("successData", countList);
		Map<String,String> codeToNameMap = kpiConfigService.getKpiCodeAndNameMapsInstance().get("codeToName");
		resultMap.put("header", codeToNameMap.get(kpiParams.getKpiCode()));
		resultMap.put("labels", dateSet);
		return resultMap;
	}

	
	

}
