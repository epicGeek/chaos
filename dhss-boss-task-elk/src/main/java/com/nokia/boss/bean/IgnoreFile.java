package com.nokia.boss.bean;

import org.joda.time.DateTime;

public class IgnoreFile {
	private String file_name;
	private DateTime analysed_time;
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public DateTime getAnalysed_time() {
		return analysed_time;
	}
	public void setAnalysed_time(DateTime analysed_time) {
		this.analysed_time = analysed_time;
	}

}
