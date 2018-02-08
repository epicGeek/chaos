package com.nokia.ices.app.dhss.jms.producer.impl;

import java.util.Map;

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

import com.google.gson.Gson;
import com.nokia.ices.app.dhss.jms.producer.MessageProducer;

@Service("messageProducer")
public class MessageProducerImpl implements MessageProducer {

	private final static Logger logger = LoggerFactory.getLogger(MessageProducerImpl.class);
	// private final static JsonParser parser =
	// JsonParserFactory.getJsonParser();

	@Autowired
	private JmsTemplate jmsTemplate;
	//
	// @Override
	// public void sendMessage(final Map<String, Object> message) {
	// jmsTemplate.setDefaultDestinationName(ProjectProperties.getDesQName());
	// jmsTemplate.send(new MessageCreator() {
	// public Message createMessage(Session session) throws JMSException {
	// TextMessage txtMessage = session.createTextMessage("");
	// txtMessage.setStringProperty("msgBody", new
	// JsonMapper().toJson(message));
	// txtMessage.setIntProperty("msgCode",
	// Integer.parseInt(message.get("msgCode").toString()));
	// txtMessage.setJMSPriority(5);
	//
	// logger.debug("message.getSrcQ() = {},message.getDestQ() = {}",
	// message.get("msgCode"),
	// message.get("destQ"));
	// logger.debug(txtMessage.toString());
	// return txtMessage;
	// }
	// });
	// }

	@Override
	public void sendTextMessage(String desQName, final String message) {
		jmsTemplate.setDefaultDestinationName(desQName);
		jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage txtMessage = session.createTextMessage(message);
				txtMessage.setJMSPriority(5);

				logger.debug(txtMessage.toString());
				return txtMessage;
			}
		});
	}

	@Override
	public void sendTextMessage(String desQName, final Map<String, Object> message) {
		jmsTemplate.setDefaultDestinationName(desQName);
		jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage txtMessage = session.createTextMessage();
				txtMessage.setJMSPriority(5);
				String jsonText = new Gson().toJson(message);
				txtMessage.setText(jsonText);
				return txtMessage;
			}
		});
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
