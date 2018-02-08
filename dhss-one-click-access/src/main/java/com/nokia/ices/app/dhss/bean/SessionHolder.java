package com.nokia.ices.app.dhss.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionHolder {
	private SessionHolder() {

	}

	private static final Map<String, UserSession> sessionMap = new ConcurrentHashMap<String, UserSession>();

	private static final Map<String, StringBuilder> sessionOutputMap = new ConcurrentHashMap<String, StringBuilder>();

	static {
		
    }
//
//	public static void write(String sessionId, String cmd, String type) {
//		try {
//			if(type.equals("keyCode"))
//			  sessionMap.get(sessionId).getCommander().write(cmd.getBytes());
//			else
//			  sessionMap.get(sessionId).getCommander().print(cmd.getBytes());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void destroy(String sessionId) {
		// TODO
		sessionMap.get(sessionId).destroy();
		sessionMap.remove(sessionId);
		sessionOutputMap.remove(sessionId);
	}

	public static UserSession getUserSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public static void addSession(String sessionId, UserSession session) {
		sessionMap.put(sessionId, session);
		sessionOutputMap.put(sessionId, new StringBuilder());
	}

	public static StringBuilder getOutput(String sessionId) {
		if (sessionOutputMap.size() != 0 && sessionOutputMap.containsKey(sessionId)) {

		} else {
			sessionOutputMap.put(sessionId, new StringBuilder());
		}
		return sessionOutputMap.get(sessionId);
	}

	public static void removeOutput(String sessionId) {
		sessionOutputMap.remove(sessionId);
	}

	public static Map<String, StringBuilder> getAllOutput() {
		return sessionOutputMap;
	}

	public static Map<String, UserSession> getAllSession() {
		return sessionMap;
	}

}
