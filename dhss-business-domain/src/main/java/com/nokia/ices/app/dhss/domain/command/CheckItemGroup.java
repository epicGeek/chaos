package com.nokia.ices.app.dhss.domain.command;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CheckItemGroup {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Long checkItemId;
	
	private Long groupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCheckItemId() {
		return checkItemId;
	}

	public void setCheckItemId(Long checkItemId) {
		this.checkItemId = checkItemId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	

}
