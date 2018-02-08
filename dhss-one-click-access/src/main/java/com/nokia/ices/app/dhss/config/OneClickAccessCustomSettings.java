package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

@Configuration
@ConfigurationProperties(prefix = "dhss.one-click-access")
public class OneClickAccessCustomSettings implements BeanClassLoaderAware {

	private String interfaceTypeResourceName;
	
	private String logStoragePath;
	
	private Integer clientIdleInterval;
	
	private EquipmentUnit proxyServerForTelnet;
	
	private Boolean needsProxy = false;

	public EquipmentUnit getProxyServerForTelnet() {
		return proxyServerForTelnet;
	}

	public void setProxyServerForTelnet(EquipmentUnit proxyServerForTelnet) {
		this.proxyServerForTelnet = proxyServerForTelnet;
	}

	public Integer getClientIdleInterval() {
		return clientIdleInterval;
	}

	public void setClientIdleInterval(Integer clientIdleInterval) {
		this.clientIdleInterval = clientIdleInterval;
	}

	public String getLogStoragePath() {
		return logStoragePath;
	}

	public void setLogStoragePath(String logStoragePath) {
		this.logStoragePath = logStoragePath;
	}
	
	public String getInterfaceTypeResourceName() {
		return interfaceTypeResourceName;
	}

	public void setInterfaceTypeResourceName(String interfaceTypeResourceName) {
		this.interfaceTypeResourceName = interfaceTypeResourceName;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	public Boolean getNeedsProxy() {
		return needsProxy;
	}

	public void setNeedsProxy(Boolean needsProxy) {
		this.needsProxy = needsProxy;
	}

	private ClassLoader classLoader;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
