package com.nokia.ices.app.dhss.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.domain.command.CheckItemGroup;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommand;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommandGroup;
import com.nokia.ices.app.dhss.service.CommandService;

@RestController
public class CommandController {
	
	@Autowired
	private CommandService commandService;
	
	
	@RequestMapping("/api/v1/init-select")
	public Map<String, Object> initSelectData(@RequestHeader("Ices-Access-Token")String token){
		Map<String, Object> rootMap = commandService.getSelectedDatas(token);
		return rootMap;
	}
	
	@RequestMapping("/api/v1/search/check-list/{resourceFlag}")
	public List<CommandCheckItem> searchCommandCheckItem(@RequestHeader("Ices-Access-Token")String token,
														 @PathVariable String resourceFlag,
														 @RequestParam(value="type",required=false)String type){
		Map<String,Object> paramMap = new HashMap<>();
		if(StringUtils.isNotBlank(type)){
			paramMap.put("category_EQ", type);
		}
		return commandService.findAll(paramMap,token,resourceFlag);
	}
	
	@RequestMapping("/api/v1/search/subscriber-command-list/{resourceFlag}")
	public List<SubscriberCommand> searchSubCommandCheckItem(@RequestHeader("Ices-Access-Token")String token,
														 @PathVariable String resourceFlag){
		return commandService.findSubscriberCommandAll(token,resourceFlag);
	}
	
	
//	@SuppressWarnings("rawtypes")
//	@RequestMapping("/api/v1/resource/{type}")
//	public List findGroupAll(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type){
//		return commandService.findResourceList(token,type,false);
//	}
//	
	@SuppressWarnings("rawtypes")
	@RequestMapping("/api/v1/commandResource/{type}")
	public List commandResource(@RequestHeader("Ices-Access-Token")String token,@PathVariable String type){
		return commandService.findResourceList(token,type,true);
	}
	
	@RequestMapping(value = "/api/v1/check-group-resource",method=RequestMethod.POST)
	public Boolean checkGroupResource(@RequestBody Map<String,String> paramsMap){
		String groupId = paramsMap.get("group");
		String [] resource = paramsMap.get("resource").split(",");
		List<CheckItemGroup> list = new ArrayList<CheckItemGroup>();
		if(StringUtils.isNotBlank(paramsMap.get("resource"))){
			for (String string : resource) {
				CheckItemGroup group = new CheckItemGroup();
				group.setGroupId(Long.parseLong(groupId));
				group.setCheckItemId(Long.parseLong(string));
				list.add(group);
			}
		}
		commandService.saveCheckItemGroup(list,Long.parseLong(groupId));
		return true;
		
	}
	
	@RequestMapping(value = "/api/v1/subscriber-command-group-resource",method=RequestMethod.POST)
	public Boolean subscriberCommandGroupResource(@RequestBody Map<String,String> paramsMap){
		String groupId = paramsMap.get("group");
		String [] resource = paramsMap.get("resource").split(",");
		List<SubscriberCommandGroup> list = new ArrayList<SubscriberCommandGroup>();
		if(StringUtils.isNotBlank(paramsMap.get("resource"))){
			for (String string : resource) {
				SubscriberCommandGroup group = new SubscriberCommandGroup();
				group.setGroupId(Long.parseLong(groupId));
				group.setSubscriberCommandId(Long.parseLong(string));
				list.add(group);
			}
		}
		commandService.saveSubscriberCommandGroup(list,Long.parseLong(groupId));
		return true;
		
	}
	
	@RequestMapping(value = "/api/v1/test-lua",method=RequestMethod.POST)
	public Map<String,String> testLua(@RequestBody Map<String,String> paramsMap){
		return commandService.execTestLua(paramsMap.get("script").toString(),paramsMap.get("luaLog").toString(),paramsMap.get("scriptType").toString());
	}
	
	/*@RequestMapping(value = "/api/v1/lua-test/upload-template",method = RequestMethod.POST)
	public Map<String,String> importLuaTestLog(@RequestParam("templateFile") MultipartFile multiQueryTemplate) {
		return commandService.importLuaTestLog(multiQueryTemplate);
	}*/
	
//	@RequestMapping("/api/v1/check-item-list")
//	public List<CommandCheckItem> findCommandCheckItemAll(){
//		return commandService.findAll(new HashMap<>());
//	}

}
