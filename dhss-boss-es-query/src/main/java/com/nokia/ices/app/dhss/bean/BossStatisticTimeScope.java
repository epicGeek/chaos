package com.nokia.ices.app.dhss.bean;

import org.joda.time.DateTime;

public class BossStatisticTimeScope {
	private String timeScopeName;
	private DateTime startDate;
	private DateTime endDate;
	public String getTimeScopeName() {
		return timeScopeName;
	}
	public void setTimeScopeName(String timeScopeName) {
		this.timeScopeName = timeScopeName;
	}
	public DateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}
	public DateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
	@Override
	public String toString(){
		return "X tag:"+timeScopeName+","+startDate.toString("yyyy-MM-dd HH:mm:ss")+"->"+endDate.toString("yyyy-MM-dd HH:mm:ss");
		
	}
}
