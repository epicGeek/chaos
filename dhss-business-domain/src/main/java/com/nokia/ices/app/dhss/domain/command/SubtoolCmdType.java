package com.nokia.ices.app.dhss.domain.command;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubtoolCmdType {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String subtoolCmdTypeName;
	
	private String subtoolCmdTypeCode;

	public String getSubtoolCmdTypeCode() {
		return subtoolCmdTypeCode;
	}

	public void setSubtoolCmdTypeCode(String subtoolCmdTypeCode) {
		this.subtoolCmdTypeCode = subtoolCmdTypeCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubtoolCmdTypeName() {
		return subtoolCmdTypeName;
	}

	public void setSubtoolCmdTypeName(String subtoolCmdTypeName) {
		this.subtoolCmdTypeName = subtoolCmdTypeName;
	}

}
