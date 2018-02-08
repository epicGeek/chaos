package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss.task")
public class ProjectConfig implements BeanClassLoaderAware{
	
	
	private static String desQName;
	
	private static String scriptServerName;
	
	private static String basePath;
	
	private static String smsName;
	
	

	public static String getDesQName() {
		return desQName;
	}


	public static void setDesQName(String desQName) {
		ProjectConfig.desQName = desQName;
	}


	public static String getScriptServerName() {
		return scriptServerName;
	}


	public static void setScriptServerName(String scriptServerName) {
		ProjectConfig.scriptServerName = scriptServerName;
	}


	public static String getBasePath() {
		return basePath;
	}


	public static void setBasePath(String basePath) {
		ProjectConfig.basePath = basePath;
	}


	public static String getSmsName() {
		return smsName;
	}


	public static void setSmsName(String smsName) {
		ProjectConfig.smsName = smsName;
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
