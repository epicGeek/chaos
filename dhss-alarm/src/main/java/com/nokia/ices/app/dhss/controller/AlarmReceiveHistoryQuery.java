package com.nokia.ices.app.dhss.controller;

import java.util.Date;

import com.nokia.ices.app.dhss.domain.alarm.AlarmReceiveHistory;

public class AlarmReceiveHistoryQuery extends AlarmReceiveHistory {

	private Integer page;

	private Integer size;

	private Date filterTimeStart;
	private Date filterTimeEnd;
	private String alarmUnit;
	private String endTime;
	
	
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAlarmUnit() {
		return alarmUnit;
	}

	public void setAlarmUnit(String alarmUnit) {
		this.alarmUnit = alarmUnit;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Date getFilterTimeStart() {
		return filterTimeStart;
	}

	public void setFilterTimeStart(Date filterTimeStart) {
		this.filterTimeStart = filterTimeStart;
	}

	public Date getFilterTimeEnd() {
		return filterTimeEnd;
	}

	public void setFilterTimeEnd(Date filterTimeEnd) {
		this.filterTimeEnd = filterTimeEnd;
	}

}
