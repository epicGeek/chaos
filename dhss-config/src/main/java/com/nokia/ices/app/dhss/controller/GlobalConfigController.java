package com.nokia.ices.app.dhss.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalConfigController {
	@RequestMapping(value = "/api/v1/global-config/url")
	public Map<String, String> findAllUrlEndPoint() {
       Map<String,String> urlMap = new HashMap<String, String>();
       return urlMap;
	}
}
