package com.nokia.ices.app.dhss.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.criteria.Predicate.BooleanOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.nokia.ices.app.dhss.config.ProjectProperties;
import com.nokia.ices.app.dhss.consumer.LuaTestConsumer;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.command.CheckItemGroup;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommand;
import com.nokia.ices.app.dhss.domain.subscriber.SubscriberCommandGroup;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.jpa.SearchFilter.Operator;
import com.nokia.ices.app.dhss.model.Message76005;
import com.nokia.ices.app.dhss.repository.command.CheckItemGroupRepository;
import com.nokia.ices.app.dhss.repository.command.CommandCheckItemRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.repository.subscriber.SubscriberCommandGroupRepository;
import com.nokia.ices.app.dhss.repository.subscriber.SubscriberCommandRepository;
import com.nokia.ices.app.dhss.service.CommandService;
import com.nokia.ices.app.dhss.service.SecurityService;

@Service
public class CommandServiceImpl implements CommandService {
	
	
	@Autowired
	private CommandCheckItemRepository commandCheckItemRepository;
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private CheckItemGroupRepository checkItemGroupRepository;
	
	@Autowired
	private SubscriberCommandRepository subscriberCommandRepository;
	
	@Autowired
	private SecurityService securityService;
	
	@Autowired
	private SubscriberCommandGroupRepository subscriberCommandGroupRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private LuaTestConsumer luaTestConsumer;
	
	
	private final static Logger logger = LoggerFactory.getLogger(CommandServiceImpl.class);
	
