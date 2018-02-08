package com.nokia.ices.app.dhss.consumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;

@Component
public class LuaTestConsumer {

	private final static Logger logger = LoggerFactory.getLogger(LuaTestConsumer.class);

	public static final String LUA_TEST_SRCQ = "Q_lua_test";

	public static Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();

	@SuppressWarnings({ "unchecked" })
	@JmsListener(destination = LUA_TEST_SRCQ, containerFactory = "jmsContainerFactory")
	public void message(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			String messageDetail = textMessage.getText();
			String msgBody = textMessage.getStringProperty("msgBody");
			logger.info("msgBody: " + msgBody);
			String sessionId = null;
			if (null != msgBody) {
				Map<String, String> json = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
				sessionId = String.valueOf(json.get("session"));
				String resultCode = String.valueOf(json.get("result_code"));
				logger.info("sessionId: " + sessionId);
				logger.info("resultCode: " + resultCode);
				String result = null;
				try {
					result = uncompress(messageDetail);
					logger.info("result:" + result);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Map<String, String> sessionAndDetailMap = new HashMap<String, String>();
				sessionAndDetailMap.put("resultCode", resultCode);
				sessionAndDetailMap.put("message", result);
				logger.info("sessionAndDetailMap: " + sessionAndDetailMap.toString());
				resultMap.put(sessionId, sessionAndDetailMap);
				logger.info("resultMap: " + resultMap.toString());
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	public static String uncompress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			GZIPInputStream gunzip = new GZIPInputStream(in);

			byte[] buffer = new byte[1024];
			int n;
			while ((n = gunzip.read(buffer)) != 0) {
				if (n >= 0) {
					out.write(buffer, 0, n);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			throw new Exception(e.getMessage());
		}
		return out.toString();
	}

}
