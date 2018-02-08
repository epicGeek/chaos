package com.nokia.ices.app.pgw.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.dhss.pgw")
public class PgwLogDataQueryConfig implements BeanClassLoaderAware {
	
	private Integer exportRecordLimit = 10000;
	private String countryCode;
	private String notStandardCountryCode;
	private String mmc;
	private Boolean searchLogMode = false;
	
	public Boolean getSearchLogMode() {
		return searchLogMode;
	}

	public void setSearchLogMode(Boolean searchLogMode) {
		this.searchLogMode = searchLogMode;
	}

	public String getNotStandardCountryCode() {
		return notStandardCountryCode;
	}

	public void setNotStandardCountryCode(String notStandardCountryCode) {
		this.notStandardCountryCode = notStandardCountryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMmc() {
		return mmc;
	}

	public void setMmc(String mmc) {
		this.mmc = mmc;
	}

	public Integer getExportRecordLimit() {
		return exportRecordLimit;
	}

	public void setExportRecordLimit(Integer exportRecordLimit) {
		this.exportRecordLimit = exportRecordLimit;
	}

	private ClassLoader classLoader;
	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
}
