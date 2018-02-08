package com.nokia.ices.app.dhss.domain.smart;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.data.rest.core.config.Projection;

@Entity
public class SmartCheckResult {

	@Id
	@GeneratedValue
	private Long id;

	// 网元ID
	private Long neId;

	// 网元类型ID
	//private Long neTypeId;

	public SmartCheckResult(Long neId, String neTypeName, String neName, Long unitId, String unitTypeName,
			String unitName, Long checkItemId, String checkItemName, Long scheduleId, boolean resultCode,
			String errorMessage, String filePath, Date startTime, boolean logState, String logContents) {
		super();
		this.neId = neId;
		this.neTypeName = neTypeName;
		this.neName = neName;
		this.unitId = unitId;
		this.unitTypeName = unitTypeName;
		this.unitName = unitName;
		this.checkItemId = checkItemId;
		this.checkItemName = checkItemName;
		this.scheduleId = scheduleId;
		this.resultCode = resultCode;
		this.errorMessage = errorMessage;
		this.filePath = filePath;
		this.startTime = startTime;
		this.logState = logState;
		this.logContents = logContents;
	}
	
	private String dhssName;


	public String getDhssName() {
		return dhssName;
	}

	public void setDhssName(String dhssName) {
		this.dhssName = dhssName;
	}

	public SmartCheckResult() {
		super();
	}


	// 网元类型名称
	private String neTypeName;

	// 网元名称
	private String neName;

	// 单元ID
	private Long unitId;
	
    private String unitTypeName;

	public String getUnitTypeName() {
		return unitTypeName;
	}

	public void setUnitTypeName(String unitTypeName) {
		this.unitTypeName = unitTypeName;
	}


	// 单元名称
	private String unitName;

	// 检查项ID
	private Long checkItemId;

	// 检查项名称
	private String checkItemName;

	// 检查任务ID
	private Long scheduleId;

	// 巡检结果 1：失败 0：成功
	private boolean resultCode;

	// 出错信息
	@Column(length=4096)
	private String errorMessage;

	// 报文路径
	private String filePath;

	// 巡检开始时间
	private Date startTime;

	// 报文结果 1：失败 0：成功
	private boolean logState;


	private String logContents;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNeId() {
		return neId;
	}

	public void setNeId(Long neId) {
		this.neId = neId;
	}

	public String getNeTypeName() {
		return neTypeName;
	}

	public void setNeTypeName(String neTypeName) {
		this.neTypeName = neTypeName;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Long getCheckItemId() {
		return checkItemId;
	}

	public void setCheckItemId(Long checkItemId) {
		this.checkItemId = checkItemId;
	}

	public String getCheckItemName() {
		return checkItemName;
	}

	public void setCheckItemName(String checkItemName) {
		this.checkItemName = checkItemName;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public boolean isResultCode() {
		return resultCode;
	}

	public void setResultCode(boolean resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public boolean isLogState() {
		return logState;
	}

	public void setLogState(boolean logState) {
		this.logState = logState;
	}

	public String getLogContents() {
		return logContents;
	}

	public void setLogContents(String logContents) {
		this.logContents = logContents;
	}
	
	public String getHighlight(){
		return this.isResultCode() ? "" : "red";
	}


    @Projection(name = "SmartCheckResultWithAssociation", types = { SmartCheckResult.class })
	public interface SmartCheckResultWithAssociation {

		Long getId();

		int getNeId();

		byte getNeTypeId();

		String getNeTypeName();

		String getNeName();

		int getUnitId();

		byte getUnitTypeId();

		String getUnitTypeName();

		String getUnitName();

		Long getCheckItemId();

		String getcheckItemName();

		Long getScheduleId();

		boolean isResultCode();

		String getErrorMessage();

		SmartCheckScheduleResult getScheduleResult();

		String getFilePath();

		Date getStartTime();

		boolean isLogState();

		String getLogContents();
	}

}
