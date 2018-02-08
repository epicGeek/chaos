package com.nokia.ices.app.dhss.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.service.OneClickAccessService;

@Controller
public class IndexController {
	@Autowired
	private OneClickAccessService oneClickAccessService;

	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;

	@RequestMapping(path = "/mml-interface/{unitId}/{token}/{user}", method = RequestMethod.GET)
	public String getConnectTicket(@PathVariable String token, @PathVariable String unitId, @PathVariable String user,
			Model model) {
		String sessionId = oneClickAccessService.generateSessionId(Long.parseLong(unitId), token, user);
		model.addAttribute("sessionId", sessionId);
		model.addAttribute("token", token);
		model.addAttribute("equipmentUnit", oneClickAccessService.findEquipmentUnitById(Long.parseLong(unitId)));
		return "console";
	}

	@RequestMapping(path = "/mml-interface", method = RequestMethod.GET)
	public String getAllAccessPoint(@RequestHeader(value = "Ices-Access-Token", required = false) String token,
			Model model) {
		model.addAttribute("unitList", equipmentUnitRepository.findAll());
		return "unit-list";
	}

	@RequestMapping(path = "/t", method = RequestMethod.GET)
	public String t(Model model, HttpServletResponse response) throws UnsupportedEncodingException {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("test.xml", "UTF-8"));
		model.addAttribute("dataList", equipmentUnitRepository.findAll());
		return "t";
	}

}
