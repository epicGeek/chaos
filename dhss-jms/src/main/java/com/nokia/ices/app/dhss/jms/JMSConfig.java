package com.nokia.ices.app.dhss.jms;

import javax.jms.ConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class JMSConfig {

	private static final Logger logger = LoggerFactory.getLogger(JMSConfig.class);

	@Bean
	JmsListenerContainerFactory<?> jmsContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		return factory;
	}

	@Bean
	JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
		logger.info(connectionFactory.getClass().getCanonicalName());
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setExplicitQosEnabled(true);
		jmsTemplate.setPriority(9);
		return jmsTemplate;
	}
}
