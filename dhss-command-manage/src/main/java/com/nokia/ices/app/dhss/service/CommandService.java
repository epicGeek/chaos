package com.nokia.ices.app.dhss.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.nokia.ices.app.dhss.domain.command.CheckItemGroup;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommand;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommandGroup;

public interface CommandService {
	
	@SuppressWarnings("rawtypes")
	public List findResourceList(String token,String type,boolean flag);
	
	public void saveCheckItemGroup(List<CheckItemGroup> list,Long groupId);

	List<CommandCheckItem> findAll(Map<String, Object> map,String token,String flag);

	public Map<String, Object> getSelectedDatas(String token);

	public void saveSubscriberCommandGroup(List<SubscriberCommandGroup> list, Long groupId);

	public List<SubscriberCommand> findSubscriberCommandAll(String token, String flag);
	
	public Map<String,String> execTestLua(String script,String luaLog,String scriptType);
	
	/*public Map<String,String> importLuaTestLog(MultipartFile multiQueryTemplate);*/
	
}
