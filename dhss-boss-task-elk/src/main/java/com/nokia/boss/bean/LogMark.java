package com.nokia.boss.bean;

 
public class LogMark {
 
	private String file_name;
	
	private int start_line;
	private int type;//0:soap 1:err
 

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}


	public int getStart_line() {
		return start_line;
	}

	public void setStart_line(int start_line) {
		this.start_line = start_line;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
