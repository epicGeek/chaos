package com.nokia.ices.app.dhss.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.service.AhubConnInfoService;
@RestController
public class AhubConnInfoController {

	// private static final Logger logger = LoggerFactory.getLogger(AhubConnInfoController.class);

	@Autowired
	private AhubConnInfoService ahubConfigService;

	@RequestMapping(value = "/api/v1/ahub-info/template-download")
	public void ahubTemplatedownload(HttpServletRequest request,HttpServletResponse response) {
		ahubConfigService.downloadTemplate(request,response);
	}
	
	@RequestMapping(value = "/api/v1/ahub-info/all-ahub-info-download")
	public void exportAhubInfo(HttpServletRequest request,HttpServletResponse response) {
		ahubConfigService.exportAhubData(request,response);
	}
	
	@RequestMapping(value = "/api/v1/ahub-info/upload-template",method = RequestMethod.POST)
	public Map<String,String> exportAhubInfo(@RequestParam("templateFile") MultipartFile multiQueryTemplate) {
		return ahubConfigService.importAhubInfoData(multiQueryTemplate);
	}
}
