package com.nokia.ices.app.dhss.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.persistence.criteria.Predicate.BooleanOperator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.ProjectConfig;
import com.nokia.ices.app.dhss.consumer.CommandConsumer;
import com.nokia.ices.app.dhss.consumer.ServiceConsumer;
import com.nokia.ices.app.dhss.core.utils.Encodes;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.ems.EmsCheckJob;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitor;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitorHistory;
import com.nokia.ices.app.dhss.domain.ems.EmsMutedItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.jms.model.MessageModel;
import com.nokia.ices.app.dhss.jpa.DynamicSpecifications;
import com.nokia.ices.app.dhss.jpa.SearchFilter;
import com.nokia.ices.app.dhss.message.MessageService;
import com.nokia.ices.app.dhss.message.MessageSms;
import com.nokia.ices.app.dhss.repository.command.CommandCheckItemRepository;
import com.nokia.ices.app.dhss.repository.ems.EmsCheckJobRepository;
import com.nokia.ices.app.dhss.repository.ems.EmsMonitorHistoryRepository;
import com.nokia.ices.app.dhss.repository.ems.EmsMonitorRepoitory;
import com.nokia.ices.app.dhss.repository.ems.EmsMutedItemRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.service.TaskService;

@Component
public class TaskServiceImpl implements TaskService{
	
	private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	@Autowired
	private EmsCheckJobRepository emsCheckJobRepository;
	
	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;
	
	@Autowired
	private CommandCheckItemRepository commandCheckItemRepository;
	
	@Autowired
	private EmsMutedItemRepository emsMutedItemRepository;
	
	@Autowired
	private EmsMonitorRepoitory emsMonitorRepoitory;
	
	@Autowired
	private EmsMonitorHistoryRepository emsMonitorHistoryRepository;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public EmsCheckJob findEmsCheckJobById(Long id) {
		return emsCheckJobRepository.findOne(id);
	}
	
	@Override
	public void execEmsJob(EmsCheckJob emsCheckJob) {
		StringBuffer unitStr = new StringBuffer(emsCheckJob.getUnits());
		StringBuffer commandStr = new StringBuffer(emsCheckJob.getCommands());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id_IN", arrayToList(unitStr.replace(0, 1, "").toString().split(",")));
		List<EquipmentUnit> unitList = findUnits(map);
		map.put("id_IN", arrayToList(commandStr.replace(0, 1, "").toString().split(",")));
		List<CommandCheckItem> commandList = findCommands(map);
		for (EquipmentUnit equipmentUnit : unitList) {
			for (CommandCheckItem commandCheckItem : commandList) {
				logger.info("in 【"+equipmentUnit.getUnitName() + "】 exec 【"+commandCheckItem.getName() + "：" + commandCheckItem.getCommand() + "】checkItem!");
				sendMessage(equipmentUnit, commandCheckItem,emsCheckJob.getRoles());
			}
		}
		
	}
	
	@Override
	public void updateJobExecTime(EmsCheckJob emsCheckJob) throws ParseException {
		emsCheckJob.setExecDate(emsCheckJob.getNextDate());
		formatDate(emsCheckJob , emsCheckJob.getJobType());
		emsCheckJobRepository.save(emsCheckJob);
	}
	
