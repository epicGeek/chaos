package com.nokia.ices.app.dhss.repository.smart.view;

public class SmartCheckResultJobView {
	
	private String checkItemName;
	
	private Integer checkItemId;
	
	private Integer unitCount;
	
	public SmartCheckResultJobView() {
		 
	}
	
	public SmartCheckResultJobView(String checkItemName, Integer checkItemId, Integer unitCount, Integer errorCount) {
		super();
		this.checkItemName = checkItemName;
		this.checkItemId = checkItemId;
		this.unitCount = unitCount;
		this.errorCount = errorCount;
	}

	private Integer errorCount;

	public String getCheckItemName() {
		return checkItemName;
	}

	public void setCheckItemName(String checkItemName) {
		this.checkItemName = checkItemName;
	}

	public Integer getCheckItemId() {
		return checkItemId;
	}

	public void setCheckItemId(Integer checkItemId) {
		this.checkItemId = checkItemId;
	}

	public Integer getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(Integer unitCount) {
		this.unitCount = unitCount;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

}
