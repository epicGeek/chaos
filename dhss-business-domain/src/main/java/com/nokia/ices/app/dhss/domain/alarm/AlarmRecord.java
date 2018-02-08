package com.nokia.ices.app.dhss.domain.alarm;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class AlarmRecord {

	@Id
	@GeneratedValue
	private Long id;
	private String alarmId;
	private String alarmType;
	private String alarmCell;
	private String alarmLevel;
	private String alarmNum;
	private String alarmStr;
	private String alarmText;
	private String alarmTime;
	private String cancelTime;
	private String neName;
	private String notifyId;
	private String alarmAnnex;
	private String neCode;
	private Date alarmTime2;
	private String userInfo;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmCell() {
		return alarmCell;
	}
	public void setAlarmCell(String alarmCell) {
		this.alarmCell = alarmCell;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getAlarmNum() {
		return alarmNum;
	}
	public void setAlarmNum(String alarmNum) {
		this.alarmNum = alarmNum;
	}
	public String getAlarmStr() {
		return alarmStr;
	}
	public void setAlarmStr(String alarmStr) {
		this.alarmStr = alarmStr;
	}
	public String getAlarmText() {
		return alarmText;
	}
	public void setAlarmText(String alarmText) {
		this.alarmText = alarmText;
	}
	public String getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getCancelTime() {
		return cancelTime;
	}
	public void setCancelTime(String cancelTime) {
		this.cancelTime = cancelTime;
	}
	public String getNeName() {
		return neName;
	}
	public void setNeName(String neName) {
		this.neName = neName;
	}
	public String getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(String notifyId) {
		this.notifyId = notifyId;
	}
	public String getAlarmAnnex() {
		return alarmAnnex;
	}
	public void setAlarmAnnex(String alarmAnnex) {
		this.alarmAnnex = alarmAnnex;
	}
	public String getNeCode() {
		return neCode;
	}
	public void setNeCode(String neCode) {
		this.neCode = neCode;
	}
	public Date getAlarmTime2() {
		return alarmTime2;
	}
	public void setAlarmTime2(Date alarmTime2) {
		this.alarmTime2 = alarmTime2;
	}
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	
	

}
