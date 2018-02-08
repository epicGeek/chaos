package com.nokia.ices.app.dhss.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.service.KpiConfigService;

@RestController
public class KpiConfigController {
	@Autowired
	KpiConfigService kpiConfigService;
	@RequestMapping("/api/v1/kpi-config/get-all")
	public List<Map<String, Object>> getAllKpiConfig() {
		List<Map<String, Object>> resultData = new ArrayList<>();
		resultData =  kpiConfigService.getAllKpiConfig();
		return resultData;
	}
	@RequestMapping(value = "/api/v1/kpi-config/kpi-map")
	public List<Map<String,Object>> kpiNameAndKpiCodeMapRef(){
		List<Map<String,Object>> resultList = new ArrayList<>();
		resultList = kpiConfigService.kpiNameAndKpiCodeMapRef();
		return resultList;
	}
	@RequestMapping(value = "/api/v1/kpi-config/delete", method = RequestMethod.DELETE)
	public boolean deleteKpiConfig(@RequestParam(value = "kpiConfigId", required = false) Integer kpiConfigId) {
		return kpiConfigService.deleteKpiConfig(kpiConfigId);
	}
	@RequestMapping(value = "/api/v1/kpi-config/add-or-edit", method = RequestMethod.POST)
	public boolean addConfig(@RequestBody KpiConfig kpiConfig,
			@RequestHeader("Ices-Access-Token") String token) {
		return kpiConfigService.addOrEditConfig(kpiConfig);
	}
	
	@RequestMapping(value = "/api/v1/kpi-config/ne-type-and-kpi-category")
	public Map<String,List<String>> neTypeAndKpiCategory() {
		return kpiConfigService.neTypeAndKpiCategory();
	}
}
