package com.nokia.ices.app.dhss.controller;


import com.nokia.ices.app.dhss.domain.alarm.AlarmRule;

public class AlarmRuleQuery extends AlarmRule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer page;

	private Integer size;
	
	private String unitType;

	
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

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	
	
}