	private void formatDate(EmsCheckJob emsCheckJob, int type) throws ParseException {
		int [] minutes = new int[]{5,10,15,20,30,60,0,1440};
		String format = "";
		Date thisDate = emsCheckJob.getNextDate();
		switch (type) {
			case 0:case 1:case 2:case 3:case 4:case 5:case 7:
				format = formatDate.format(thisDate.getTime() + (1000 * 60 * minutes[type]));
				break;
			case 6:
				format = formatDate.format(thisDate.getTime() + (1000 * 60 * (60 * emsCheckJob.getHour())));
				break;
		}
		emsCheckJob.setNextDate(formatDate.parse(format));
		logger.info("update 【" + emsCheckJob.getJobName() + "】next execDate ：" + emsCheckJob.getNextDate());
	}
	
	
	private void sendMessage(EquipmentUnit equipmentUnit,CommandCheckItem commandCheckItem,String roleId){
		String uuid = UUID.randomUUID().toString().replace("-", "") + "@" + commandCheckItem.getId() + "-" + roleId;
		
		String unitType = equipmentUnit.getUnitType();
		
		String password = Encodes.encodeHex(equipmentUnit.getLoginPassword() != null ? equipmentUnit.getLoginPassword().getBytes() : "".getBytes());
		
		Map<String,String> content = new HashMap<String,String>();
		content.put("cmd", equipmentUnit.getServerProtocol() + "_DHLR_COMMAND|"+commandCheckItem.getAccount()+","+
				Encodes.encodeHex(equipmentUnit.getRootPassword() != null ? equipmentUnit.getRootPassword().getBytes() : 
					"".getBytes())+":"+commandCheckItem.getCommand());
		content.put("ct", "2");
		content.put("rt", "1");
		
		MessageModel model = new MessageModel(unitType.equals("SGW") || unitType.equals("SOAP_GW") ? 8  : 4,"dhss", 
				equipmentUnit.getNeType(),CommandConsumer.EMS_CMD_TASK,ProjectConfig.getDesQName(),uuid, equipmentUnit.getUnitName(), 
				"DHSS_"+equipmentUnit.getServerProtocol(), password, equipmentUnit.getServerPort(),5, 
				equipmentUnit.getServerProtocol(),equipmentUnit.getLoginName(), equipmentUnit.getServerIp(), 
				content, equipmentUnit.getHostname(), "","", "", "", 
				equipmentUnit.getUnitType(), 71000);
		
        jmsTemplate.setDefaultDestinationName(ProjectConfig.getDesQName());
        jmsTemplate.send(new MessageCreator(){
			public Message createMessage(Session session) throws JMSException {
				 	TextMessage txtMessage = session.createTextMessage("");
	                txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(model));
	                txtMessage.setIntProperty("msgCode", model.getMsgCode());
	                txtMessage.setJMSPriority(5);
	                logger.debug(new JsonMapper().toJson(model));
	                logger.debug("message.getSrcQ() = {},message.getDestQ() = {}", model.getSrcQ(), model.getDestQ());
	                logger.debug(txtMessage.toString());
	                return txtMessage;
			}
		});
	}
	
	
	@Override
	public void saveEmsMonitor(EmsMonitor emsMonitor) {
		emsMonitorRepoitory.save(emsMonitor);
	}
	
	@SuppressWarnings("unused")
	@Override
	public void noticeGroup(String groupId,String message,String unit, String item,String msg,boolean flag,String command) {
		String msgBody = msg + ":" + message;
		if(flag){
			msgBody = "clear ： " + msg + " : " + command ;
		}
		if(isNotCancel(unit, item) == 0){
			if(groupId != null && !"".equals(groupId)){
				
				String [] array = groupId.split(",");
				for (String string : array) {
					if("".equals(string)){
						continue;
					}
					/*SystemRole role = systemRoleRepository.findOne(Long.parseLong(string));
					Collection<SystemRole> roles = new HashSet<SystemRole>();
					roles.add(role);
					List<SystemUser> userList = systemUserRepository.findUserBySystemRoleIn(roles);
					
					Map<String, String> userMap  = new HashMap<String, String>();
					for (SystemUser systemUser : userList) {
						if(systemUser.getMobile() != null && !systemUser.getMobile().equals("")){
							userMap.put(systemUser.getMobile(),msgBody);
						}
					}
					for (String key : userMap.keySet()) {
						sendMessageSms(key, userMap.get(key));
					}*/
				}
			}
			
		}else{
			logger.info(msg + ": Has canceled the notification function！");
		}
	}
	
	@Override
	public void sendMessageSms(String moblie,String smscontent){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jmsTemplate.setDefaultDestinationName(ProjectConfig.getSmsName());
    	jmsTemplate.send(new MessageCreator(){
			public Message createMessage(Session session) throws JMSException {
				MessageSms message = new MessageSms();
				message.setMobile(moblie);
				message.setSmscontent(smscontent + " " + format.format(new Date()));
				
				TextMessage txtMessage = session.createTextMessage("");
				txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(message));
				txtMessage.setJMSPriority(5);
				logger.debug("message.getSrcQ() ={},message.getDestQ() = {},txtMessag={}",txtMessage.toString());				 
				return txtMessage;
			}
		});
	}
	
	
	@Override
	public int isNotCancel(String unit, String item) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("mutedUnitId_EQ", unit);
		map.put("mutedCommandId_EQ", item);
		try {
			map.put("resumeTime_GT", formatDate.parse(formatDate.format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String, SearchFilter> filter = SearchFilter.parse(map);
		Specification<EmsMutedItem> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				EmsMutedItem.class);
		return emsMutedItemRepository.findAll(spec).size();
		
	}
	
	@Override
	public List<EmsMonitor> findEmsMonitors(Map<String, Object> map) {
		Map<String, SearchFilter> filter = SearchFilter.parse(map);
		Specification<EmsMonitor> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				EmsMonitor.class);
		return emsMonitorRepoitory.findAll(spec);
	}
	
	@Override
	public void saveEmsMonitorHistory(EmsMonitorHistory emsMonitorHistory) {
		emsMonitorHistoryRepository.save(emsMonitorHistory);
	}
	
	
	@Override
	public void sendMessageService(Map<String, Object> json,EquipmentUnit equipmentUnit,CommandCheckItem commandCheckItem){
		jmsTemplate.setDefaultDestinationName(ProjectConfig.getScriptServerName());
    	jmsTemplate.send(new MessageCreator(){
			public Message createMessage(Session session) throws JMSException {
				MessageService message = new MessageService();
				message.setDestQ(ProjectConfig.getScriptServerName());
				message.setSession(UUID.randomUUID().toString().replace("-", ""));
				message.setSrcQ(ServiceConsumer.EMS_SERVICE_TASK);
				message.setType("1");
				message.setLog_path(ProjectConfig.getBasePath()+json.get("src").toString());
				message.setInvariant(equipmentUnit.getId()+"#"+equipmentUnit.getUnitName()+"@"+commandCheckItem.getId()+
												"#"+commandCheckItem.getName() + "@" + json.get("sessionid").toString().split("-")[1]/*"2#SHHSS50SG01-ESA40-1@1#版本@3"*/);
				message.setMsgCode(76005);
				TextMessage txtMessage = session.createTextMessage("");
				txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(message));
				txtMessage.setIntProperty("msgCode", 76005);
				txtMessage.setJMSPriority(5);
				txtMessage.setText(compress(StringUtils.isEmpty(commandCheckItem.getScript()) ?  "" : commandCheckItem.getScript()));
				logger.debug("message.getSrcQ() ={},message.getDestQ() = {},txtMessag={}",message.getSrcQ() ,message.getDestQ(),txtMessage.toString());				 
				return txtMessage;
			}
		});
	}
	
	
	@Override
	public List<CommandCheckItem> findCommands(Map<String, Object> map) {
		Map<String, SearchFilter> filter = SearchFilter.parse(map);
		Specification<CommandCheckItem> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				CommandCheckItem.class);
		return commandCheckItemRepository.findAll(spec);
	}
	
	@Override
	public List<EquipmentUnit> findUnits(Map<String,Object> map) {
		Map<String, SearchFilter> filter = SearchFilter.parse(map);
		Specification<EquipmentUnit> spec = DynamicSpecifications.bySearchFilter(filter.values(), BooleanOperator.AND,
				EquipmentUnit.class);
		return equipmentUnitRepository.findAll(spec);
	}
	
	private List<Long> arrayToList(String [] array){
		List<Long> list = new ArrayList<Long>();
		for (String string : array) {
			list.add(Long.parseLong(string));
		}
		return list;
	}
	
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
	
	public static String uncompress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int n;
			while ((n = gunzip.read(buffer)) != 0) {
				if (n >= 0) {
					out.write(buffer, 0, n);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		return out.toString();
	}

}
