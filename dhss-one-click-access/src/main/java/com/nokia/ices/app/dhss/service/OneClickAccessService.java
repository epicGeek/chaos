package com.nokia.ices.app.dhss.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

public interface OneClickAccessService {
	void downloadLog( String filePath,HttpServletRequest request,HttpServletResponse response);

	public EquipmentUnit findEquipmentUnitById(Long id);

	public String generateSessionId(Long unitId, String token ,String userName);

}
