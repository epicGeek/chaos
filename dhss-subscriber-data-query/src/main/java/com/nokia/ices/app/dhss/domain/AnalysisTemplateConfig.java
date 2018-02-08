package com.nokia.ices.app.dhss.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class AnalysisTemplateConfig {
    @Id
    @GeneratedValue
    private Long id;
    private String templateType;
    private String uploader;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date uploadTime;
    @Lob
    private String comment;
    private String templateFilePath;
    private String isInUse;
    private String isOriginFile;
    
	public String getIsOriginFile() {
		return isOriginFile;
	}
	public void setIsOriginFile(String isOriginFile) {
		this.isOriginFile = isOriginFile;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTemplateFilePath() {
		return templateFilePath;
	}
	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}
	public String getIsInUse() {
		return isInUse;
	}
	public void setIsInUse(String isInUse) {
		this.isInUse = isInUse;
	}
    
}
