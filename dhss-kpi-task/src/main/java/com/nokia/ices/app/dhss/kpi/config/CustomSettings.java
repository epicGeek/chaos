package com.nokia.ices.app.dhss.kpi.config;

import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dhss.kpi")
public class CustomSettings implements BeanClassLoaderAware {

	private String kpiUnitType;
	private List<String> kpiUnitTypeList;
	private String insertKpiHistoryTable;
	private String insertKpiCurrentTable;
	private String addCustomAlarm;
	private Integer holdKpiDataMonth = 1;
	private String alarmTemplate;
	private String testConnCronExp;
	private String filterMode = "null"; // null ,fujian-qz,fujian-fz

	
	
	public String getFilterMode() {
		return filterMode;
	}

	public void setFilterMode(String filterMode) {
		this.filterMode = filterMode;
	}

	public String getTestConnCronExp() {
		return testConnCronExp;
	}

	public void setTestConnCronExp(String testConnCronExp) {
		this.testConnCronExp = testConnCronExp;
	}

	public String getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(String alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}
	public String getKpiUnitType() {
		return kpiUnitType;
	}

	public void setKpiUnitType(String kpiUnitType) {
		this.kpiUnitType = kpiUnitType;
	}

	public String getInsertKpiHistoryTable() {
		return insertKpiHistoryTable;
	}

	public void setInsertKpiHistoryTable(String insertKpiHistoryTable) {
		this.insertKpiHistoryTable = insertKpiHistoryTable;
	}

	public String getInsertKpiCurrentTable() {
		return insertKpiCurrentTable;
	}

	public void setInsertKpiCurrentTable(String insertKpiCurrentTable) {
		this.insertKpiCurrentTable = insertKpiCurrentTable;
	}

	public String getAddCustomAlarm() {
		return addCustomAlarm;
	}

	public void setAddCustomAlarm(String addCustomAlarm) {
		this.addCustomAlarm = addCustomAlarm;
	}

	public Integer getHoldKpiDataMonth() {
		return holdKpiDataMonth;
	}

	public void setHoldKpiDataMonth(Integer holdKpiDataMonth) {
		this.holdKpiDataMonth = holdKpiDataMonth;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private ClassLoader classLoader;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;

	}

	public void setKpiUnitTypeList(List<String> kpiUnitTypeList) {
		this.kpiUnitTypeList = kpiUnitTypeList;
	}
	public List<String> getKpiUnitTypeList() {
		return kpiUnitTypeList;
	}

}
