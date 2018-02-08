package com.nokia.ices.app.dhss.domain.kpi;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class KpiConfig {
	@Id
	@GeneratedValue
	private Long id;
	
	/**
	 * 执行数据库操作的SQL语句
	 */
	@Column(length=4096)
	private String kpiQueryScript;

    /**
     * kpi名称 如：鉴权成功率
     */
	private String kpiName;
	

    /**
     * kpi针对的网元类型 如：HSSFE NTHLRFE
     */
	private String kpiNeType;
	

	/**
	 * KPI代码 ：kpi001,kpi002....
	 */
	private String kpiCode;

    public String getKpiNeType() {
		return kpiNeType;
	}

	public void setKpiNeType(String kpiNeType) {
		this.kpiNeType = kpiNeType;
	}

	/**
     * 输出字段success_rate,fail_rate,total_count,success_count,fail_count
     */
    private String outPutField;
    /***************门限相关的字段**************/
    /**
     * 请求次数的样本基数。对应quota_monitor里面的kpi_request_count.以样本基数判断忙时闲时。
     * 样本基数=0，该KPI无样本基数概念或者该单元设备上不存在这类KPI。
     */
    private Integer requestSample;
    
    private String threshold;
    /**
     * 取消门限。KPI恢复正常所要达到的门限值。
     */
    private String thresholdCancel;

    private String comparedMethod;
    
    private String kpiCategory;
    
    private String kpiUnit;
    
    private String alarmLevel;

    
    public String getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public String getKpiUnit() {
		return kpiUnit;
	}

	public void setKpiUnit(String kpiUnit) {
		this.kpiUnit = kpiUnit;
	}
	private Boolean kpiEnabled;

	public Boolean getKpiEnabled() {
		return kpiEnabled;
	}

	public void setKpiEnabled(Boolean kpiEnabled) {
		this.kpiEnabled = kpiEnabled;
	}
	
	public String getKpiCategory() {
		return kpiCategory;
	}

	public void setKpiCategory(String kpiCategory) {
		this.kpiCategory = kpiCategory;
	}

	/**
     * 监控时间段：开始-结束。其他时间段不触发告警
     * 1-4,6,8-9
     */
    private String monitorTimeString;

	public String getMonitorTimeString() {
		return monitorTimeString;
	}

	public void setMonitorTimeString(String monitorTimeString) {
		this.monitorTimeString = monitorTimeString;
	}

	public String getThresholdCancel() {
		return thresholdCancel;
	}

	public void setThresholdCancel(String thresholdCancel) {
		this.thresholdCancel = thresholdCancel;
	}

	public Integer getRequestSample() {
		return requestSample;
	}

	public void setRequestSample(Integer requestSample) {
		this.requestSample = requestSample;
	}

	public String getOutPutField() {
        return outPutField;
    }

    public void setOutPutField(String outPutField) {
        this.outPutField = outPutField;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKpiQueryScript() {
		return kpiQueryScript;
	}

	public void setKpiQueryScript(String kpiQueryScript) {
		this.kpiQueryScript = kpiQueryScript;
	}

	public String getKpiName() {
		return kpiName;
	}

	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}

	public String getKpiCode() {
		return kpiCode;
	}

	public void setKpiCode(String kpiCode) {
		this.kpiCode = kpiCode;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

    public String getComparedMethod() {
		return comparedMethod;
	}

	public void setComparedMethod(String comparedMethod) {
		this.comparedMethod = comparedMethod;
	}
}
