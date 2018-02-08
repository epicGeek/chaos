package com.nokia.ices.app.dhss.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.ProjectConfig;
import com.nokia.ices.app.dhss.repository.UserTestRepository;

@RestController
public class IndexController {
	
	@Autowired
	private UserTestRepository userTestRepository;
	
	@RequestMapping("/api/v1/index")
	public Map<String,Object> index() {
		Map<String,Object> map = new HashMap<>();
		map.put("users", userTestRepository.findAll());
		map.put("profile", ProjectConfig.getProfiles() +"_@_" + ProjectConfig.getTest());
		return map;
	}

}
