package com.nokia.boss.settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.nokia.boss.bean.DefaultConfig;
import com.nokia.boss.bean.ElasticSearchConf;
import com.nokia.boss.bean.SoapGwLogin;

@Configuration
@ConfigurationProperties(prefix = "dhss.boss")
public class CustomSettings implements BeanClassLoaderAware {

	private ClassLoader classLoader;

	private ElasticSearchConf elSearchConfig;
	private List<SoapGwLogin> loginInfoList = new ArrayList<>();;
	private DefaultConfig defaultConfig;

	public List<SoapGwLogin> getLoginInfoList() {
		return loginInfoList;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ElasticSearchConf getElSearchConfig() {
		return elSearchConfig;
	}

	public void setElSearchConfig(ElasticSearchConf elSearchConfig) {
		this.elSearchConfig = elSearchConfig;
	}

	public DefaultConfig getDefaultConfig() {
		return defaultConfig;
	}

	public void setDefaultConfig(DefaultConfig defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

}
