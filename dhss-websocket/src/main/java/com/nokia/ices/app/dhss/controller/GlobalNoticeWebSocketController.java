package com.nokia.ices.app.dhss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.bean.RequestMessage;
import com.nokia.ices.app.dhss.bean.ResponseMessage;

@RestController
@RequestMapping("/api/v1")
public class GlobalNoticeWebSocketController {

	private static final Logger logger = LoggerFactory.getLogger(GlobalNoticeWebSocketController.class);
	
	@MessageMapping("/publish")
	@SendTo("/topic/global-notice")
	 public ResponseMessage publishGlobalNotice(RequestMessage message) {
		ResponseMessage responseMessage = new ResponseMessage(message.getSessionId());
		logger.debug(responseMessage.toString());
		return responseMessage;
	}
	

}