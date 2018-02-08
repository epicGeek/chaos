package com.nokia.ices.app.dhss.controller;

import java.util.Date;

import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;

public class AlarmMonitorCustomQuery extends AlarmMonitor {
	
	
	private Integer page;

	private Integer size;

	private Date filterTimeStart;
	private Date filterTimeEnd;
	private String endTime;
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
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	

}
