package com.nokia.boss.bean;

public class MessageItem{
	private String user_name;
	private String task_id;
	private String response_time;
	private String hlrsn ;
	private String msisdn;
	private String imsi;
	private String business_type;
	private Long delay;
	private String soap_log;
	private String response_status;
	private String error_log;
	private String error_code;
	private String error_message;
	private String user_password;

	private String operation_name;

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getBusiness_type() {
		return business_type;
	}

	public void setBusiness_type(String business_type) {
		this.business_type = business_type;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getHlrsn() {
		return hlrsn;
	}

	public void setHlrsn(String hlrsn) {
		this.hlrsn = hlrsn;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}

	public String getOperation_name() {
		return operation_name;
	}

	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}


	public String getResponse_time() {
		return response_time;
	}

	public void setResponse_time(String response_time) {
		this.response_time = response_time;
	}

	public String getSoap_log() {
		return soap_log;
	}

	public void setSoap_log(String soap_log) {
		this.soap_log = soap_log;
	}

	public String getResponse_status() {
		return response_status;
	}

	public void setResponse_status(String response_status) {
		this.response_status = response_status;
	}

	public String getError_log() {
		return error_log;
	}

	public void setError_log(String error_log) {
		this.error_log = error_log;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}


}
