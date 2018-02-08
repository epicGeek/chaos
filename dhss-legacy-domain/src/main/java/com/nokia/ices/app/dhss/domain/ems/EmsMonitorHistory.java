package com.nokia.ices.app.dhss.domain.ems;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//@Entity
public class EmsMonitorHistory {

	@Id
	@GeneratedValue
	private Long id;

	private Long monitoredUnitId;

	private Long monitoredCommandId;

	private String monitoredUnitName;

	private String monitoredCommandName;

	private Date executeTime;

	private String resultValue;

	private String resultLevel;

	private String resultPath;

	private String groupId;
	public String getGroupId() {
		return groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	private String notificationContent;
	
	public void setData(Date executeTime,Long monitoredCommandId,String monitoredCommandName,Long monitoredUnitId,
			String monitoredUnitName,String notificationContent,String resultLevel,String resultPath,String resultValue,String groupId){
		this.executeTime = executeTime;
		this.monitoredCommandId = monitoredCommandId;
		this.monitoredCommandName = monitoredCommandName;
		this.monitoredUnitId = monitoredUnitId;
		this.monitoredUnitName = monitoredUnitName;
		this.notificationContent = notificationContent;
		this.resultLevel = resultLevel;
		this.resultPath = resultPath;
		this.resultValue = resultValue;
		this.groupId = groupId;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMonitoredUnitId() {
		return monitoredUnitId;
	}

	public void setMonitoredUnitId(Long monitoredUnitId) {
		this.monitoredUnitId = monitoredUnitId;
	}

	public String getMonitoredUnitName() {
		return monitoredUnitName;
	}

	public Long getMonitoredCommandId() {
		return monitoredCommandId;
	}

	public void setMonitoredCommandId(Long monitoredCommandId) {
		this.monitoredCommandId = monitoredCommandId;
	}

	public String getMonitoredCommandName() {
		return monitoredCommandName;
	}

	public void setMonitoredCommandName(String monitoredCommandName) {
		this.monitoredCommandName = monitoredCommandName;
	}

	public void setMonitoredUnitName(String monitoredUnitName) {
		this.monitoredUnitName = monitoredUnitName;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public String getResultLevel() {
		return resultLevel;
	}

	public void setResultLevel(String resultLevel) {
		this.resultLevel = resultLevel;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public String getNotificationContent() {
		return notificationContent;
	}

	public void setNotificationContent(String notificationContent) {
		this.notificationContent = notificationContent;
	}

}
