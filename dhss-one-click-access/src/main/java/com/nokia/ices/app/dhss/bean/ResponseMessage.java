package com.nokia.ices.app.dhss.bean;

public class ResponseMessage {

	private String sessionId;

	private String token;

	private String message;

	private String type;// resize,conn,cmd,keyCode

	public ResponseMessage(String msg) {
		this.message = msg;
	}

	public ResponseMessage(String type, String msg) {
		this.type = type;
		this.message = msg;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "RequestMessage [sessionId=" + sessionId + ", token=" + token + ", message=" + message + ", type=" + type
				+ "]";
	}

}