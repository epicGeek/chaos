package com.nokia.ices.app.dhss.bean;

import java.util.Map;

public class BatchTemplate {
	private String qName ;
	private String zhName;
	private String xPath ;
	private Boolean hasValueDesc = false;
	private Map<String,String> valueDecsList;
	public String getqName() {
		return qName;
	}
	public void setqName(String qName) {
		this.qName = qName;
	}
	public String getZhName() {
		return zhName;
	}
	public void setZhName(String zhName) {
		this.zhName = zhName;
	}
	public String getxPath() {
		return xPath;
	}
	public void setxPath(String xPath) {
		this.xPath = xPath;
	}
	public Boolean getHasValueDesc() {
		return hasValueDesc;
	}
	public void setHasValueDesc(Boolean hasValueDesc) {
		this.hasValueDesc = hasValueDesc;
	}
	public Map<String,String> getValueDecsList() {
		return valueDecsList;
	}
	public void setValueDecsList(Map<String,String> valueDecsList) {
		this.valueDecsList = valueDecsList;
	}
	
}
