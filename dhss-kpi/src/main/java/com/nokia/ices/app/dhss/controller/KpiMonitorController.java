package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.KpiCustomSetting;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitorHistory;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.service.KpiItemService;
import com.nokia.ices.app.dhss.service.KpiMonitorHistoryService;

@RestController
public class KpiMonitorController {
	private String lastToken = "";

	private static final Logger logger = LoggerFactory.getLogger(KpiMonitorController.class);
	@Autowired
	private KpiItemService kpiItemService;
	@Autowired
	private KpiMonitorHistoryService kpiMonitorHistoryService;
	@Autowired
	private KpiCustomSetting kpiCustomSetting;

	@RequestMapping(value="/api/v1/kpi-overview/grain",method=RequestMethod.GET)
	public Boolean showGrain(){
		return kpiCustomSetting.getShowMoreGrains();
	}
	
	@RequestMapping(value="/api/v1/kpi-item/char-data",method=RequestMethod.POST)
	public Map<String,Object> getKpiItemCharDataByGrain(@RequestBody KpiParams kpiParams)throws ParseException{
		String grain = kpiParams.getGrain();
		Map<String,Object> resultDataMap = new HashMap<>();
		if(grain == null || (!grain.equals("day"))&&(!grain.equals("week"))&&(!grain.equals("month"))){
			resultDataMap = getKpiItemCharData(kpiParams);
		}else{
			resultDataMap = kpiItemService.kpiStatisticsByMoreGrains(kpiParams);
		}
		String kpiCode = resultDataMap.get("kpiCode_EQ")==null?"":resultDataMap.get("kpiCode_EQ").toString();
		List<String> noCountDataKpiCodeSet = kpiCustomSetting.getNoCountDataKpiCodeSet();
		if(noCountDataKpiCodeSet.contains(kpiCode)){
			resultDataMap.remove("successData");
		}
		return resultDataMap;
	}
	
