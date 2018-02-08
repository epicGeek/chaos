package com.nokia.ices.app.dhss.domain.command;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CommandCategory {  
	
	@Id
	@GeneratedValue
	private Long id; 
	
	private String commandCategoryTypeName;
	
	private String commandCategoryTypeCode; 

	public String getCommandCategoryTypeCode() {
		return commandCategoryTypeCode;
	}

	public void setCommandCategoryTypeCode(String commandCategoryTypeCode) {
		this.commandCategoryTypeCode = commandCategoryTypeCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id; 
	}

	public String getCommandCategoryTypeName() {
		return commandCategoryTypeName;
	}

	public void setCommandCategoryTypeName(String commandCategoryTypeName) {
		this.commandCategoryTypeName = commandCategoryTypeName;
	}
}
