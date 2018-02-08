package com.nokia.ices.app.dhss.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.service.AlarmMonitorService;


@RestController
public class AlarmMonitorCustomController {
	
	@Autowired
    private AlarmMonitorService alarmMonitorService;
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(AlarmMonitorCustomController.class);
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	
	@RequestMapping(value = "api/v1/alarm-monitor-custom",method=RequestMethod.POST)
	public Page<AlarmMonitor> findAlarmMonitorCustom(@RequestBody AlarmMonitorCustomQuery alarmMonitorCustomQuery,@RequestHeader("Ices-Access-Token")String token) throws ParseException{
		
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterOR = new ArrayList<SearchFilter>();
		if(StringUtils.isNotEmpty(alarmMonitorCustomQuery.getUnitName())){
			searchFilterAND.add(new SearchFilter("unitName", Operator.EQ,alarmMonitorCustomQuery.getUnitName()));
		}
		if(StringUtils.isNotEmpty(alarmMonitorCustomQuery.getAlarmType())){
			searchFilterAND.add(new SearchFilter("alarmType", Operator.EQ,alarmMonitorCustomQuery.getAlarmType()));
		}
		List<String> unitList = alarmMonitorService.getUnitList(token);
		searchFilterOR.add(new SearchFilter("unitName", Operator.ISNULL,null));
		for (String unit : unitList) {
			searchFilterOR.add(new SearchFilter("unitName", Operator.EQ,unit));
		}
		Date date = new Date();
		if(alarmMonitorCustomQuery.getStartTime()==null){
			alarmMonitorCustomQuery.setStartTime(SDF.format(new Date(date.getTime()-30*24*60*60*1000)));
		}
		if(alarmMonitorCustomQuery.getEndTime()==null){
			alarmMonitorCustomQuery.setEndTime(SDF.format(date));
		}
		searchFilterAND.add(new SearchFilter("startTime", Operator.GE,SDF.parse(alarmMonitorCustomQuery.getStartTime())));
		searchFilterAND.add(new SearchFilter("startTime", Operator.LE,SDF.parse(alarmMonitorCustomQuery.getEndTime())));
		Pageable pageable = new PageRequest(alarmMonitorCustomQuery.getPage(), alarmMonitorCustomQuery.getSize(),new Sort(Direction.DESC,"startTime"));
		Map<String,Specification<AlarmMonitor>> m = new HashMap<>();
		Specification<AlarmMonitor> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,AlarmMonitor.class);
		Specification<AlarmMonitor> speciFicationsOR = DynamicSpecifications
				.bySearchFilter(searchFilterOR, BooleanOperator.OR,AlarmMonitor.class);
		m.put("AND", speciFicationsAND);
		m.put("OR", speciFicationsOR);
		Page<AlarmMonitor> p = alarmMonitorService.findAlarmMonitorCustom(m, pageable);
		return p;
	}
	
	

	

}
