package com.nokia.ices.app.dhss.service;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.config.SmartProjectProperties;
import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.event.SmartCheckJobEventHandler;
import com.nokia.ices.app.dhss.jms.consumer.MessageConsumer;


@Component
public class TaskConsumer {
    
     
    
    	private final static Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    	
    	
    	@SuppressWarnings({ "unchecked" })
		@JmsListener(destination = SmartProjectProperties.smarTaskName, containerFactory = "jmsContainerFactory")
    	public void newreseiveMessage(Message message){
    		try {
    			TextMessage messages = (TextMessage) message;
    			String msgBody =  messages.getText();
//    			Integer msgCode = message.getIntProperty("msgCode");
    			Map<String, String> mapBody = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
    			String messageCode = mapBody.get("messageCode");
    			logger.info("消息返回信息  --- ：msgCode:{},msgBody:{}", messageCode, msgBody);
    			if (!messageCode.equals("70004") && null != mapBody) {
//    				Map<String, Object> json = (Map<String, Object>) new JsonMapper().fromJson(msgBody, Map.class);
    				String taskName = mapBody.get("serviceName").toString();
        			String newMessage = mapBody.get("message").toString();
        			System.out.println(mapBody.get("isSuccess"));
        			String isSuccess = mapBody.get("isSuccess");
        			String nextime = mapBody.get("nextime");
        			SmartCheckJobEventHandler.messageResult.put(taskName, ( isSuccess.equals("0") ? (isSuccess+"_"+nextime) : (isSuccess+"_"+newMessage)));
    			}
    			
    		} catch (JMSException e) {
    			logger.info(e.getMessage());
    		}
    	}
    	
    	
//    	
    	
    
}

