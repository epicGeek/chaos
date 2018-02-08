package com.nokia.ices.app.dhss.domain;

public class HomeAlarmItem {
    private String displayName;
    private String notice;
    private String linkAddress;
    private String groupName;
    private Boolean isDisplay;
    
	/**
	 * 
	 */
	public HomeAlarmItem() {
		super();
	}
	public HomeAlarmItem(String displayName, String notice, String linkAddress,String groupName, Boolean isDisplay) {
		super();
		this.displayName = displayName;
		this.notice = notice;
		this.linkAddress = linkAddress;
		this.groupName = groupName;
		this.isDisplay = isDisplay;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	
	public String getLinkAddress() {
		return linkAddress;
	}
	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Boolean getIsDisplay() {
		return isDisplay;
	}
	public void setIsDisplay(Boolean isDisplay) {
		this.isDisplay = isDisplay;
	}
    

}
