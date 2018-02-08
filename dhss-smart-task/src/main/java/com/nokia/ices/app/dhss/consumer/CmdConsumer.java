package com.nokia.ices.app.dhss.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.ProjectConfig;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResultTmp;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;
import com.nokia.ices.app.dhss.model.Message76001;
import com.nokia.ices.app.dhss.service.TaskService;
import com.nokia.ices.app.dhss.utils.ZipUtil;

@Component
public class CmdConsumer {

	private final static Logger logger = LogManager.getLogger(CmdConsumer.class);
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public static final String CMD_TASK_NAME = "smart-task-cmd-consumer";
	

	@SuppressWarnings({ "unchecked", "unused" })
	@JmsListener(destination = CMD_TASK_NAME, containerFactory = "jmsContainerFactory")
	public void message(Message message){
		String  flagStr = "";
		try {
			TextMessage messages = (TextMessage) message;
			String msgBody = message.getStringProperty("msgBody");
			Integer msgCode = message.getIntProperty("msgCode");
			
			if (null != msgBody) {
				Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
				String sessionid = String.valueOf(json.get("sessionid"));
				flagStr = StringUtils.isNotBlank(sessionid) ? sessionid.split("@@")[1] : "";
				logger.info(flagStr+"Message  ：msgCode:{},msgBody:{}", msgCode, msgBody);
				String resultCode = String.valueOf(json.get("flag"));
				String flag = resultCode.equals("0") ? "2" : "3";
				String path = String.valueOf(json.get("src"));
    			String errorMessage = json.get("msg") ;
    			
    			
    			Map<String,Object> mapSessage = new HashMap<String,Object>();
    			if(StringUtils.isNoneEmpty(sessionid)){
    				mapSessage.put("uuId_EQ",sessionid);
    			}
    			SmartCheckResultTmp temp = taskService.getSmartCheckResultTmpByUUID(mapSessage);
    			
    			String model = flagStr;
    			temp.setExecFlag(flag);
				temp.setErrorMessage(errorMessage);
				temp.setResultCode(resultCode.equals("0"));
				temp.setFilePath(path);
    			if(resultCode.equals("0") && temp.getScript() != null && !"".equals(temp.getScript())){
    				String zipScript = ZipUtil.compress(temp.getScript());
					logger.info(flagStr+"Analytical timing patrol task，NE:{}::uuId:{}:::Execution inspection task：{}", temp.getNeName() + ":::" + temp.getUnitName(),sessionid, temp.getCheckItemName());
					jmsTemplate.setDefaultDestinationName(ProjectConfig.getScriptServerName());
			    	jmsTemplate.send(new MessageCreator(){
						public Message createMessage(Session session) throws JMSException {
							taskService.saveSmartCheckResultTmp(temp);
							Message76001 message = new Message76001();
							message.setDestQ(ProjectConfig.getScriptServerName());
							message.setSession(sessionid);
							message.setSrcQ(ScriptConsumer.SCRIPT_TASK_NAME); 
							String type = temp.getScriptType() == null ? "1" : temp.getScriptType();
							message.setType(type);
							message.setScript_type(type);
							message.setMsgCode(76005);
							message.setLog_path(ProjectConfig.getBasePath()+path);
							TextMessage txtMessage = session.createTextMessage("");
							txtMessage.setStringProperty("msgBody", new JsonMapper().toJson(message));
							txtMessage.setIntProperty("msgCode", 76005);
							txtMessage.setJMSPriority(5);
							txtMessage.setText(zipScript);
							logger.info(model+"message.getSrcQ() ={},message.getDestQ() = {},txtMessag={}",message.getSrcQ() ,message.getDestQ(),txtMessage.toString());				 
							return txtMessage;
						}
					});
			    	
    			}else{
    				try {
    					
    					SmartCheckResult smartCheckResult = new SmartCheckResult(temp.getNeId(), temp.getNeType(), temp.getNeName(), temp.getUnitId(), 
    							temp.getUnitType(), temp.getUnitName(), temp.getCheckItemId(), temp.getCheckItemName(), temp.getScheduleId(), 
    							temp.isResultCode(), temp.getErrorMessage(), temp.getFilePath(), temp.getStartTime(), true, "");
    					smartCheckResult.setDhssName(temp.getDhssName());
    					logger.info(flagStr+"smartCheckResult{}",smartCheckResult);
    					taskService.saveSmartCheckResult(smartCheckResult);
    					
    					if(!resultCode.equals("0")){
    						logger.info(flagStr+"errorMessage：{},file_path:{}",errorMessage,path);
    						
    						SmartCheckScheduleResult result = taskService.getSmartCheckScheduleResultById(temp.getScheduleId());
    						Integer size = taskService.findSmartCheckResultErrorSize(temp.getScheduleId());
    						result.setErrorUnit(size);
    						taskService.saveSmartCheckScheduleResult(result);
    						
    						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    						AlarmMonitor alarm = new AlarmMonitor(temp.getErrorMessage(), "*", "", temp.getCheckItemName(), "Healthy check",
    								"", temp.getNeName(), temp.getNeType(), format.format(new Date()), temp.getFilePath(), temp.getUnitName(), 
    								temp.getUnitType());
    						
    						taskService.saveAlarmMonitor(alarm);
    						logger.info(flagStr+"AlarmMonitor{}",alarm);
    					}
    					
    					
    					
    					
    					
						
					} catch (Exception e) {
						logger.warn(flagStr+e.getMessage());
					}
    			}
    			
			}
			
		} catch (JMSException e) {
			logger.warn(flagStr+e.getMessage());
		}
	}
	
	
	
	
	
	
	
}
