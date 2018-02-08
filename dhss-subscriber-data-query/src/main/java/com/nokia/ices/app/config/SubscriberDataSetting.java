package com.nokia.ices.app.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.nokia.ices.app.dhss.domain.PgwAccessPoint;

@Configuration
@ConfigurationProperties(prefix = "spring.dhss.subscriber-data")
public class SubscriberDataSetting implements BeanClassLoaderAware {
	// /ProvisioningGateway/services/SPMLSubscriber10Service?wsdl
	/**
	 * PGW web service 链接
	 */
	private Integer searchLimit = 10000;
	private List<PgwAccessPoint> pgwList = new ArrayList<PgwAccessPoint>();
	private String singleQueryResponseXmlDir;
	/**
	 * 批量用户数据查询生成excel的地址
	 */
	private String downloadExcelAbsPath;
	/**
	 * 解析XML日志的模板文件路径（单用户，默认，单例）
	 */
	private String xmlAnalysisFileTemplateAbsPath;
	
	/**
	 * 多用户查询解析模板,单例，默认路径
	 */
	private String subscriberBatchAnalysisTemplateAbsPath;
	/**
	 * 国家码。中国大陆+86
	 */
	private String countryCode;
	/**
	 * 移动国家码。中国大陆460
	 */
	private String mmc;

	private String releaseVersion;

	/**
	 * 多用户查询文件保存位置
	 */
	private String multiExcelFileSaveDir;

	private String uploadAnalysisTemplateDir;

	private String dhssSubscriberBaseDir;
	
	private String analyzeMode = "dom";
	
	private Boolean showSoapLog = false;
	
	
	public Boolean getShowSoapLog() {
		return showSoapLog;
	}

	public void setShowSoapLog(Boolean showSoapLog) {
		this.showSoapLog = showSoapLog;
	}

	public String getAnalyzeMode() {
		return analyzeMode;
	}

	public void setAnalyzeMode(String analyzeMode) {
		this.analyzeMode = analyzeMode;
	}

	public Integer getSearchLimit() {
		return searchLimit;
	}
	public String testFilesPath;
	
	
	public String getTestFilesPath() {
		return testFilesPath;
	}

	public void setTestFilesPath(String testFilesPath) {
		this.testFilesPath = testFilesPath;
	}

	public void setSearchLimit(Integer searchLimit) {
		this.searchLimit = searchLimit;
	}

	public String getDhssSubscriberBaseDir() {
		return dhssSubscriberBaseDir;
	}

	public void setDhssSubscriberBaseDir(String dhssSubscriberBaseDir) {
		this.dhssSubscriberBaseDir = dhssSubscriberBaseDir;
	}

	public String getUploadAnalysisTemplateDir() {
		return this.uploadAnalysisTemplateDir;
	}

	public String getUploadAnalysisTemplateDirWithDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
		return uploadAnalysisTemplateDir + sdf.format(new Date());
	}

	public void setUploadAnalysisTemplateDir(String uploadAnalysisTemplateDir) {
		this.uploadAnalysisTemplateDir = uploadAnalysisTemplateDir;
	}

	public String getMultiExcelFileSaveDir() {
		return multiExcelFileSaveDir;
	}

	public void setMultiExcelFileSaveDir(String multiExcelFileSaveDir) {
		this.multiExcelFileSaveDir = multiExcelFileSaveDir;
	}

	public String getSubscriberBatchAnalysisTemplateAbsPath() {
		return subscriberBatchAnalysisTemplateAbsPath;
	}

	public void setSubscriberBatchAnalysisTemplateAbsPath(String subscriberBatchAnalysisTemplateAbsPath) {
		this.subscriberBatchAnalysisTemplateAbsPath = subscriberBatchAnalysisTemplateAbsPath;
	}

	public String getSingleQueryResponseXmlDir() {
		return singleQueryResponseXmlDir;
	}

	public void setSingleQueryResponseXmlDir(String singleQueryResponseXmlDir) {
		this.singleQueryResponseXmlDir = singleQueryResponseXmlDir;
	}

	public String getDownloadExcelAbsPath() {
		return downloadExcelAbsPath;
	}

	public void setDownloadExcelAbsPath(String downloadExcelAbsPath) {
		this.downloadExcelAbsPath = downloadExcelAbsPath;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMmc() {
		return mmc;
	}

	public void setMmc(String mmc) {
		this.mmc = mmc;
	}

	public List<PgwAccessPoint> getPgwList() {
		return pgwList;
	}

	public void setPgwList(List<PgwAccessPoint> pgwList) {
		this.pgwList = pgwList;
	}

	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public String getXmlAnalysisFileTemplateAbsPath() {
		return xmlAnalysisFileTemplateAbsPath;
	}

	public void setXmlAnalysisFileTemplateAbsPath(String xmlAnalysisFileTemplateAbsPath) {
		this.xmlAnalysisFileTemplateAbsPath = xmlAnalysisFileTemplateAbsPath;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	private ClassLoader classLoader;

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
