package com.nokia.ices.app.dhss.model;

import java.util.List;

public class CommandModel {
	
	private List<String> commandList;
	
	private String checkName;
	
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getCommandList() {
		return commandList;
	}

	public void setCommandList(List<String> commandList) {
		this.commandList = commandList;
	}

	public String getCheckName() {
		return checkName;
	}

	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}

}
