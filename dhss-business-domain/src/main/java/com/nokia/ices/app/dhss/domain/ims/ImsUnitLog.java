package com.nokia.ices.app.dhss.domain.ims;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ImsUnitLog {

	@Id
	@GeneratedValue
	private Long id;
	
	private String fileName;
	
	private String unitName;
	
	private String fileLog;
	
	private Date createDate;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getFileLog() {
		return fileLog;
	}

	public void setFileLog(String fileLog) {
		this.fileLog = fileLog;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
