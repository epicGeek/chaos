package com.nokia.ices.app.dhss.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.service.OneClickAccessService;

@RestController
@RequestMapping("/api/v1")
public class OneClickAccessController {
	@Autowired
	private OneClickAccessService oneClickAccessService;

	@RequestMapping(path = "/download-log")
	public void downloadLog(@RequestParam(value = "filePath", required = false) String filePath,
			HttpServletRequest request, HttpServletResponse response) {
		oneClickAccessService.downloadLog(filePath, request, response);
	}

	@RequestMapping(path = "/one-click-check", method = RequestMethod.GET)
	public String checkParam(@RequestParam(value = "id", required = false) String id) {
		EquipmentUnit unit = oneClickAccessService.findEquipmentUnitById(Long.parseLong(id));
		if (unit.isDirect()) {
			return checkDirect(unit);
		} else {
			String str = checkDirect(unit);
			if (("true".equals(str))) {
				return checkNoDirect(unit);
			} else {
				return str;
			}

		}
	}

	private String checkNoDirect(EquipmentUnit unit) {
		if (StringUtils.isEmpty(unit.getJumperIp())) {
			return "The jumperIp is empty";
		}
		if (StringUtils.isEmpty(unit.getJumpProtocol())) {
			return "The jumperProtocol is empty";
		}
		if (StringUtils.isEmpty(unit.getJumperPort())) {
			return "The jumperPort is empty";
		}
		if (StringUtils.isEmpty(unit.getJumperUserName())) {
			return "The jumperUserName is empty";
		}
		if (StringUtils.isEmpty(unit.getJumperPassword())) {
			return "The jumperPassword is empty";
		}
		return "true";
	}

	private String checkDirect(EquipmentUnit unit) {
		if (StringUtils.isEmpty(unit.getServerIp())) {
			return "The serverIp is empty";
		}
		if (StringUtils.isEmpty(unit.getServerProtocol())) {
			return "The serverProtocol is empty";
		}
		if (unit.getServerPort() == 0) {
			return "The serverPort is empty";
		}
		if (StringUtils.isEmpty(unit.getLoginName())) {
			return "The LoginName is empty";
		}
		if (StringUtils.isEmpty(unit.getLoginPassword())) {
			return "The LoginPassword is empty";
		}
		return "true";
	}
}
