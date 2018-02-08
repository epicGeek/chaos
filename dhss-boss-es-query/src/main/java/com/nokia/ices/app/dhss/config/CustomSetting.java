package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dhss.boss.query")
public class CustomSetting implements BeanClassLoaderAware{
	
	private String countryCode = "86";
	private String mmc = "460";
	private String elasticSearchHost;
	private String indexNamePattern;
	private String typeName;
	private String bossVersion = "chinamobile";
	private Boolean hlrsnTransform = false;
	private Integer exportLimit = 10000;
	
	
	
	public String getIndexNamePattern() {
		return indexNamePattern;
	}


	public void setIndexNamePattern(String indexNamePattern) {
		this.indexNamePattern = indexNamePattern;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public Integer getExportLimit() {
		return exportLimit;
	}


	public void setExportLimit(Integer exportLimit) {
		this.exportLimit = exportLimit;
	}


	public Boolean getHlrsnTransform() {
		return hlrsnTransform;
	}


	public void setHlrsnTransform(Boolean hlrsnTransform) {
		this.hlrsnTransform = hlrsnTransform;
	}


	public String getBossVersion() {
		return bossVersion;
	}


	public void setBossVersion(String bossVersion) {
		this.bossVersion = bossVersion;
	}

	public String getElasticSearchHost() {
		return elasticSearchHost;
	}


	public void setElasticSearchHost(String elasticSearchHost) {
		this.elasticSearchHost = elasticSearchHost;
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
