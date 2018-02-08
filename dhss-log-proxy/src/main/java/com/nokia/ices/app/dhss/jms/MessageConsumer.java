package com.nokia.ices.app.dhss.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

	private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	
//	@JmsListener(destination = "SYSTEM_ALARM_QUEUE", containerFactory = "jmsContainerFactory")
    public void alarmMessageReceive(Message message) {
		logger.info(message.getClass().getCanonicalName()+":"+message.toString());
	}

//	@JmsListener(destination = "SYSTEM_EVENT_QUEUE", containerFactory = "jmsContainerFactory")
    public void eventMessageReceive(TextMessage message) {
		try {

			logger.info(message.toString());
			logger.info(message.getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
