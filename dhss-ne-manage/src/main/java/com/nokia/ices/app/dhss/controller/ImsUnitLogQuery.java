package com.nokia.ices.app.dhss.controller;

import java.util.Date;

import com.nokia.ices.app.dhss.domain.ims.ImsUnitLog;

public class ImsUnitLogQuery extends ImsUnitLog {

	private Integer page;

	private Integer size;

	private Date startTime;

	private Date endTime;

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

}
