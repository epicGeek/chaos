package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.dhss.security")
public class SecurityGlobalSetting implements BeanClassLoaderAware {
	private ClassLoader classLoader;

	private String remoteBaseUrl;

	private String resourcePartUrl;
	private String noPermissionResourcePartUrl;
	private String removeTokenPartUrl;
	private String eventManagerDataPartUrl;
	private String afterLdapAuthPartUrl;

	private String editUserPwdPartUrl;
	private String accessTokenPartUrl;
	private String validateTokenPartUrl;
	private String menuCheckUrl;
	private String licenseCheckUrl;


	public String getLicenseCheckUrl() {
		return this.remoteBaseUrl +"/"+ licenseCheckUrl;
	}

	public void setLicenseCheckUrl(String licenseCheckUrl) {
		this.licenseCheckUrl = licenseCheckUrl;
	}

	private String ldapServerAddress;
	private String ldapDN;
	private String ldapDC;
	
	private String proviceName;
	private String cityName;
	private String serviceName;
	private String projectName;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProviceName() {
		return proviceName;
	}

	public void setProviceName(String proviceName) {
		this.proviceName = proviceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	private String[] tokenProtectedPath;
	private String[] accessTokenHeader;
	

	public String getMenuCheckUrl() {
		return this.remoteBaseUrl +"/"+ menuCheckUrl;
	}

	public void setMenuCheckUrl(String menuCheckUrl) {
		this.menuCheckUrl = menuCheckUrl;
	}

	public String getRemoteBaseUrl() {
		return remoteBaseUrl;
	}

	public void setRemoteBaseUrl(String remoteBaseUrl) {
		this.remoteBaseUrl = remoteBaseUrl;
	}

	public String getResourcePartUrl() {
		return resourcePartUrl;
	}

	public void setResourcePartUrl(String resourcePartUrl) {
		this.resourcePartUrl = resourcePartUrl;
	}

	public String getNoPermissionResourcePartUrl() {
		return noPermissionResourcePartUrl;
	}

	public void setNoPermissionResourcePartUrl(String noPermissionResourcePartUrl) {
		this.noPermissionResourcePartUrl = noPermissionResourcePartUrl;
	}

	public String getRemoveTokenPartUrl() {
		return removeTokenPartUrl;
	}

	public void setRemoveTokenPartUrl(String removeTokenPartUrl) {
		this.removeTokenPartUrl = removeTokenPartUrl;
	}

	public String getEventManagerDataPartUrl() {
		return eventManagerDataPartUrl;
	}

	public void setEventManagerDataPartUrl(String eventManagerDataPartUrl) {
		this.eventManagerDataPartUrl = eventManagerDataPartUrl;
	}

	public String getAfterLdapAuthPartUrl() {
		return afterLdapAuthPartUrl;
	}

	public void setAfterLdapAuthPartUrl(String afterLdapAuthPartUrl) {
		this.afterLdapAuthPartUrl = afterLdapAuthPartUrl;
	}

	public String getEditUserPwdPartUrl() {
		return editUserPwdPartUrl;
	}

	public void setEditUserPwdPartUrl(String editUserPwdPartUrl) {
		this.editUserPwdPartUrl = editUserPwdPartUrl;
	}

	public String getAccessTokenPartUrl() {
		return accessTokenPartUrl;
	}

	public void setAccessTokenPartUrl(String accessTokenPartUrl) {
		this.accessTokenPartUrl = accessTokenPartUrl;
	}

	public String getValidateTokenPartUrl() {
		return validateTokenPartUrl;
	}

	public void setValidateTokenPartUrl(String validateTokenPartUrl) {
		this.validateTokenPartUrl = validateTokenPartUrl;
	}

	public String[] getTokenProtectedPath() {
		return tokenProtectedPath;
	}

	public void setTokenProtectedPath(String[] tokenProtectedPath) {
		this.tokenProtectedPath = tokenProtectedPath;
	}

	public String[] getAccessTokenHeader() {
		return accessTokenHeader;
	}

	public void setAccessTokenHeader(String[] accessTokenHeader) {
		this.accessTokenHeader = accessTokenHeader;
	}

	public String getLdapServerAddress() {
		return ldapServerAddress;
	}

	public void setLdapServerAddress(String ldapServerAddress) {
		this.ldapServerAddress = ldapServerAddress;
	}

	public String getLdapDN() {
		return ldapDN;
	}

	public void setLdapDN(String ldapDN) {
		this.ldapDN = ldapDN;
	}

	public String getLdapDC() {
		return ldapDC;
	}

	public void setLdapDC(String ldapDC) {
		this.ldapDC = ldapDC;
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

	public String getValidateTokenUrl() {
		return this.remoteBaseUrl +"/"+ this.validateTokenPartUrl;
	}

	public String getAccessTokenUrl() {
		return this.remoteBaseUrl +"/"+ this.accessTokenPartUrl;
	}

	public String getAfterLdapAuthUrl() {
		return this.remoteBaseUrl +"/"+ this.afterLdapAuthPartUrl;
	}

	public String getNoPermissionResourceUrl() {
		return this.remoteBaseUrl +"/"+ this.noPermissionResourcePartUrl;
	}

	public String getResourceUrl() {
		return this.remoteBaseUrl +"/"+ this.resourcePartUrl;
	}

	public String getEditUserPwdUrl() {
		return this.remoteBaseUrl +"/"+ this.editUserPwdPartUrl;
	}

	public String getRemoveTokenUrl() {
		return this.remoteBaseUrl +"/"+ this.removeTokenPartUrl;
	}

	public String getEventManagerDataUrl() {
		return this.remoteBaseUrl +"/"+ this.eventManagerDataPartUrl;
	}
}
