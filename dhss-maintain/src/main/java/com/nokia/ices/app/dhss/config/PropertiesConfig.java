package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss")
public class PropertiesConfig implements BeanClassLoaderAware{
	
	//日常运维发送AMQ队列名称
	private static String desQName;
	
	private static String baseLogPath;
	
	private static String compBasePath;
	
	public static String getDesQName() {
		return desQName;
	}


	public static void setDesQName(String desQName) {
		PropertiesConfig.desQName = desQName;
	}

	public static String getBaseLogPath() {
		return baseLogPath;
	}


	public static void setBaseLogPath(String baseLogPath) {
		PropertiesConfig.baseLogPath = baseLogPath;
	}

	public static String getCompBasePath() {
		return compBasePath;
	}


	public static void setCompBasePath(String compBasePath) {
		PropertiesConfig.compBasePath = compBasePath;
	}

	private ClassLoader classLoader;
	
	public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    public ClassLoader getClassLoader() {
        return classLoader;
    }

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
