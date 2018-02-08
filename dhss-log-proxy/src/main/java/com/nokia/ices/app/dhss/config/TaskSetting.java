package com.nokia.ices.app.dhss.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss.task")
public class TaskSetting implements BeanClassLoaderAware{
	
	
	private String desQName;
	
	private String appName;
	
	private Map<String,String> urlMap = new HashMap<String,String>();
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Map<String, String> getUrlMap() {
		return urlMap;
	}
	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}
	public String getDesQName() {
		return desQName;
	}
	public void setDesQName(String desQName) {
		this.desQName = desQName;
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
