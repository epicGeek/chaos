package com.nokia.ices.app.dhss.nelog.jms;

import java.io.IOException;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.ices.app.dhss.nelog.log.LogConfig;

@Component
public class NeLogMessageListener {

	private final static Logger logger = LoggerFactory.getLogger(NeLogMessageListener.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	@SuppressWarnings("unchecked")
	@JmsListener(destination = "POSEIDON_DEV", containerFactory = "jmsContainerFactory")
	public void reseiveMessage(Message message) {
		
		String msgBody = null;
		Integer msgCode = new Integer(0);

		try {

			msgBody = message.getStringProperty("msgBody");
			msgCode = message.getIntProperty("msgCode");
			
			logger.debug(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|"+"消息返回信息：{},消息返回Code：{}", msgBody,msgCode);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> json = mapper.readValue(msgBody, Map.class);
			String resultCode = String.valueOf(json.get("flag"));
			String msg = String.valueOf(json.get("msg"));
			String unitName = String.valueOf(json.get("ne"));
			
			if ("71000".equalsIgnoreCase(resultCode) && StringUtils.isNotEmpty(msg)) {

				String flag = msg.split(",")[0].split(":")[1];
				int startLeng = msg.indexOf(flag) + flag.length() + 1;
				String path = msg.substring(startLeng, msg.indexOf(";"));
				/**
				 * update path
				 */
				jdbcTemplate.update("UPDATE equipment_ne_operation_log SET path=? WHERE unit_name=?",
						new Object[] { path, unitName });

			} else {
				logger.debug(LogConfig.PROJECT_NAME+"|"+LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME+"|result msg fail....");
			}

		} catch (JMSException | IOException e) {
			logger.error(LogConfig.PROJECT_NAME+"|"
		    +LogConfig.MODULE_NAME+"|"+LogConfig.TASK_NAME
		    +"|result reseiveMessage  failure,reason:JMSException or IOException:"+e.toString());
			
		}

	}

}
