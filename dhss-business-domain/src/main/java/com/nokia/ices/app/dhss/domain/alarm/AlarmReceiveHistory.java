package com.nokia.ices.app.dhss.domain.alarm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class AlarmReceiveHistory {

	@Id
	@GeneratedValue
	private Long id;
	@Column(length=50)
	private String notifyId;
	@Column(length=4)
	private String alarmLevel;
	@Column(length=10)
	private String alarmNo;
	@Column(length=100)
	private String alarmCell;
	private String startTime;
	private String cancelTime;
	@Column(length=50)
	private String neName;
	@Column(length=100)
	private String neCode;
	private Date receiveStartTime;
	private Date receiveCancelTime;
	

	@Column(length=1024)
	private String userInfo;
	@Column(length=1024)
	private String supplInfo;
	@Column(length=1024)
	private String diagInfo;
	@Column(length=1024)
	private String objInfo;
	@Column(length=64)
	private String alarmType;
	
	private String dhssName;
	
	public String getDhssName() {
		return dhssName;
	}
	public void setDhssName(String dhssName) {
		this.dhssName = dhssName;
	}
	
	private String alarmDesc;
	
	public String getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public String getSupplInfo() {
		return supplInfo;
	}
	public void setSupplInfo(String supplInfo) {
		this.supplInfo = supplInfo;
	}
	public String getDiagInfo() {
		return diagInfo;
	}
	public void setDiagInfo(String diagInfo) {
		this.diagInfo = diagInfo;
	}
	public String getObjInfo() {
		return objInfo;
	}
	public void setObjInfo(String objInfo) {
		this.objInfo = objInfo;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public String getAlarmDesc() {
		return alarmDesc;
	}
	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}
	@Column(length=4096)
	private String alarmString;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNotifyId() {
		return notifyId;
	}
	public void setNotifyId(String notifyId) {
		this.notifyId = notifyId;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public String getAlarmNo() {
		return alarmNo;
	}
	public void setAlarmNo(String alarmNo) {
		this.alarmNo = alarmNo;
	}
	public String getAlarmCell() {
		return alarmCell;
	}
	public void setAlarmCell(String alarmCell) {
		this.alarmCell = alarmCell;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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
	public String getNeCode() {
		return neCode;
	}
	public void setNeCode(String neCode) {
		this.neCode = neCode;
	}
	public Date getReceiveStartTime() {
		return receiveStartTime;
	}
	public void setReceiveStartTime(Date receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}
	public Date getReceiveCancelTime() {
		return receiveCancelTime;
	}
	public void setReceiveCancelTime(Date receiveCancelTime) {
		this.receiveCancelTime = receiveCancelTime;
	}
	public String getAlarmString() {
		return alarmString;
	}
	public void setAlarmString(String alarmString) {
		this.alarmString = alarmString;
	}
	private String unitType;

	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	
	
}
