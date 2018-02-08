package com.nokia.ices.app.dhss.domain.subscriber;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubscriberCommand {

	@Id
	@GeneratedValue
	private Long id;
	/**
	 * 
	 * 指令类型
	 */
	private String category;

	/**
	 * 指令名称
	 */
	private String name;

	/**
	 * 指令内容 用"$"+从1开始的数字代表参数
	 */
	@Column(length = 1024)
	private String command;

	/**
	 * 参数名称（如果有参数，按数字顺序标明参数名称）
	 */
	private String params;

	private String defaultParamValues;
	
	/**
	 * 备注
	 */
	private String remarks;

	public String getDefaultParamValues() {
		return defaultParamValues;
	}

	public void setDefaultParamValues(String defaultParamValues) {
		this.defaultParamValues = defaultParamValues;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Long getItemId() {
		return this.id;
	}

}
