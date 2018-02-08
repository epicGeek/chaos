package com.nokia.ices.app.dhss.consumer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitor;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitorHistory;
import com.nokia.ices.app.dhss.service.TaskService;
import com.nokia.ices.app.dhss.service.impl.TaskServiceImpl;



@Component
public class ServiceConsumer {
	
	private Logger logger = LoggerFactory.getLogger(ServiceConsumer.class);
	
	@Autowired
	private TaskService taskService;
	
	
	
	public static final String EMS_SERVICE_TASK = "EMS_SERVICE_TASK";
	
	@SuppressWarnings("unchecked")
	@JmsListener(destination = EMS_SERVICE_TASK, containerFactory = "jmsContainerFactory")
	public void reseiveMessageForSmartCheckJobTask(Message message) {
		
		
		try {
			TextMessage messages = (TextMessage) message;
			String msgBody = message.getStringProperty("msgBody");
			logger.info("msgBody  --- ：msgBody:{}", msgBody);
			String luaResult = messages.getText();
			Map<String, String> map = new HashMap<String,String>();
 			if(luaResult != null && !("").equals(luaResult)){
				luaResult = TaskServiceImpl.uncompress(luaResult);
				map = (Map<String, String>) new JsonMapper().fromJson(luaResult, Map.class);
			}
 			Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
			logger.info("SERVICE message  --- ：msgBody:{}", msgBody);
			logger.info("luaResult  --- ：luaResult:{}", map);
			String string  = json.get("invariant");
			String [] array = string.split("@");
			String [] s = array[0].split("#");
			String [] s1 = array[1].split("#");
			
			
			Map<String,Object> emsmap = new HashMap<String,Object>();
			emsmap.put("monitoredUnitId_EQ", s[0]);
			emsmap.put("monitoredCommandId_EQ", s1[0]);
			List<EmsMonitor>  ess = taskService.findEmsMonitors(emsmap);
			
			
			String historyFlag = "";
			boolean isNoticeFlag = true;
			boolean flag = false;
			String msg = "";
			EmsMonitor em = ess.size() == 0 ? new EmsMonitor() : ess.get(0);
			if(em != null){
				if(em.getResultLevel() != null && !"0".equals(em.getResultLevel()) 
						&& map.get("level") != null && "0".equals(map.get("level")) ){
					em.setCancelTime(new Date());
					flag = true;
					msg = em.getNotificationContent();
				}
				if(em.getResultLevel() != null && !"".equals(map.get("level"))){
					int a = Integer.parseInt(em.getResultLevel());
					int b = Integer.parseInt(map.get("level"));
					historyFlag = a != 0 && b != 0 ? "（ALARM_REMIND）" : historyFlag;
					if(a!=0 && b != 0 && a>=b){
						isNoticeFlag = false;
						historyFlag = "（ALARM_REMIND）";
					}
				}
				
			}
			em.setData(new Date(),Long.parseLong(s1[0]),s1[1],Long.parseLong(s[0]),s[1],
					map.get("content"),map.get("level"),json.get("log_path"),historyFlag+map.get("value"),array[2]);
			taskService.saveEmsMonitor(em);
			
			
			EmsMonitorHistory emh = new EmsMonitorHistory();
			emh.setData(new Date(),Long.parseLong(s1[0]),s1[1],Long.parseLong(s[0]),s[1],
					map.get("content"),map.get("level"),json.get("log_path"),historyFlag+map.get("value"),array[2]);
			taskService.saveEmsMonitorHistory(emh);
			
			if(isNoticeFlag){
				if(flag || (map.get("level") != null && !map.get("level").equals("0"))){
					taskService.noticeGroup(array[2], map.get("content"),s[0],s1[0],/*"单元：" + */s[1] /*+ ",指令:" + s1[1]*/ ,flag,msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
