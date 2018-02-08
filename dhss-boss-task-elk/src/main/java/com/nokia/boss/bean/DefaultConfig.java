package com.nokia.boss.bean;

public class DefaultConfig {

	private boolean useDefaultUser;
	private String defaultUser;
	private String defaulPassword;
	private boolean hlrsnTransform;
	private int saveIgnoreDataDay;
	private String dataPath;
	private Integer saveDays;
	private String loadFileDir;
	private String bossVersion;
	private String rsyncDataDir;
	private String cacheDataDir;
	private String ruleFileAbsSoapPath;
	private String ruleFileAbsErrPath;
	private String rsyncCmd;
	public boolean isUseDefaultUser() {
		return useDefaultUser;
	}

	public void setUseDefaultUser(boolean useDefaultUser) {
		this.useDefaultUser = useDefaultUser;
	}

	public String getDefaultUser() {
		return defaultUser;
	}

	public void setDefaultUser(String defaultUser) {
		this.defaultUser = defaultUser;
	}

	public String getDefaulPassword() {
		return defaulPassword;
	}

	public void setDefaulPassword(String defaulPassword) {
		this.defaulPassword = defaulPassword;
	}

	public boolean getHlrsnTransform() {
		return hlrsnTransform;
	}

	public void setHlrsnTransform(boolean hlrsnTransform) {
		this.hlrsnTransform = hlrsnTransform;
	}

	public int getSaveIgnoreDataDay() {
		return saveIgnoreDataDay;
	}

	public void setSaveIgnoreDataDay(int saveIgnoreDataDay) {
		this.saveIgnoreDataDay = saveIgnoreDataDay;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public Integer getSaveDays() {
		return saveDays;
	}

	public void setSaveDays(Integer saveDays) {
		this.saveDays = saveDays;
	}

	public String getLoadFileDir() {
		return loadFileDir;
	}

	public void setLoadFileDir(String loadFileDir) {
		this.loadFileDir = loadFileDir;
	}

	public String getBossVersion() {
		return bossVersion;
	}

	public void setBossVersion(String bossVersion) {
		this.bossVersion = bossVersion;
	}

	public String getRsyncDataDir() {
		return rsyncDataDir;
	}

	public void setRsyncDataDir(String rsyncDataDir) {
		this.rsyncDataDir = rsyncDataDir;
	}

	public String getCacheDataDir() {
		return cacheDataDir;
	}

	public void setCacheDataDir(String cacheDataDir) {
		this.cacheDataDir = cacheDataDir;
	}

	public String getRuleFileAbsErrPath() {
		return ruleFileAbsErrPath;
	}

	public void setRuleFileAbsErrPath(String ruleFileAbsErrPath) {
		this.ruleFileAbsErrPath = ruleFileAbsErrPath;
	}

	public String getRuleFileAbsSoapPath() {
		return ruleFileAbsSoapPath;
	}

	public String getRsyncCmd() {
		return rsyncCmd;
	}


	public void setRsyncCmd(String rsyncCmd) {
		this.rsyncCmd = rsyncCmd;
	}

	public void setRuleFileAbsSoapPath(String ruleFileAbsSoapPath) {
		this.ruleFileAbsSoapPath = ruleFileAbsSoapPath;
	}
}
