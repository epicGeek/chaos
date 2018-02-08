package com.nokia.ices.app.dhss.domain.smart;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SmartCheckScheduleResult {

	@Id
	@GeneratedValue
	private Long id;

	private Long jobId;

	private String jobName;

	private Date startTime;

	private byte execFlag;

	private Integer amountUnit;

	private Integer errorUnit;

	private String jobDesc;

	private Integer amountJob;
	
	public Long getEntityId(){
		return getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public byte getExecFlag() {
		return execFlag;
	}

	public void setExecFlag(byte execFlag) {
		this.execFlag = execFlag;
	}

	public Integer getAmountUnit() {
		return amountUnit;
	}

	public void setAmountUnit(Integer amountUnit) {
		this.amountUnit = amountUnit;
	}

	public Integer getErrorUnit() {
		return errorUnit;
	}

	public void setErrorUnit(Integer errorUnit) {
		this.errorUnit = errorUnit;
	}

	public String getJobDesc() {
		return jobDesc;
	}

	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}

	public Integer getAmountJob() {
		return amountJob;
	}

	public void setAmountJob(Integer amountJob) {
		this.amountJob = amountJob;
	}
	
	public String getHighlight(){
		return this.errorUnit <= 0 ? "" : "red";
	}

}
