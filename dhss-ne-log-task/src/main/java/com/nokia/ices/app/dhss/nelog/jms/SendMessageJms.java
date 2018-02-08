package com.nokia.ices.app.dhss.nelog.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.ices.app.dhss.nelog.log.LogConfig;


@Service
public class SendMessageJms {

	private final static Logger logger = LoggerFactory.getLogger(SendMessageJms.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMessage(final MessageModel message) {

		jmsTemplate.setDefaultDestinationName(message.getDestQ());
		jmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage txtMessage = session.createTextMessage("");
				ObjectMapper mapper = new ObjectMapper();
				String JsonMgs = null;
				try {
					JsonMgs = mapper.writeValueAsString(message);
					
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				txtMessage.setStringProperty("msgBody", JsonMgs);
				txtMessage.setIntProperty("msgCode", message.getMsgCode());
				txtMessage.setJMSPriority(5);
				/*logger.debug(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|"+"message.getSrcQ() = {},message.getDestQ() = {}", message.getMsgCode(),
						message.getDestQ());*/
				
				logger.debug(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|"+txtMessage.toString());
				return txtMessage;
			}
		});

	}

}
