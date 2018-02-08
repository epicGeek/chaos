package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss")
public class PropertiesConfig implements BeanClassLoaderAware{
	
	private static String pingCmd;




	public static String getPingCmd() {
		return pingCmd;
	}


	public static void setPingCmd(String pingCmd) {
		PropertiesConfig.pingCmd = pingCmd;
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
