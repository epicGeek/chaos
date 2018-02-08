package com.nokia.ices.app.auth.domain;

import java.io.Serializable;

public class SystemMenu implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1910284853530566874L;
	private String linkAddress;
	private String resourceName;
	private String icon;
	public String getLinkAddress() {
		return linkAddress;
	}
	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
