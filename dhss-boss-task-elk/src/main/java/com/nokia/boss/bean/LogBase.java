package com.nokia.boss.bean;

import java.util.List;

public class LogBase {
	private String soap_name;
	private List<LogMark> LogMark;
	private List<IgnoreFile> ignoreFile;

	public String getSoap_name() {
		return soap_name;
	}

	public void setSoap_name(String soap_name) {
		this.soap_name = soap_name;
	}

	public List<LogMark> getLogMark() {
		return LogMark;
	}

	public void setLogMark(List<LogMark> logMark) {
		LogMark = logMark;
	}

	public List<IgnoreFile> getIgnoreFile() {
		return ignoreFile;
	}

	public void setIgnoreFile(List<IgnoreFile> ignoreFile) {
		this.ignoreFile = ignoreFile;
	}

}
