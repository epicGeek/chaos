package com.nokia.ices.app.dhss.consumer;

import java.text.ParseException;
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
import com.nokia.ices.app.dhss.domain.ems.EmsCheckJob;
import com.nokia.ices.app.dhss.service.TaskService;

@Component
public class TaskConsumer {

	private final static Logger logger = LoggerFactory.getLogger(TaskConsumer.class);
	
	@Autowired
	private TaskService taskService;

	@SuppressWarnings({ "unchecked", "unused" })
	@JmsListener(destination = "ems-task-consumer", containerFactory = "jmsContainerFactory")
	public void message(Message message) {
		try {
			logger.info("START");
			TextMessage messages = (TextMessage) message;
			String msgBody = messages.getStringProperty("msgBody");
			logger.info("msgBody  --- ：msgBody:{}", msgBody);
			Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);

			// 获取消息里的任务ID
			Object obj = /* json.get(SMARTID) */messages.getJMSCorrelationID();
			if(obj != null && !"".equals(obj)){
				EmsCheckJob job = taskService.findEmsCheckJobById(Long.valueOf(obj.toString()));
				
				taskService.execEmsJob(job);
				
				try {
					taskService.updateJobExecTime(job);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				logger.error("No parameters");
			}
			
			
		} catch (JMSException e) {
			logger.error("Exception : {}",e.getMessage());
		}

	}
}
