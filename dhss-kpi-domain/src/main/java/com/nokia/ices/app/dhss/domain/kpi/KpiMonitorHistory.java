package com.nokia.ices.app.dhss.domain.kpi;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class KpiMonitorHistory {
	
	@Id
	@GeneratedValue
	private Long id;

	private String dhssName;
	private String neSite;
	private String neName;
	private String neType;
	private String neId;
	
	private String unitName;
	private String unitId;
	private String unitType;
	
	private String unitNext;
	private String unitNextId;
	
	private String coGid;
	
	private String kpiUnit;


	private String kpiCategory;
	private String kpiName;
	private String kpiCode;
	


	private Double kpiOutputValue;
	private Double kpiValue;
    private Integer kpiTotal;
    
	private String kpiCompareMethod;
	private Date periodStartTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDhssName() {
		return dhssName;
	}

	public void setDhssName(String dhssName) {
		this.dhssName = dhssName;
	}

	public String getNeSite() {
		return neSite;
	}

	public void setNeSite(String neSite) {
		this.neSite = neSite;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getNeType() {
		return neType;
	}

	public void setNeType(String neType) {
		this.neType = neType;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getUnitNext() {
		return unitNext;
	}

	public void setUnitNext(String unitNext) {
		this.unitNext = unitNext;
	}

	public String getUnitNextId() {
		return unitNextId;
	}

	public void setUnitNextId(String unitNextId) {
		this.unitNextId = unitNextId;
	}

	public String getCoGid() {
		return coGid;
	}

	public void setCoGid(String coGid) {
		this.coGid = coGid;
	}

	public String getKpiUnit() {
		return kpiUnit;
	}

	public void setKpiUnit(String kpiUnit) {
		this.kpiUnit = kpiUnit;
	}

	public String getKpiCategory() {
		return kpiCategory;
	}

	public void setKpiCategory(String kpiCategory) {
		this.kpiCategory = kpiCategory;
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

	public Double getKpiOutputValue() {
		return kpiOutputValue;
	}

	public void setKpiOutputValue(Double kpiOutputValue) {
		this.kpiOutputValue = kpiOutputValue;
	}

	public Double getKpiValue() {
		return kpiValue;
	}

	public void setKpiValue(Double kpiValue) {
		this.kpiValue = kpiValue;
	}

	public Integer getKpiTotal() {
		return kpiTotal;
	}

	public void setKpiTotal(Integer kpiTotal) {
		this.kpiTotal = kpiTotal;
	}

	public String getKpiCompareMethod() {
		return kpiCompareMethod;
	}

	public void setKpiCompareMethod(String kpiCompareMethod) {
		this.kpiCompareMethod = kpiCompareMethod;
	}

	public Date getPeriodStartTime() {
		return periodStartTime;
	}

	public void setPeriodStartTime(Date periodStartTime) {
		this.periodStartTime = periodStartTime;
	}

	@Override
	public String toString() {
		return "KpiMonitorHistory [id=" + id + ", dhssName=" + dhssName + ", neSite=" + neSite + ", neName=" + neName
				+ ", neType=" + neType + ", neId=" + neId + ", unitName=" + unitName + ", unitId=" + unitId
				+ ", unitType=" + unitType + ", unitNext=" + unitNext + ", unitNextId=" + unitNextId + ", coGid="
				+ coGid + ", kpiUnit=" + kpiUnit + ", kpiCategory=" + kpiCategory + ", kpiName=" + kpiName
				+ ", kpiCode=" + kpiCode + ", kpiOutputValue=" + kpiOutputValue + ", kpiValue=" + kpiValue
				+ ", kpiTotal=" + kpiTotal + ", kpiCompareMethod=" + kpiCompareMethod + ", periodStartTime="
				+ periodStartTime + "]";
	}
}
