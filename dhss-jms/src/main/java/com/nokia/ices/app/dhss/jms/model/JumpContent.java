package com.nokia.ices.app.dhss.jms.model;

public class JumpContent
{
	private String jumpIp;
	
	private String jumpPort;
	
	private String jumpUserName;
	
	private String jumpPassword;
	
	private String jumpProtocol;

	public JumpContent(String jumpIp, String jumpPort, String jumpUserName, String jumpPassword, String jumpProtocol) {
		super();
		this.jumpIp = jumpIp;
		this.jumpPort = jumpPort;
		this.jumpUserName = jumpUserName;
		this.jumpPassword = jumpPassword;
		this.jumpProtocol = jumpProtocol;
	}

	public String getJumpIp() {
		return jumpIp;
	}

	public void setJumpIp(String jumpIp) {
		this.jumpIp = jumpIp;
	}

	public String getJumpPort() {
		return jumpPort;
	}

	public void setJumpPort(String jumpPort) {
		this.jumpPort = jumpPort;
	}

	public String getJumpUserName() {
		return jumpUserName;
	}

	public void setJumpUserName(String jumpUserName) {
		this.jumpUserName = jumpUserName;
	}

	public String getJumpPassword() {
		return jumpPassword;
	}

	public void setJumpPassword(String jumpPassword) {
		this.jumpPassword = jumpPassword;
	}

	public String getJumpProtocol() {
		return jumpProtocol;
	}

	public void setJumpProtocol(String jumpProtocol) {
		this.jumpProtocol = jumpProtocol;
	}
	
	
}
