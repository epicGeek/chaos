package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud-client")
public class ProjectConfig implements BeanClassLoaderAware{
	
	
	private static String profiles;
	
	private static String test;
	
	

	public static String getTest() {
		return test;
	}


	public static void setTest(String test) {
		ProjectConfig.test = test;
	}


	public static String getProfiles() {
		return profiles;
	}


	public static void setProfiles(String profiles) {
		ProjectConfig.profiles = profiles;
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
