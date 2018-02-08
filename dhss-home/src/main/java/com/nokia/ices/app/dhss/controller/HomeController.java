package com.nokia.ices.app.dhss.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.HomeAlarmItem;
import com.nokia.ices.app.dhss.domain.HomeNavItem;
import com.nokia.ices.app.dhss.service.HomeService;

@RestController
public class HomeController {
	
	
	@Autowired
	private HomeService homeService;

	@RequestMapping("api/v1/home-kpi-list-with-code")
	public List<Map<String, Object>> findHomeKpiList(@RequestParam(name="kpiCode") String kpiCode){
		List<Map<String, Object>> rootMap = homeService.findKpiCount(kpiCode);
		return rootMap;
	}
	
	@RequestMapping("api/v1/home-alarm")
	public List<String> findHomeAlarmCount(){
		List<String> map = homeService.findDayAlarmCount();
		return map;
	}
	 
	@RequestMapping(value = "/api/v1/home-nav-item",method=RequestMethod.GET,produces={"application/json;charset=UTF-8"})
	public List<HomeNavItem> findHomeNavItem(@RequestHeader("Ices-Access-Token")String token){
		List<HomeNavItem> homeNavItemList = homeService.findHomeNavItem(token);
		return homeNavItemList;
	}

	@RequestMapping(value = "/api/v1/home-alarm-item",method=RequestMethod.GET,produces={"application/json;charset=UTF-8"})
	public List<HomeAlarmItem> findHomeAlarmItem(){
		List<HomeAlarmItem> homeAlarmItemList = new ArrayList<HomeAlarmItem>();
		return homeAlarmItemList;
	}
}