	public static final String LUA_TEST_SRCQ = "Q_lua_test";
	
	
	@Override
	public Map<String, Object> getSelectedDatas(String token){
		Map<String, Object> rootMap = new HashMap<>();
		
		List<Map<String, String>> neData = getResource(token, "net", true);
		List<Map<String, String>> typeData = getResource(token, "neUnitType", true);
		Iterable<EquipmentUnit> unitResultList = findUnitList(neData, typeData);
		
		Set<String> vlidSet = new HashSet<>();
		List<Map<String, String>> dhssList = new ArrayList<>();
		
		List<Map<String, String>> locationList = new ArrayList<>();
		
		List<Map<String, String>> neList = new ArrayList<>();
		for (Map<String, String> map : neData) {
			if(vlidSet.add(map.get("dhss_name"))){
				Map<String, String> dhssMap = new HashMap<>();
				dhssMap.put("label", map.get("dhss_name"));
				dhssMap.put("value", map.get("dhss_name"));
				dhssList.add(dhssMap);
			}
			if(vlidSet.add(map.get("physical_location"))){
				Map<String, String> locationMap = new HashMap<>();
				locationMap.put("label", map.get("physical_location"));
				locationMap.put("value", map.get("physical_location"));
				locationList.add(locationMap);
			}
			Map<String, String> neMap = new HashMap<>();
			neMap.put("label", map.get("ne_name"));
			neMap.put("value", map.get("ne_name"));
			neMap.put("dhss", map.get("dhss_name"));
			neMap.put("location", map.get("physical_location"));
			neMap.put("neType", map.get("ne_type"));
			neList.add(neMap);
		}
		List<Map<String, String>> neTypeList = new ArrayList<>();
		List<Map<String, String>> unitTypeList = new ArrayList<>();
//		List<Map<String, String>> typeData = getResource(token, "neUnitType", false);
		for (Map<String, String> map : typeData) {
			if(vlidSet.add("ne_"+map.get("neType"))){
				Map<String, String> neTypeMap = new HashMap<>();
				neTypeMap.put("label", map.get("neType"));
				neTypeMap.put("value", map.get("neType"));
				neTypeMap.put("dhss", map.get("dhss"));
				neTypeList.add(neTypeMap);
			}
			if(vlidSet.add("unit_"+map.get("unitType"))){
				Map<String, String> unitTypeMap = new HashMap<>();
				unitTypeMap.put("label", map.get("unitType"));
				unitTypeMap.put("value", map.get("unitType"));
				unitTypeMap.put("neType", map.get("neType"));
				unitTypeList.add(unitTypeMap);
			}
		}
		
		
		List<Map<String, String>> unitList = new ArrayList<>();
		unitResultList.forEach(unit ->{
			Map<String, String> unitMap = new HashMap<>();
			unitMap.put("label", unit.getUnitName());
			unitMap.put("value", unit.getUnitName());
			unitMap.put("neName", unit.getNeName());
			unitMap.put("unitType", unit.getUnitType());
			unitList.add(unitMap);
		});
		
		rootMap.put("dhss", dhssList);
		rootMap.put("location", locationList);
		rootMap.put("ne", neList);
		rootMap.put("neType", neTypeList);
		rootMap.put("unitType", unitTypeList);
		rootMap.put("unit", unitList);
		return rootMap;
	}
	
	
	private List<EquipmentUnit> findUnitList(List<Map<String, String>> neData, List<Map<String, String>> typeList){
		List<Long> neIds = new ArrayList<>();
		for (Map<String,String> map : neData) {
			Object id = map.get("id");
			String idStr = String.valueOf(id);
			neIds.add(Long.valueOf(idStr)); 
		}
		List<SearchFilter> searchFilterNeTypeOr = new ArrayList<SearchFilter>();
		List<SearchFilter> searchFilterUnitTypeOr = new ArrayList<SearchFilter>();
		Set<String> neTypeSet = new HashSet<>();
		Set<String> unitTypeSet = new HashSet<>();
		for (Map<String, String> map : typeList) {
			if(neTypeSet.add(map.get("neType"))) {
				searchFilterNeTypeOr.add(new SearchFilter("neType", Operator.EQ,map.get("neType")));
			}
			if(unitTypeSet.add(map.get("unitType"))) {
				searchFilterUnitTypeOr.add(new SearchFilter("unitType", Operator.EQ,map.get("unitType")));
			}
		}
		
		Specification<EquipmentUnit> speciFicationsNeTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterNeTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		Specification<EquipmentUnit> speciFicationsUnitTypeAND = DynamicSpecifications
				.bySearchFilter(searchFilterUnitTypeOr, BooleanOperator.OR,EquipmentUnit.class);
		 
		List<SearchFilter> searchFilterAND = new ArrayList<SearchFilter>();
		searchFilterAND.add(new SearchFilter("neId", Operator.IN,neIds));
		Specification<EquipmentUnit> speciFicationsAND = DynamicSpecifications
				.bySearchFilter(searchFilterAND, BooleanOperator.AND,EquipmentUnit.class);
		
		return equipmentUnitRepository.findAll(Specifications.where(speciFicationsAND).and(speciFicationsNeTypeAND).and(speciFicationsUnitTypeAND));
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Map<String, String>> getResource(String token,String type,boolean flag){
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		return securityService.getResource(paramsMap,flag);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List findResourceList(String token,String type,boolean flag) {
		Map paramsMap = new HashMap<>();
		paramsMap.put("token", token);
		paramsMap.put("resourceFlag", type);
		paramsMap.put("contentFlag", "1");
		paramsMap.put("assocResourceFlag", "");
		paramsMap.put("assocResourceAttr", "");
		paramsMap.put("assocResourceAttrValue", "");
		List sourceData = securityService.getResource(paramsMap,flag);
		return sourceData;
	}

	@Override
	public void saveCheckItemGroup(List<CheckItemGroup> list,Long groupId) {
		checkItemGroupRepository.delete(checkItemGroupRepository.findByGroupIdEquals(groupId));
		checkItemGroupRepository.save(list);
	}
	
	@Override
	public void saveSubscriberCommandGroup(List<SubscriberCommandGroup> list,Long groupId) {
		subscriberCommandGroupRepository.delete(subscriberCommandGroupRepository.findByGroupIdEquals(groupId));
		subscriberCommandGroupRepository.save(list);
	}
	
	@Override
	public List<CommandCheckItem> findAll(Map<String, Object> map,String token,String flag){
		List<Long> IdList = new ArrayList<>();
		List<Map<String, String>> groupList = getResource(token, flag, true);
		for (Map<String, String> group : groupList) {
			Object objId = group.get("id");
			IdList.add(Long.parseLong(objId.toString()));
		}
		List<CheckItemGroup> checkGroupList = checkItemGroupRepository.findByGroupIdIn(IdList);
		IdList.clear();
		for (CheckItemGroup checkItemGroup : checkGroupList) {
			IdList.add(checkItemGroup.getCheckItemId());
		}
		if(IdList.size() == 0){
			return new ArrayList<>();
		}
		map.put("id_IN", IdList);
		Map<String,SearchFilter> filter = SearchFilter.parse(map);
		Specification<CommandCheckItem> spec = 
                DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND, CommandCheckItem.class);
		return commandCheckItemRepository.findAll(spec);
	}

	@Override
	public List<SubscriberCommand> findSubscriberCommandAll(String token,String flag){
		List<Long> IdList = new ArrayList<>();
		List<Map<String, String>> groupList = getResource(token, flag, true);
		for (Map<String, String> group : groupList) {
			Object objId = group.get("id");
			IdList.add(Long.parseLong(objId.toString()));
		}
		List<SubscriberCommandGroup> checkGroupList = subscriberCommandGroupRepository.findByGroupIdIn(IdList);
		IdList.clear();
		for (SubscriberCommandGroup checkItemGroup : checkGroupList) {
			IdList.add(checkItemGroup.getSubscriberCommandId());
		}
		if (IdList.size() == 0) {
			return new ArrayList<>();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id_IN", IdList);
		Map<String, SearchFilter> filter = SearchFilter.parse(paramMap);
		Specification<SubscriberCommand> spec = DynamicSpecifications.bySearchFilter(filter.values(),
				BooleanOperator.AND, SubscriberCommand.class);
		return subscriberCommandRepository.findAll(spec);
	}


	@Override
	public Map<String,String> execTestLua(String script, String luaLog,String scriptType) {
		
//		 报文写入文件供scriptService读取
		mkdirs(ProjectProperties.getLuaTestLogPath() + currtime_path());
		String filename_url = ProjectProperties.getLuaTestLogPath() + currtime_path() + (int)(100000+Math.random()*9900000) + date2Stc() + ".src";
//		String filename_url = "/home/jrdas/data/luaTestLogPath/325658320171107123107.src";
		logger.info("filename_url: " + filename_url);
		iowrite(new String(luaLog), filename_url);

//		 发送amq消息给scripService
		logger.info("script: "+script);
		String zipScript = compress(script);
		String sessionId = UUID.randomUUID().toString().replaceAll("-", "");
		logger.info("SEND AMQ-----------------------------------START");
		jmsTemplate.setDefaultDestinationName(ProjectProperties.getLuaTestDestQ());
		jmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message76005 message = new Message76005();
				message.setDestQ(ProjectProperties.getLuaTestDestQ());
				message.setSession(sessionId);
				message.setSrcQ(LUA_TEST_SRCQ);
				message.setScript_type(scriptType);
				message.setType(scriptType);
				message.setLog_path(filename_url);
				message.setDynamic_script("true");
				message.setInvariant("");
				message.setReply_type("0");
				TextMessage txtMessage = session.createTextMessage("");
				txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(message));
				txtMessage.setIntProperty("msgCode", 76005);
				txtMessage.setText(zipScript);
				return txtMessage;
			}
		});
		logger.info("SEND AMQ-----------------------------------END");
		
		for (int i = 0; i < 15; i++) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(luaTestConsumer.resultMap.get(sessionId) != null) {
				return luaTestConsumer.resultMap.get(sessionId);
			}
			
		}
		return null;
	}
	
	/*@Override
	public Map<String,String> importLuaTestLog(MultipartFile multiQueryTemplate) {
		Map<String,String> result = new HashMap<>();
		try {
			File uploadFile = UploadFileUtil.saveUploadFileToDest(multiQueryTemplate, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}*/
	
	public static String compress(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		String result = "";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != gzip) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			result = out.toString("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private String mkdirs(String path) {
		File file = new File(path);
		if ((!file.exists()) && (!file.isDirectory())) {
			logger.info("dir is not exist, create dir !");
			file.mkdirs();
		}
		return path;
	}
	
	private String currtime_path() {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		String date = sd.format(new Date());
		date = date.replace("-", "/");
		return date + "/";
	}
	
	private String date2Stc() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	
	private void iowrite(String content, String path) {
		try {
			content = new String(content.getBytes("utf-8"), "utf-8");
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
