package com.nokia.ices.app.dhss.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss.subscriber")
public class SubscriberConfig implements BeanClassLoaderAware{
	
	private  String mmlIp;
	
	private  String mmlPort;
	
	private  String mmlProtocol;
	
	private  String mmlUserName;
	
	private  String mmlUserPwd;

	private  String ifDynamicGetIp;
	
	private List<String> soapIpList = new ArrayList<String>();
	
	private String EditVrlOrSgsnServerPort;
	
	private String EditVrlOrSgsnUserName;
	
	private String EditVrlOrSgsnPassWord;
	
	public static String baseLogPath;
	

	public String getEditVrlOrSgsnServerPort() {
		return EditVrlOrSgsnServerPort;
	}


	public void setEditVrlOrSgsnServerPort(String editVrlOrSgsnServerPort) {
		EditVrlOrSgsnServerPort = editVrlOrSgsnServerPort;
	}


	public String getEditVrlOrSgsnUserName() {
		return EditVrlOrSgsnUserName;
	}


	public void setEditVrlOrSgsnUserName(String editVrlOrSgsnUserName) {
		EditVrlOrSgsnUserName = editVrlOrSgsnUserName;
	}


	public String getEditVrlOrSgsnPassWord() {
		return EditVrlOrSgsnPassWord;
	}


	public void setEditVrlOrSgsnPassWord(String editVrlOrSgsnPassWord) {
		EditVrlOrSgsnPassWord = editVrlOrSgsnPassWord;
	}


	public List<String> getSoapIpList() {
		return soapIpList;
	}


	public void setSoapIpList(List<String> soapIpList) {
		this.soapIpList = soapIpList;
	}


	public String getIfDynamicGetIp() {
		return ifDynamicGetIp;
	}


	public void setIfDynamicGetIp(String ifDynamicGetIp) {
		this.ifDynamicGetIp = ifDynamicGetIp;
	}


	public String getMmlIp() {
		return mmlIp;
	}


	public void setMmlIp(String mmlIp) {
		this.mmlIp = mmlIp;
	}


	public String getMmlPort() {
		return mmlPort;
	}


	public void setMmlPort(String mmlPort) {
		this.mmlPort = mmlPort;
	}


	public String getMmlProtocol() {
		return mmlProtocol;
	}


	public void setMmlProtocol(String mmlProtocol) {
		this.mmlProtocol = mmlProtocol;
	}


	public String getMmlUserName() {
		return mmlUserName;
	}


	public void setMmlUserName(String mmlUserName) {
		this.mmlUserName = mmlUserName;
	}


	public String getMmlUserPwd() {
		return mmlUserPwd;
	}


	public void setMmlUserPwd(String mmlUserPwd) {
		this.mmlUserPwd = mmlUserPwd;
	}


	public String getBaseLogPath() {
		return baseLogPath;
	}


	public void setBaseLogPath(String baseLogPath) {
		this.baseLogPath = baseLogPath;
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
