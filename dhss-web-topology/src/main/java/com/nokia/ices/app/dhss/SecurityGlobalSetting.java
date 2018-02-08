package com.nokia.ices.app.dhss;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.dhss.security")
public class SecurityGlobalSetting implements BeanClassLoaderAware {
	private ClassLoader classLoader;
	private String baseUrl;
    private String resourceUrl;
	private String noPerssionResourceUrl;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getNoPerssionResourceUrl() {
		return noPerssionResourceUrl;
	}

	public void setNoPerssionResourceUrl(String noPerssionResourceUrl) {
		this.noPerssionResourceUrl = noPerssionResourceUrl;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

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
