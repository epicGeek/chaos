package com.nokia.pgw.settings;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dhss.pgw-log")
public class CustomSetting implements BeanClassLoaderAware {

	private String pgwBasicInfo;
	private String rsyncCmdPattern;
	private boolean isDryRunMode;
	private boolean isAccurateSyncMode;
	private String accurateMatchRuleFileDir;
	private String pgwLogDeployDir;
	private String remotePgwLogBaseDir;
	private Integer saveDays;
	private String mainProgramCron;
	private boolean isHalfAutoMode;
	private String mmc;
	private String countryCode;
	private boolean isManualMode = false;
	private String manualDir;
	
	
	
	public boolean isManualMode() {
		return isManualMode;
	}

	public void setManualMode(boolean isManualMode) {
		this.isManualMode = isManualMode;
	}

	public String getManualDir() {
		return manualDir;
	}

	public void setManualDir(String manualDir) {
		this.manualDir = manualDir;
	}

	public String getMmc() {
		return mmc;
	}

	public void setMmc(String mmc) {
		this.mmc = mmc;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isHalfAutoMode() {
		return isHalfAutoMode;
	}

	public void setHalfAutoMode(boolean isHalfAutoMode) {
		this.isHalfAutoMode = isHalfAutoMode;
	}

	public String getMainProgramCron() {
		return mainProgramCron;
	}

	public void setMainProgramCron(String mainProgramCron) {
		this.mainProgramCron = mainProgramCron;
	}

	public Integer getSaveDays() {
		return saveDays;
	}

	public void setSaveDays(Integer saveDays) {
		this.saveDays = saveDays;
	}

	public String getPgwBasicInfo() {
		return pgwBasicInfo;
	}

	public void setPgwBasicInfo(String pgwBasicInfo) {
		this.pgwBasicInfo = pgwBasicInfo;
	}

	public String getRsyncCmdPattern() {
		return rsyncCmdPattern;
	}

	public void setRsyncCmdPattern(String rsyncCmdPattern) {
		this.rsyncCmdPattern = rsyncCmdPattern;
	}

	public boolean isDryRunMode() {
		return isDryRunMode;
	}

	public void setDryRunMode(boolean isDryRunMode) {
		this.isDryRunMode = isDryRunMode;
	}

	public boolean isAccurateSyncMode() {
		return isAccurateSyncMode;
	}

	public void setAccurateSyncMode(boolean isAccurateSyncMode) {
		this.isAccurateSyncMode = isAccurateSyncMode;
	}

	public String getAccurateMatchRuleFileDir() {
		return accurateMatchRuleFileDir;
	}

	public void setAccurateMatchRuleFileDir(String accurateMatchRuleFileDir) {
		this.accurateMatchRuleFileDir = accurateMatchRuleFileDir;
	}

	public String getPgwLogDeployDir() {
		return pgwLogDeployDir;
	}

	public void setPgwLogDeployDir(String pgwLogDeployDir) {
		this.pgwLogDeployDir = pgwLogDeployDir;
	}

	public String getRemotePgwLogBaseDir() {
		return remotePgwLogBaseDir;
	}

	public void setRemotePgwLogBaseDir(String remotePgwLogBaseDir) {
		this.remotePgwLogBaseDir = remotePgwLogBaseDir;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private ClassLoader classLoader;

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
