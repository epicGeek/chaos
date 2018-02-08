package com.nokia.ices.app.dhss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;
import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveRecord;
import com.nokia.ices.app.dhss.domain.alarm.UserAlarmMonitor;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.repository.alarm.AlarmReceiveRecordRepository;
import com.nokia.ices.app.dhss.service.AlarmMonitorService;
import com.nokia.ices.app.dhss.service.SecurityService;

@RestController
public class AlarmMonitorController {
	
    private static final Logger logger = LoggerFactory.getLogger(AlarmMonitorController.class);
    
    @Autowired
    AlarmMonitorService alarmMonitorService;
    
    @Autowired
	AlarmReceiveRecordRepository alarmReceiveRecordRepository;
    
    @Autowired
    SecurityService securityService;
    
    @RequestMapping(method=RequestMethod.POST,value="api/v1/cancelAlarm")
    public AlarmReceiveHistory cancelAlarm( @RequestBody AlarmReceiveRecord record){
    	return alarmMonitorService.cancelAlarm(record);
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping("api/v1/dhss-list/{type}/{flag}")
	public Map<String, Object> getSystemManageNeList(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type,@PathVariable String flag){
		Map<String, Object> paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", flag);
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List<Map<String, String>> data = securityService.getResource(paramsMap,true);
		
		List<Map<String, String>> dhssList = new ArrayList<>();
		Set<String> tempSet = new HashSet<>();
		for (Map<String, String> map : data) {
			if(tempSet.add(map.get("dhss_name"))){
				Map<String, String> tMap = new HashMap<>();
				tMap.put("label", map.get("dhss_name"));
				tMap.put("value", map.get("dhss_name"));
				dhssList.add(tMap);
			}
		}
		
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("dhss", dhssList);
		return resultMap;
	}
    
//    @RequestMapping("api/v1/dhss-list")
//    public List<Map<String, String>> findDhssList(){
//    	List<EquipmentNe> neList = this.alarmMonitorService.findEquipmentNe(new HashMap<>());
//    	Set<String> tempSet = new HashSet<String>();
//    	for (EquipmentNe equipmentNe : neList) {
//    		tempSet.add(equipmentNe.getDhssName());
//		}
//    	tempSet = AlarmMonitorServiceImpl.sortByValue(tempSet);
//    	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
//    	for (String string : tempSet) {
//			Map<String, String> map = new HashMap<String,String>();
//			map.put("label", string);
//			map.put("value", string);
//			list.add(map);
//		}
//    	return list;
//    }
    
    
    /**
     * 获取treenode
     * @param token
     * @param dhssName
     * @param neName
     * @return
     */
	@RequestMapping("api/v1/alarm-dhss")
	public List<Map<String, Object>> findDhssList(@RequestHeader("Ices-Access-Token") String token,
												  @RequestParam(value="dhssName",required=false)String dhssName,
												  @RequestParam(value="neName",required=false)String neName){
		List<Map<String, Object>> rootList = new ArrayList<Map<String, Object>>();
		if(StringUtils.isNotBlank(dhssName)){
			alarmMonitorService.returnNeList( rootList,dhssName,token);
		}else{
			if(StringUtils.isNotBlank(neName)){
				alarmMonitorService.returnUnitList( rootList,neName,token);
			}else{
				alarmMonitorService.returnDhssList( rootList,token);
			}
		}
		return rootList;
	}
	/**
	 * 加入或者收藏
	 * @param userAlarm
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "api/v1/collection",method=RequestMethod.POST)
	public boolean joinOrCancelCollection(@RequestBody UserAlarmMonitor userAlarm,@RequestHeader("Ices-Access-Token") String token){
		logger.info("{}",userAlarm);
		if(userAlarm.getIsCollection()){
			return this.alarmMonitorService.joinCollection(userAlarm, token) == null ? false : true;
		}else{
			return this.alarmMonitorService.cancelCollection(userAlarm, token);
		}
	}
	
	

	/**
	 * 获取用户收藏的列表
	 * @param token
	 * @return
	 */
	@RequestMapping("api/v1/user-alarm-param")
	public List<UserAlarmMonitor> getUserAlarmMonitorList(@RequestHeader("Ices-Access-Token") String token){
		String userName = securityService.getSystemUser(token);
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("userName_EQ",userName);
		
		return this.alarmMonitorService.findUserAlarmMonitor(paramMap);
	}
	
	
	
	@RequestMapping(value = "api/v1/alarm-record",method=RequestMethod.POST)
	public List<AlarmReceiveRecord> findAlarmReceiveRecord(@RequestBody UserAlarmMonitor userAlarm,
															@RequestHeader("Ices-Access-Token") String token){
		
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		
		if(StringUtils.isNotEmpty(userAlarm.getCnum()) ){
			if("unknown".equals(userAlarm.getCnum())){
				searchFilterAND.add(new SearchFilter("dhssName", Operator.EQ,""));
			}else if(userAlarm.getCnum().endsWith("_ne")){
				Map<String,Object> mapParams = new HashMap<String,Object>();
				mapParams.put("neName_EQ", userAlarm.getCnum().replace("_ne", ""));
				List<EquipmentUnit> unitList = alarmMonitorService.findEquipmentUnit(mapParams);
				for (EquipmentUnit equipmentUnit : unitList) {
					searchFilterOR.add(new SearchFilter("neName", Operator.LIKE,equipmentUnit.getUnitName()));
				}
			}else{
				searchFilterOR.add(new SearchFilter("alarmCell", Operator.LIKE,userAlarm.getCnum()));
				searchFilterOR.add(new SearchFilter("neName", Operator.LIKE,userAlarm.getCnum()));
				searchFilterOR.add(new SearchFilter("dhssName", Operator.LIKE,userAlarm.getCnum()));
			}
		}
		if(null != userAlarm.getStartTime()){
			searchFilterAND.add(new SearchFilter("receiveStartTime", Operator.GE, userAlarm.getStartTime() ));
		}
		
		if(null != userAlarm.getEndTime()){
			searchFilterAND.add(new SearchFilter("receiveStartTime", Operator.LT, userAlarm.getEndTime() ));
		}
		
		if(StringUtils.isNotEmpty(userAlarm.getAlarmNum()) ){
			searchFilterAND.add(new SearchFilter("alarmNo", Operator.LIKE, userAlarm.getAlarmNum() ));
		}
		
		if(StringUtils.isNotEmpty(userAlarm.getKeyword()) ){
			searchFilterAND.add(new SearchFilter("alarmCell", Operator.LIKE, userAlarm.getKeyword() ));
		}
		
		if(StringUtils.isNotEmpty(userAlarm.getNotAlarmNo())){
			String [] alarmNum = userAlarm.getNotAlarmNo().split("_");
			for (String string : alarmNum) {
				searchFilterAND.add(new SearchFilter("alarmNo", Operator.NOTEQ, string));
			}
		}
		
		Specification<AlarmReceiveRecord> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,AlarmReceiveRecord.class);
		Specification<AlarmReceiveRecord> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,AlarmReceiveRecord.class);
		
		List<AlarmReceiveRecord> list = alarmReceiveRecordRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsOR),new Sort(Direction.DESC,"receiveStartTime"));
		
		return list;
	}
	
	
	
	
	@SuppressWarnings("unused")
	private Sort Sort(String param){
		List<Order> orders = new ArrayList<>();
		String [] orderArray = param.split(";");
		for (String string : orderArray) {
			String [] o = string.split(",");
			Direction d = o[1].equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC;
			Order order = new Order(d, o[0]);
			orders.add(order);
		}
		return new Sort(orders);
	}
	
	
	
	
	
	
	
	
	
}




























