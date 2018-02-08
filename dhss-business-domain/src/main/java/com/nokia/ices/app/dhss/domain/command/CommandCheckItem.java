package com.nokia.ices.app.dhss.domain.command;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;


@Entity
public class CommandCheckItem {

	@Id
    @GeneratedValue
    private Long id;

//	@JsonIgnore
//	@ManyToMany()
//    private Set<CommandGroup> commandGroup;
    /**

	 * 指令类型
	 */
	private String category;

	@ManyToMany(mappedBy="checkItem")
	@JsonIgnore
	private Set<SmartCheckJob> smartCheckJob;

	public Set<SmartCheckJob> getSmartCheckJob() {
		return smartCheckJob;
	}

	public void setSmartCheckJob(Set<SmartCheckJob> smartCheckJob) {
		this.smartCheckJob = smartCheckJob;
	}

	private String cmdType;


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
	 * ems 通知
	 */
	private String emsType;
	
	private String applyUnit;
	
	private String scriptType;



	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getApplyUnit() {
		return applyUnit;
	}

	public void setApplyUnit(String applyUnit) {
		this.applyUnit = applyUnit;
	}

	public String getEmsType() {
		return emsType;
	}

	public void setEmsType(String emsType) {
		this.emsType = emsType;
	}

	public String getDefaultParamValues() {
		return defaultParamValues;
	}

	public void setDefaultParamValues(String defaultParamValues) {
		this.defaultParamValues = defaultParamValues;
	}

	/**
	 * 执行指令的用户
	 */
	private String account;

	/**
	 * 备注
	 */
	private String remarks;

	/**
	 * 指令解析脚本
	 *
	 * @author yudq
	 */
	@Column(length=4096)
	private String script;


	/**
	 * 解析脚本参数,比如报文
	 */
	@Transient
	private List<String> scriptParams;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Set<CommandGroup> getCommandGroup() {
//		return commandGroup;
//	}
//
//	public void setCommandGroup(Set<CommandGroup> commandGroup) {
//		this.commandGroup = commandGroup;
//	}

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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<String> getScriptParams() {
		return scriptParams;
	}

	public void setScriptParams(List<String> scriptParams) {
		this.scriptParams = scriptParams;
	}
	public String getCmdType() {
		return cmdType;
	}

	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}
	public Long getItemId(){
	    return this.id;
	}
}
