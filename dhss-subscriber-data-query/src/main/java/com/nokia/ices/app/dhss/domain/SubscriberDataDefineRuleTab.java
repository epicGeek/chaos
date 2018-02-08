package com.nokia.ices.app.dhss.domain;

import java.io.Serializable;
import java.util.List;

public class SubscriberDataDefineRuleTab implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7450664102317124629L;
	private String tabName;
	private String col;

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	private List<SubscriberDataDefineRuleField> children;

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public List<SubscriberDataDefineRuleField> getChildren() {
		return children;
	}

	public void setChildren(List<SubscriberDataDefineRuleField> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "SubscriberDataDefineRuleTab [tabName=" + tabName + "]";
	}

}
