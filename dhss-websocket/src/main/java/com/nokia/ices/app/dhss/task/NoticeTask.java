package com.nokia.ices.app.dhss.task;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.bean.ResponseMessage;

@Component
@EnableScheduling
public class NoticeTask {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Scheduled(fixedDelay=500)
	public void taskRun() {
		ResponseMessage responseMessage = new ResponseMessage(new Date().toString() + Math.floor(Math.random()*1000));
		simpMessagingTemplate.convertAndSend("/topic/global-notice", responseMessage);
	}
}
