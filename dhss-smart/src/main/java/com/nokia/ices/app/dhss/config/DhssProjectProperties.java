package com.nokia.ices.app.dhss.config;


import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.dhss")
public class DhssProjectProperties implements BeanClassLoaderAware {

	
	private static String baseLogPath;
    public static String getBaseLogPath() {
		return baseLogPath;
	}


	public static void setBaseLogPath(String baseLogPath) {
		DhssProjectProperties.baseLogPath = baseLogPath;
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
