package com.nokia.ices.app.dhss.domain;

import org.springframework.web.multipart.MultipartFile;

public class UploadAnalysisTemplate {
	private String templateType;
	private String comment;
	private MultipartFile multiQueryTemplate;
	private String uploader;
	
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public MultipartFile getMultiQueryTemplate() {
		return multiQueryTemplate;
	}
	public void setMultiQueryTemplate(MultipartFile multiQueryTemplate) {
		this.multiQueryTemplate = multiQueryTemplate;
	}
	
}
