package com.nokia.ices.app.dhss.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.PropertiesConfig;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.kpi.KpiMonitor;
import com.nokia.ices.app.dhss.domain.topology.AhubConnInfo;
import com.nokia.ices.app.dhss.service.TopologyService;

@RestController
public class TopologyController {
	
	@Autowired
	private TopologyService topologyService;
	
	@RequestMapping("connection/test/{id}")
	public String pingUnit(@PathVariable Long id){
		EquipmentUnit unit = topologyService.findUnitById(id);
		
		String result = "";
		try {
			String command =  PropertiesConfig.getPingCmd() + " " +  unit.getServerIp() ;
			String commands[] = {"/bin/sh","-c", command};
			for (String string : commands) {
				System.out.println(string);
			}
			Process process = Runtime.getRuntime().exec(commands);
			process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while(null!=(line=br.readLine())){
				if(line.indexOf("%") != -1){
					result = line;
				}
			}
			
			unit.setLastPingResult(result);
			unit.setLastPingTime(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
			topologyService.saveEquipmentUnit(unit);
			return result;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping("resource/{type}/{token}")
	public List findGroupAll(@PathVariable String token,@PathVariable String type){
		return topologyService.findResource(token,"1",type,false);
	}
	
	@RequestMapping("ahub-result/{ahub}/{dhss}")
	public Map<String, Object> findAuhbResult(@PathVariable String ahub,@PathVariable String dhss){
		Map<String, Object> resultMap = new HashMap<>();
		if(StringUtils.isNotBlank(ahub)){
			resultMap.put("ahubName_EQ", ahub.split("_")[1]);
			List<AhubConnInfo> list = topologyService.getfindAhubResultList(resultMap);
			resultMap.put("result", list);
		}
		return resultMap;
	}
	
	@RequestMapping("kpi-result/{params}/{dhss}")
	public Map<String, Object> findKpiResult(@PathVariable String params,@PathVariable String dhss){
		Map<String, Object> resultMap = new HashMap<>();
		if(!params.startsWith("unit_")){
			resultMap.put("result", new ArrayList<>());
			return resultMap;
		}
		if(StringUtils.isNotBlank(params)){
			String [] param = params.split("_");
			Map<String,Object> paramMap = new HashMap<>();
			paramMap.put("dhssName_EQ", dhss);
			if(params.startsWith("location_")){
				paramMap.put("neSite_EQ", param[1]);
			}
			if(params.startsWith("ne_")){
				paramMap.put("neName_EQ", param[1]);			
			}
			if(params.startsWith("unitType_")){
				paramMap.put("unitType_EQ", param[1]);
				paramMap.put("neName_EQ", param[2]);
			}
			if(params.startsWith("unit_")){
				paramMap.put("unitName_EQ", param[1]);
			}
			if(params.startsWith("dhss_")){
				paramMap.put("dhssName_EQ", param[1]);
			}
			List<KpiMonitor> resultList = topologyService.getfindKpiResultList(paramMap);
			resultMap.put("result", resultList);
			
		}
		return resultMap;
	}
	
	@RequestMapping("alarm-result/{params}/{token}/{dhss}")
	public Map<String, Object> findAlarmResult(@PathVariable String params,@PathVariable String token,@PathVariable String dhss){
		return topologyService.findAlarmResult(params, token, dhss);
	}
	
	
	@RequestMapping("dhss-result/{token}/{dhss}")
	public Map<String, Object> findDhssList(@PathVariable String token,@PathVariable String dhss){
		return topologyService.findDhssList(token, dhss);
	}
	
	

}
