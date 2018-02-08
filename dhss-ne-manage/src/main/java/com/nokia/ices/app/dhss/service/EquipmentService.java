package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

public interface EquipmentService {
	
	@SuppressWarnings("rawtypes")
	public List getSystemManageNeList(String token,String type,String flag);
	
	public List<EquipmentUnit> findUnitList(String token,String type,String flag,String queryNeType,String queryNeName,String queryUnitType,String paramStr);
	
	public void exportUnitData(String token,String type,String flag,String queryNeType,String queryNeName,String queryUnitType,String paramStr, HttpServletResponse response);
	
	@SuppressWarnings("rawtypes")
	public EquipmentUnit addOrEditEquipmentUnit(Map map);
	
	public Page<EquipmentUnit> queryequipmentUnit(String queryNeType,String queryNeName,String queryUnitType,String paramStr,String token,String type,String flag,Pageable page);

}
