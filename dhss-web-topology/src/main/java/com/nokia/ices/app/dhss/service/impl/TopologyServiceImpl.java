package com.nokia.ices.app.dhss.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.nokia.ices.app.dhss.SecurityGlobalSetting;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.domain.topology.AhubConnInfo;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.repository.alarm.AlarmReceiveRecordRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.kpi.KpiMonitorRepository;
import com.nokia.ices.app.dhss.repository.topology.AhubConnInfoRepository;
import com.nokia.ices.app.dhss.service.TopologyService;

@Service
public class TopologyServiceImpl implements TopologyService{
	
	@Autowired
	private SecurityGlobalSetting securityGlobalSetting;
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private KpiMonitorRepository kpiMonitorRepository;
	
	@Autowired
	private AlarmReceiveRecordRepository alarmReceiveRecordRepository; 
	
	@Autowired
	private AhubConnInfoRepository ahubConnInfoRepository;
	
	private final RestTemplate restTemplate;

	public TopologyServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	@Override
	public List<AhubConnInfo> getfindAhubResultList(Map<String,Object> paramMap){
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AhubConnInfo> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.OR, AhubConnInfo.class);
		return ahubConnInfoRepository.findAll(spec);
	}
	
	@Override
	public List<AlarmReceiveRecord> getfindAlarmResultList(Map<String,Object> paramMap){
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<AlarmReceiveRecord> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.OR, AlarmReceiveRecord.class);
		return alarmReceiveRecordRepository.findAll(spec);
	}
	
	@Override
	public List<KpiMonitor> getfindKpiResultList(Map<String,Object> paramMap){
		Map<String,SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<KpiMonitor> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, KpiMonitor.class);
		return kpiMonitorRepository.findAll(spec);
	}

	@Override
	public Set<String> findDhssList(String token) {
		Set<String> result = new HashSet<String>();
		List<Map<String,String>> allNeList = findResource(token,"1","net",true);
		for (Map<String,String> map : allNeList) {
			result.add(map.get("dhss_name"));
		}
		return result;
	}
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  List<Map<String,String>> findResource(String token,String flag,String resourceFlag,boolean isPerssion){
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", resourceFlag);
		paramsMap.put("contentFlag", flag);
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List<Map<String,String>> data = getResource(paramsMap,isPerssion);
		return data;
	}
	
 
	@SuppressWarnings({  "rawtypes", "unchecked" })
	private List<Map<String, String>> getResource(Map paramsMap, boolean isPermission) {
		try {
			String baseURL = securityGlobalSetting.getBaseUrl();
			String url = isPermission ? (baseURL + securityGlobalSetting.getResourceUrl()) :( baseURL + securityGlobalSetting.getNoPerssionResourceUrl());
			ResponseEntity<Map> responseMap = restTemplate.getForEntity(url, Map.class, paramsMap);
			Map resultMap = responseMap.getBody();
			List data = (List) resultMap.get("sourceData");
			if (data.size() == 0) {
				return data;
			}
			Map sourceData = (Map) data.get(0);
			List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
			List resultData = (List) sourceData.get("sourceData");
			for (Object object : resultData) {
				mapList.add((Map) object);
			}
			return mapList;
		} catch (RestClientException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<Map<String, String>> findallNe(String token,String flag) {
		return findResource(token,flag,"net",true);
	}

	@Override
	public  Iterable<EquipmentUnit> findEquipmentUnitList(String token) {
		List<Map<String, String>> typeList = findResource(token,"1","neUnitType",true);
		List<SearchFilter> searchFilterNeTypeOr = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterUnitTypeOr = new ArrayList<SearchFilter>();
		Set<String> neTypeSet = new HashSet<>();
		Set<String> unitTypeSet = new HashSet<>();
		for (Map<String, String> map : typeList) {
			if(neTypeSet.add(map.get("neType"))) {
				searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,map.get("neType")));
			}
			if(unitTypeSet.add(map.get("unitType"))) {
				searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,map.get("unitType")));
			}
		}
		
		if(null == typeList || typeList.size() == 0) {
			searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,"none"));
			searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,"none"));
		}
		
		Specification<EquipmentUnit> speciFicationsNeTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterNeTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsUnitTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterUnitTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		return equipmentUnitRepository.findAll(Specifications.where(speciFicationsNeTypeAND).and(speciFicationsUnitTypeAND),new Sort(Direction.ASC, "unitName"));
	}

	@Override
	public Map<String, Object> findDhssList(String token, String dhss) {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		List<Object> rootList = new ArrayList<Object>();
		
		Map<String,Object> rootMap = new HashMap<String,Object>();
		
		List<Map<String, String>> allNeList = findallNe(token,"1");
		
		Iterable<EquipmentUnit> unitAllList = findEquipmentUnitList(token);
//		Set<String> alarmUnitName = new HashSet<>();
		Map<String, Integer> neAlarmLevelMap = new HashMap<>();
		List<AlarmReceiveRecord> alarmList = getfindAlarmResultList(new HashMap<>());
		for (AlarmReceiveRecord alarmReceiveRecord : alarmList) {
//			alarmUnitName.add(alarmReceiveRecord.getNeName());
			Integer neLevel = neAlarmLevelMap.get(alarmReceiveRecord.getNeName())== null ?  0 : neAlarmLevelMap.get(alarmReceiveRecord.getNeName());
			Integer level = StringUtils.isNotBlank(alarmReceiveRecord.getAlarmLevel()) ? alarmReceiveRecord.getAlarmLevel().length() : 0;
			neAlarmLevelMap.put(alarmReceiveRecord.getNeName(), (level > neLevel) ? level : neLevel);
		}
		
		
		Map<String, List<Map<String, Object>>> unitTempMap = new HashMap<>();
		Set<String> tempSet = new HashSet<>();
		for (EquipmentUnit equipmentUnit : unitAllList) {
			//单元节点
			if(StringUtils.isNotBlank(dhss) && !dhss.equals(equipmentUnit.getDhssName())){
				continue;
			}
			String key = equipmentUnit.getUnitType() + equipmentUnit.getNeName();
			List<Map<String, Object>> unitList = unitTempMap.get(key);
			unitList = unitList == null ? new ArrayList<>() : unitList;
			Map<String,Object> unitMap = new HashMap<String,Object>();
//			boolean isAlarm = alarmUnitName.contains(equipmentUnit.getUnitName()) ? true : false; 
			Integer alarmLevel = neAlarmLevelMap.get(equipmentUnit.getUnitName());
			alarmLevel = alarmLevel == null ? 0 : alarmLevel;
			fullMap(unitMap, "unit_"+equipmentUnit.getUnitName(), equipmentUnit.getUnitName(), equipmentUnit.getUnitType(), equipmentUnit.getPhysicalLocation(), 
					equipmentUnit.getUnitIdsVersion(), equipmentUnit.getUnitSwVersion(), equipmentUnit.getUnitRemark(), null,equipmentUnit.getIsForbidden()?"1" :"0","4",equipmentUnit.getId(),alarmLevel,equipmentUnit.getLastPingResult());
			unitList.add(unitMap);
			unitTempMap.put(key, unitList);
			
		}
		
		
		Map<String, List<Map<String, Object>>> unitTypeTempMap = new HashMap<>();
		tempSet = new HashSet<>();
		for (EquipmentUnit equipmentUnit : unitAllList) {
			if(StringUtils.isNotBlank(dhss) && !dhss.equals(equipmentUnit.getDhssName())){
				continue;
			}
			//单元类型节点
			String key = equipmentUnit.getUnitType() + equipmentUnit.getNeName();
			if(tempSet.add(key)){
				List<Map<String, Object>> unitTypeList = unitTypeTempMap.get(equipmentUnit.getNeName());
				unitTypeList = unitTypeList == null ? new ArrayList<>() : unitTypeList;
				Map<String,Object> unitTypeMap = new HashMap<String,Object>();
				fullMap(unitTypeMap, "unitType_"+equipmentUnit.getUnitType()+"_"+equipmentUnit.getNeName(), equipmentUnit.getUnitType(), "HSSTYPE", "", "", "", "",unitTempMap.get(key),"0","3",null,0,null);
				unitTypeList.add(unitTypeMap);
				unitTypeTempMap.put(equipmentUnit.getNeName(), unitTypeList);
			}
			
		}
		
//		Map<String,Object> tempMap = new HashMap<String,Object>();
		Set<String> locationSet = new TreeSet<String>();
		
		//将网元按照局址分开
		Map<String, List<Map<String, Object>>> neTempMap = new HashMap<>();
		
		for (Map<String, String> map : allNeList) {
			if(StringUtils.isNotBlank(dhss) && !dhss.equals(map.get("dhss_name"))){
				continue;
			}
			//局址节点
			locationSet.add(map.get("physical_location"));
			
			//网元节点
			List<Map<String, Object>> neListTemp = neTempMap.get(map.get("physical_location"));
			neListTemp = neListTemp == null ? new ArrayList<>() : neListTemp;
			Map<String,Object> neMap = new HashMap<String,Object>();
			fullMap(neMap, "ne_"+map.get("ne_name"), map.get("ne_name"), map.get("ne_type"), "", "", "", "", unitTypeTempMap.get(map.get("ne_name")),"0","2",null,0,null);
			neListTemp.add(neMap);
			neTempMap.put(map.get("physical_location"), neListTemp);
		}
		
		List<Map<String,Object>> locationList = new ArrayList<>();
		for (String string : locationSet) {
			Map<String,Object> locationMap = new HashMap<String,Object>();
			fullMap(locationMap, "location_"+string, string, "SITE", "", "", "", "",neTempMap.get(string),"0","1",null,0,null);
			locationList.add(locationMap);
		}
		
		fullMap(rootMap, "dhss_"+dhss, dhss, "ALLHSS", "", "", "", "",locationList,"0","0",null,0,null);
		
		rootList.add(rootMap);
		
		resultMap.put("result", rootList);
		return resultMap;
	}
	
	private void fullMap(Map<String,Object> rootMap,String neid,String nename,String netype,
			String location,String idsVersion,String swVersion,String remarks,Object children,String neState,String iconLevel,Long id,Integer alarmLevel,String pingText){
		rootMap.put("neid", neid);
		rootMap.put("nename", nename);
		rootMap.put("netype", netype);
		rootMap.put("neState", neState);
		rootMap.put("location", location);
		rootMap.put("idsVersion", idsVersion);
		rootMap.put("swVersion", swVersion);
		rootMap.put("remarks", remarks);
		rootMap.put("iconLevel", iconLevel);
		rootMap.put("unitId", id);
		rootMap.put("alarm", alarmLevel);
		rootMap.put("pingText", pingText);
		rootMap.put("children", children);
	}

	@Override
	public Map<String, Object> findAlarmResult(String params, String token, String dhss) {
		Map<String, Object> resultMap = new HashMap<>();
		if(!params.startsWith("unit_")){
			resultMap.put("result", new ArrayList<>());
			return resultMap;
		}
		if(StringUtils.isNotBlank(params)){
			String [] param = params.split("_");
			List<SearchFilter> alarmSearchFilterAND = new ArrayList<SearchFilter>();
			if(params.startsWith("dhss_")){
				alarmSearchFilterAND.add(new SearchFilter("dhssName", Operator.EQ,param[1]));
			}else{
				
				List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
				if(params.startsWith("unit_")){
					searchFilterAND.add(new SearchFilter("unitName", Operator.LIKE,param[1]));
				}else{
					List<Map<String, String>> allNeList = findallNe(token,"1");
					List<Long> list = new ArrayList<>();
					for(Map<String, String> action : allNeList){
						if(action.get("dhss_name").equals(dhss)){
							Object str = action.get("id");
							Long id = Long.parseLong(str.toString());
							if(params.startsWith("location_")){
								if(action.get("physical_location").equals(param[1])){
									list.add(id);
								}
							}
							if(params.startsWith("ne_")){
								if(action.get("ne_name").equals(param[1])){
									list.add(id);
								}
							}
							if(params.startsWith("unitType_")){
								if(action.get("ne_name").equals(param[2])){
									list.add(id);
								}
							}
						}
					};
					
					searchFilterAND.add(new SearchFilter("neId", Operator.IN,list));
				}
				
				
				Specification<EquipmentUnit> speciFicationsAND = DynamicSpecifications
						.bySearchFilter(searchFilterAND, BooleanOperator.AND,EquipmentUnit.class);
				List<EquipmentUnit> unitList = equipmentUnitRepository.findAll(Specifications.where(speciFicationsAND));
				for (EquipmentUnit equipmentUnit : unitList) {
					alarmSearchFilterAND.add(new SearchFilter("neName", Operator.LIKE,equipmentUnit.getUnitName()));
				}
			}
			Specification<AlarmReceiveRecord> alarmSpeciFicationsAND = DynamicSpecifications
					.bySearchFilter(alarmSearchFilterAND, BooleanOperator.OR,AlarmReceiveRecord.class);
			List<AlarmReceiveRecord> resultList = alarmReceiveRecordRepository.findAll(Specifications.where(alarmSpeciFicationsAND),new Sort(Direction.DESC, "alarmLevel"));
			resultMap.put("result", resultList);
			
		}
		return resultMap;
	}

	@Override
	public EquipmentUnit findUnitById(Long id) {
		return equipmentUnitRepository.findOne(id);
	}

	@Override
	public EquipmentUnit saveEquipmentUnit(EquipmentUnit unit) {
		return equipmentUnitRepository.save(unit);
	}
}
