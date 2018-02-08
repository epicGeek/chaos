package com.nokia.ices.app.dhss.jms.producer;


import java.util.Map;


public interface MessageProducer {
	
    public void sendTextMessage(String desQName, String message);

    public void sendTextMessage(String desQName, Map<String, Object> message);
    
}
