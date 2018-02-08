package com.nokia.ices.app.dhss.controller;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nokia.ices.app.dhss.service.TopologyService;

@Controller
public class IndexController {
	
	@Autowired
	private TopologyService topologyService;
	
	@RequestMapping("topology")  
	public String index(Model model,@RequestParam(value="token",required=false) String token,@RequestParam(value="dhss",required=false)String dhss,
			@RequestParam(value="userName",required=false)String userName,
			@RequestParam(value="language",required=false)String language){
		model.addAttribute("token", token);
		model.addAttribute("userName", userName);
		Set<String> dhssList = new TreeSet<>(topologyService.findDhssList(token));
		System.out.println(dhssList.size());
		model.addAttribute("dhssList", dhssList);
		model.addAttribute("language", language);
		model.addAttribute("dhssName",StringUtils.isNotBlank(dhss) ?  dhss : (dhssList.size() == 0 ? "" : dhssList.iterator().next()));
		language = StringUtils.isNotBlank(language) ? language : "zh";
		return "topology-" + language;
	}

}
