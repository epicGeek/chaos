package com.nokia.ices.app.dhss.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.model.CommandModel;
import com.nokia.ices.app.dhss.service.SubscriberService;

@RestController
public class SubscriberController {
	
	@Autowired
	private SubscriberService subscriberService;
	
	
	
	@RequestMapping("api/v1/export-template/download")
	public void exportTemplate(@RequestParam(value="title",required=false)String title,
			@RequestParam(value="name",required=false)String name,
			@RequestParam(value="defaultValue",required=false)String defaultValue,
			HttpServletResponse response){
		subscriberService.exportTemplate(title, name, defaultValue,response);
	}
	
	@RequestMapping("api/v1/sub/upload/template")
	public List<Map<String,String>>  test(@RequestParam(value = "file", required = false) MultipartFile file,HttpServletRequest request){
		return subscriberService.importTemplate(file, request);
	}
	
	@RequestMapping(value = "api/v1/sub/sendCommand", method = RequestMethod.POST)
	public Map<String,Object> sendSubToolCommand(@RequestBody CommandModel commandModel,
												 @RequestHeader("Ices-Access-Token")String token,
												 HttpServletRequest request) {
		Map<String,Object> message = new HashMap<>();
		String path = request.getSession().getServletContext().getRealPath("subscriber");
		
		for (String sendCmd : commandModel.getCommandList()) {
			int satus  = subscriberService.sendCommandSubtool(sendCmd, commandModel.getCheckName(),
					commandModel.getUserName(),token,path);
			message.put(sendCmd, satus);
		}
		return message;
	}

}
