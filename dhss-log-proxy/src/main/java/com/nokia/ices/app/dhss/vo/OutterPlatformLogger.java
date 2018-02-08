package com.nokia.ices.app.dhss.vo;

public class OutterPlatformLogger {
	private String token;
	private String appName;
	private String appModule;
	private String eventContent;
	private String time;
	private String user;
	private String requestURL;
	private String remoteAddress;


	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppModule() {
		return appModule;
	}

	public void setAppModule(String appModule) {
		this.appModule = appModule;
	}

	public String getEventContent() {
		return eventContent;
	}
	

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "OutterPlatformLogger [token=" + token + ", appName=" + appName + ", appModule=" + appModule
				+ ", eventContent=" + eventContent + ", time=" + time + ", user=" + user + ", requestURL=" + requestURL
				+ "]";
	}

}
