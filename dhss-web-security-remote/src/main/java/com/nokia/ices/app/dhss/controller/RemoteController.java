package com.nokia.ices.app.dhss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.service.SecurityService;

@RestController
public class RemoteController {
	
	@Autowired
	private SecurityService securityService;
	
	@RequestMapping("api/v1/checkToken/{token}")
	public boolean checkToken(@PathVariable String token){
		return securityService.checkTokenExists(token);
	}

}
