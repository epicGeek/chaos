package com.nokia.ices.app.dhss.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jcraft.jsch.ChannelShell;
import com.nokia.ices.app.dhss.bean.RequestMessage;
import com.nokia.ices.app.dhss.bean.ResponseMessage;
import com.nokia.ices.app.dhss.bean.SessionHolder;
import com.nokia.ices.app.dhss.bean.UserSession;

@RestController
@RequestMapping("/api/v1")
public class OneClickWebSocketController {

	private static final Logger logger = LoggerFactory.getLogger(OneClickWebSocketController.class);

//	@Autowired
//	private OneClickAccessService oneClickAccessService;

	@MessageMapping("/notice")
	@SendTo("/topic/global-notice")
	public ResponseMessage globalMessage(RequestMessage message) {
		logger.info(message.getMessage());
		return new ResponseMessage(message.getMessage() + " at " + new Date());
	}

	@MessageMapping("/terminal")
	public void onTerminal(RequestMessage message) {
//		String token = message.getToken();
		String sessionId = message.getSessionId();
		String type = message.getType();
		String messageContent = message.getMessage();


		if (type.equalsIgnoreCase("command")) {
			UserSession userSession = SessionHolder.getUserSession(sessionId);
			if (userSession != null) {
				logger.debug(getLastMessage(messageContent));
				userSession.getCommander().print(messageContent);
				userSession.setLastOperateTimeStamp(System.currentTimeMillis());
			}
		}

		if (type.equalsIgnoreCase("resize")) {
			UserSession userSession = SessionHolder.getUserSession(sessionId);
			String[] sizeStr = messageContent.split("x");
			Integer ptyWidth = Integer.parseInt(sizeStr[0]);
			Integer ptyHeight = Integer.parseInt(sizeStr[1]);
			if (userSession != null) {
				ChannelShell channel = (ChannelShell) userSession.getChannel();
				channel.setPtySize(ptyWidth, ptyHeight, (int) Math.floor(ptyWidth * 7.2981),
						(int) Math.floor(ptyHeight * 14.4166));
				userSession.setChannel(channel);
			}
		}
	}

	private String getLastMessage(String messageContent){
		String result = "";
		String[] messageArray = messageContent.split("\n");
		for (int i = messageArray.length - 1; i >= 0; i--) {
			if(StringUtils.isNotBlank(messageArray[i])){
				logger.info(messageArray[i]);
				System.out.println(messageArray[i].getBytes());
				result = messageArray[i];
			}
		}
		return result;
	}
	@SendToUser(value = "/queue/terminal", broadcast = false)
	public ResponseMessage terminal(RequestMessage message) {
		// String sop= SessionHolder.read(message.getSessionId()).toString();
		return new ResponseMessage(new Date().toString() + Math.random());
	}
}