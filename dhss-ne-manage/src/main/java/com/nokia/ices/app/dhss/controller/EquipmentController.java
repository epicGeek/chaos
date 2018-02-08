package com.nokia.ices.app.dhss.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.service.EquipmentService;

@RestController
@CrossOrigin
public class EquipmentController {

	@Autowired
	private EquipmentService equipmentService;

	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("api/v1/system-ne-list/{type}/{flag}")
	public List getSystemManageNeList(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type,@PathVariable String flag){
		return equipmentService.getSystemManageNeList(token, type, flag);
	}

	@RequestMapping("api/v1/equipment-unit-list/{type}/{flag}/download")
	public void queryequipmentUnit(
			@RequestParam(value="queryNeType",required=false)String queryNeType,
			@RequestParam(value="queryNeName",required=false)String queryNeName,
			@RequestParam(value="queryUnitType",required=false)String queryUnitType,
			@RequestParam(value="paramStr",required=false)String paramStr,
			@RequestParam(value="token",required=false)String token,
			@PathVariable String type,@PathVariable String flag,HttpServletResponse response
			){

		equipmentService.exportUnitData(token, type, flag, queryNeType, queryNeName, queryUnitType, paramStr, response);

	}

	@RequestMapping("api/v1/query-equipment-unit-list/{type}/{flag}")
	public Page<EquipmentUnit> queryequipmentUnit(
			@RequestParam(value="queryNeType",required=false)String queryNeType,
			@RequestParam(value="queryNeName",required=false)String queryNeName,
			@RequestParam(value="queryUnitType",required=false)String queryUnitType,
			@RequestParam(value="paramStr",required=false)String paramStr,@RequestHeader("Ices-Access-Token")String token,
			@PathVariable String type,@PathVariable String flag,
			Pageable page
			){
		return equipmentService.queryequipmentUnit(queryNeType, queryNeName, queryUnitType, paramStr, token, type, flag, page);

	}

	@RequestMapping("api/v1/query-equipment-unit-all/{type}/{flag}")
	public List<EquipmentUnit> queryequipmentUnitAll(
			@RequestParam(value="queryNeType",required=false)String queryNeType,
			@RequestParam(value="queryNeName",required=false)String queryNeName,
			@RequestParam(value="queryUnitType",required=false)String queryUnitType,
			@RequestParam(value="paramStr",required=false)String paramStr,@RequestHeader("Ices-Access-Token")String token,
			@PathVariable String type,@PathVariable String flag
			){
		return equipmentService.findUnitList(token, type, flag, queryNeType, queryNeName, queryUnitType, paramStr);
	}


	@SuppressWarnings("rawtypes")
	@RequestMapping(value="api/v1/addedit-equipment-unit",method = RequestMethod.POST)
	public EquipmentUnit addOrEditEquipmentUnit(@RequestBody Map map){
		return equipmentService.addOrEditEquipmentUnit(map);
	}
}
