package com.nokia.ices.app.dhss.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResultTmp;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;
import com.nokia.ices.app.dhss.service.TaskService;
import com.nokia.ices.app.dhss.utils.ZipUtil;

@Component
public class ScriptConsumer {
	
	private final static Logger logger = LogManager.getLogger(CmdConsumer.class);
	
	public static final String SCRIPT_TASK_NAME = "smart-task-script-consumer";
	
	@Autowired
	private TaskService taskService;
	
	
	@SuppressWarnings("unchecked")
	@JmsListener(destination = SCRIPT_TASK_NAME, containerFactory = "jmsContainerFactory")
	public void message(Message message){
		String modelFlag = "";
		try {
			TextMessage messages = (TextMessage) message;
			String msgBody = message.getStringProperty("msgBody");
			Integer msgCode = message.getIntProperty("msgCode");
			String luaResult = messages.getText();
			
			if (null != msgBody) {
    			Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
    			String sessionid = String.valueOf(json.get("session"));
    			modelFlag = StringUtils.isNotBlank(sessionid) ? sessionid.split("@@")[1] : "";
    			logger.info(modelFlag+"SCRIPT SERVER Return information  --- ：msgCode:{},msgBody:{},luaResult:{}", msgCode, msgBody,luaResult);
    			Map<String,Object> mapSessage = new HashMap<String,Object>();
    			if(StringUtils.isNoneEmpty(sessionid)){
    				mapSessage.put("uuId_EQ",sessionid);
    			}
    			SmartCheckResultTmp temp = taskService.getSmartCheckResultTmpByUUID(mapSessage);
    			String resultCode = String.valueOf(json.get("result_code"));
    			String type = resultCode.equals("0") ? "2" : "3";
				if(StringUtils.isNotEmpty(luaResult)){
					type = "3";
					luaResult = ZipUtil.uncompress(luaResult);
					logger.info(modelFlag+"Analytical LUA results：{}",luaResult);
					
				}
				
				temp.setExecFlag(type);
				temp.setErrorMessage(luaResult);
				temp.setResultCode(type.equals("2"));
				
				SmartCheckResult smartCheckResult = new SmartCheckResult(temp.getNeId(), temp.getNeType(), temp.getNeName(), temp.getUnitId(), 
						temp.getUnitType(), temp.getUnitName(), temp.getCheckItemId(), temp.getCheckItemName(), temp.getScheduleId(), 
						temp.isResultCode(), temp.getErrorMessage(), temp.getFilePath(), temp.getStartTime(), true, "");
				smartCheckResult.setDhssName(temp.getDhssName());
				logger.info(modelFlag+"smartCheckResult：{}",smartCheckResult);
				taskService.saveSmartCheckResult(smartCheckResult);
				
				if(type.equals("3")){
					
					SmartCheckScheduleResult result = taskService.getSmartCheckScheduleResultById(temp.getScheduleId());
					Integer size = taskService.findSmartCheckResultErrorSize(temp.getScheduleId());
					result.setErrorUnit(size);
					logger.info(modelFlag+"SmartCheckScheduleResult error size：{}",result);
					taskService.saveSmartCheckScheduleResult(result);
					
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					AlarmMonitor alarm = new AlarmMonitor(temp.getErrorMessage(), "*", "", temp.getCheckItemName(), "Healthy check",
							"", temp.getNeName(), temp.getNeType(), format.format(new Date()), temp.getFilePath(), temp.getUnitName(), 
							temp.getUnitType());
					logger.info(modelFlag+"AlarmMonitor insert into：{}",alarm);
					taskService.saveAlarmMonitor(alarm);
				}
				
				
			}
			
		} catch (Exception e) {
			logger.error(modelFlag+e.getMessage());
		}
	}
}
















