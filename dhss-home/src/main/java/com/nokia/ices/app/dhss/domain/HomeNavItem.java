package com.nokia.ices.app.dhss.domain;

import java.io.Serializable;

public class HomeNavItem implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8273194972983938807L;
	private String menuName;
    private String displayName;
    private String notice;
    private String icon;
    private String linkAddress;
    private String groupDisplayName;
    private String groupName;
    private Boolean isDisplay;
	/**
	 * 
	 */
	public HomeNavItem() {
		super();
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
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
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLinkAddress() {
		return linkAddress;
	}
	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
	}
	public String getGroupDisplayName() {
		return groupDisplayName;
	}
	public void setGroupDisplayName(String groupDisplayName) {
		this.groupDisplayName = groupDisplayName;
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