	public Map<String,Object> getKpiItemCharData(@RequestBody KpiParams kpiParams) throws ParseException{
		SimpleDateFormat Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String,Object> paramMap = new HashMap<String,Object>();
		if(StringUtils.isNotBlank(kpiParams.getNeType())){
			paramMap.put("neType_EQ", kpiParams.getNeType());
		}
		if(StringUtils.isNotBlank(kpiParams.getLocation())){
			paramMap.put("neSite_EQ", kpiParams.getLocation());
		}
		if(StringUtils.isNotBlank(kpiParams.getNeName())){
			paramMap.put("neName_EQ", kpiParams.getNeName()); 
		}
		if(StringUtils.isNotBlank(kpiParams.getKpiCode())){
			paramMap.put("kpiCode_EQ", kpiParams.getKpiCode());
		}
		if(StringUtils.isNotBlank(kpiParams.getStartDate())){
			paramMap.put("periodStartTime_GE", Format.parse(kpiParams.getStartDate()+":00"));
		}
		if(StringUtils.isNotBlank(kpiParams.getEndDate())){
			paramMap.put("periodStartTime_LT", Format.parse(kpiParams.getEndDate()+":00"));
		}
		List<KpiMonitorHistory> history = kpiItemService.findKpiMonitorHistory(paramMap);
		
		paramMap.clear();
		paramMap.put("kpiCode_EQ", kpiParams.getKpiCode());
		List<KpiConfig> kpi = kpiItemService.findKpiItem(paramMap);
		
		kpiItemService.kpiStatistics(paramMap,history,kpi.size() == 0 ? null : kpi.get(0));
		
		return paramMap;
		
	}
	@RequestMapping(value="/api/v1/kpi/history-data-list")
	public Page<KpiMonitorHistory> getKpiItemCharData(
			@RequestParam(value = "dhssName", required = false) String dhssName, 
			@RequestParam(value = "physicalLocation", required = false) String physicalLocation, 
			@RequestParam(value = "neType", required = false) String neType,
			@RequestParam(value = "neName", required = false) String neName, 
			@RequestParam(value = "unitName", required = false) String unitName, 
			@RequestParam(value = "kpiType", required = false) String kpiType,
			@RequestParam(value = "kpiName", required = false) String kpiName, 
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "unitType", required = false) String unitType,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestHeader("Ices-Access-Token")String token,
			Pageable pageable){
		lastToken = token;
		Map<String, Specification<KpiMonitorHistory>> m = getKpiQueryCondition(dhssName, physicalLocation, neType,
				neName, unitName, kpiType, kpiName, startTime, unitType, endTime, token);
		return kpiMonitorHistoryService.getKpiDataByCondition(m, pageable);
	}
	@RequestMapping(value="/api/v1/kpi/kpi-export-report-download")
	public void exportKpiReport(
			@RequestParam(value = "dhssName", required = false) String dhssName, 
			@RequestParam(value = "physicalLocation", required = false) String physicalLocation, 
			@RequestParam(value = "neType", required = false) String neType,
			@RequestParam(value = "neName", required = false) String neName, 
			@RequestParam(value = "unitName", required = false) String unitName, 
			@RequestParam(value = "kpiType", required = false) String kpiType,
			@RequestParam(value = "kpiName", required = false) String kpiName, 
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "unitType", required = false) String unitType,
			@RequestParam(value = "endTime", required = false) String endTime,
			HttpServletRequest request,HttpServletResponse response
			) throws Exception{
		Map<String, Specification<KpiMonitorHistory>> m = getKpiQueryCondition(dhssName, physicalLocation, neType,
				neName, unitName, kpiType, kpiName, startTime, unitType, endTime, lastToken);
		Page<KpiMonitorHistory> exportDataList = kpiMonitorHistoryService.getExportData(m);
		kpiMonitorHistoryService.exportData(exportDataList, request, response);
	}
	private Map<String, Specification<KpiMonitorHistory>> getKpiQueryCondition(String dhssName, String physicalLocation,
			String neType, String neName, String unitName, String kpiType, String kpiName, String startTime,
			String unitType, String endTime, String token) {
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		
		if(StringUtils.isNotBlank(dhssName)&&!dhssName.equals("null")){
			searchFilterAND.add(new SearchFilter("dhssName", Operator.EQ,dhssName));
		}
		if(StringUtils.isNotBlank(unitType)&&!unitType.equals("null")){
			searchFilterAND.add(new SearchFilter("unitType", Operator.EQ,unitType));
		}
		if(StringUtils.isNotBlank(physicalLocation)&&!physicalLocation.equals("null")){
			searchFilterAND.add(new SearchFilter("neSite", Operator.EQ,physicalLocation));
		}
		if(StringUtils.isNotBlank(neType)&&!neType.equals("null")){
			searchFilterAND.add(new SearchFilter("neType", Operator.EQ,neType));
		}
		if(neName!=null && StringUtils.isNotBlank(neName) && !neName.equals("null") ){
			searchFilterAND.add(new SearchFilter("neName", Operator.EQ,neName));
		}else{
			List<String> neList = kpiMonitorHistoryService.getNeList(token);
			searchFilterOR.add(new SearchFilter("neName", Operator.ISNULL,null));
			for (String ne : neList) {
				searchFilterOR.add(new SearchFilter("neName", Operator.EQ,ne));
			}
		}
		if(StringUtils.isNotBlank(unitName)&&!unitName.equals("null")){
			searchFilterAND.add(new SearchFilter("unitName", Operator.EQ,unitName));
		}
		if(StringUtils.isNotBlank(kpiType)&&!kpiType.equals("null")){
			searchFilterAND.add(new SearchFilter("kpiCategory", Operator.EQ,kpiType));
		}
		if(StringUtils.isNotBlank(kpiName)&&!kpiName.equals("null")){
			searchFilterAND.add(new SearchFilter("kpiCode", Operator.EQ,kpiName));
		}
		if (startTime != null && startTime != "" && endTime != null && endTime != "") {
			try {
				SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				searchFilterAND.add(new SearchFilter("periodStartTime", Operator.GE,SDF.parse(startTime)));
				searchFilterAND.add(new SearchFilter("periodStartTime", Operator.LE,SDF.parse(endTime)));
			} catch (ParseException e) {
				e.getMessage();
				logger.info("Time parse error:");
				logger.info("start time:"+startTime);
				logger.info("end time:"+endTime);
			}
		}
		Specification<KpiMonitorHistory> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,KpiMonitorHistory.class);
		Specification<KpiMonitorHistory> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,KpiMonitorHistory.class);
		Map<String,Specification<KpiMonitorHistory>> m = new HashMap<>();
		m.put("AND", speciFicationsAND);
		m.put("OR", speciFicationsOR);
		return m;
	}
	


}

