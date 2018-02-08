package com.nokia.ices.app.dhss.consumer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitor;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.service.TaskService;


@Component
public class CommandConsumer {
	
	private Logger logger = LoggerFactory.getLogger(CommandConsumer.class);
	
	public static final String EMS_CMD_TASK = "EMS_CMD_TASK";
	
	@Autowired
	private TaskService taskService;
	
	@SuppressWarnings({ "unused", "unchecked" })
	@JmsListener(destination = EMS_CMD_TASK, containerFactory = "jmsContainerFactory")
	public void reseiveMessageForEmsCheckJobTask(Message message) {
		
		try {
			TextMessage messages = (TextMessage) message;
			String msgBody =  message.getStringProperty("msgBody");
			logger.info("消息返回信息  --- ：msgBody:{}", msgBody);
			Map<String, Object> json = (Map<String, Object>) new JsonMapper().fromJson(msgBody, Map.class);
			
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("unitName_EQ", json.get("ne"));
			List<EquipmentUnit> units = taskService.findUnits(map);
			map.clear();
			Map<String, Object> content = (Map<String, Object>)json.get("content");
			/*content.get("cmd").toString().substring(content.get("cmd").toString().indexOf(":")+1))*/
			String [] array = json.get("sessionid").toString().split("@")[1].split("-");
			map.put("id_EQ", array[0]);
//			map.put("emsType_EQ", "EMS");
			List<CommandCheckItem> items = taskService.findCommands(map);
			
			EquipmentUnit unit = units.size() == 0 ? null : units.get(0);
			CommandCheckItem item = items.size() == 0 ? null : items.get(0);
			
			if(json.get("flag") != null && Integer.parseInt(json.get("flag").toString()) == 0){
				logger.info("EXEC SUCCESS，send serviceScript");
				taskService.sendMessageService(json,unit,item);
			}else{
				try {
					logger.info("exec error ：insert into DB");
					Map<String,Object> emsmap = new HashMap<String,Object>();
					emsmap.put("monitoredUnitId_EQ", String.valueOf(unit.getId()));
					emsmap.put("monitoredCommandId_EQ", String.valueOf(item.getId()));
					
					List<EmsMonitor>  ess = taskService.findEmsMonitors(emsmap);
					EmsMonitor em = ess.size() == 0 ? new EmsMonitor() : ess.get(0); 
					
					String msg = json.get("msg") == null ? "" : json.get("msg").toString();
					
					if(!"3".equals(em.getResultLevel())){
						taskService.noticeGroup(array[1], msg,String.valueOf(unit.getId()), String.valueOf(item.getId()), unit.getUnitName(), false, "");
					}
					
					em.setData(new Date(), item.getId(),item.getName(), unit.getId(),
							unit.getUnitName(), msg, "3", "",msg ,json.get("sessionid").toString().split("-")[1]);
					
					taskService.saveEmsMonitor(em);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
