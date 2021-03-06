package com.nokia.ices.app.dhss.domain.alarm;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class UserAlarmMonitor {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String unitName;
	
	private String cnum;
	
	private Date startTime;
	
	private Date endTime;
	
	private Date createTime;
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	private String alarmNum;
	
	private String keyword;
	
	public String getAlarmDesc() {
		return alarmDesc;
	}

	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}

	private String alarmDesc;
	
	private String userName;
	
	@Transient
	private boolean isCollection;
	
	@Transient
	private String notAlarmNo;

	public String getNotAlarmNo() {
		return notAlarmNo;
	}

	public void setNotAlarmNo(String notAlarmNo) {
		this.notAlarmNo = notAlarmNo;
	}

	public boolean getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCnum() {
		return cnum;
	}

	public void setCnum(String cnum) {
		this.cnum = cnum;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getAlarmNum() {
		return alarmNum;
	}

	public void setAlarmNum(String alarmNum) {
		this.alarmNum = alarmNum;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
