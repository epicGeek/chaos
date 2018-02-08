package com.nokia.boss.bean;

public class ElasticSearchConf {
	private String elasticSearchHost;
	private String mockIndex;
	private String soapType;
	private int mockBatch;
	private int elasticSearchPort;
	public String getElasticSearchHost() {
		return elasticSearchHost;
	}
	public void setElasticSearchHost(String elasticSearchHost) {
		this.elasticSearchHost = elasticSearchHost;
	}
	public String getMockIndex() {
		return mockIndex;
	}
	public void setMockIndex(String mockIndex) {
		this.mockIndex = mockIndex;
	}
	public String getSoapType() {
		return soapType;
	}
	public void setSoapType(String soapType) {
		this.soapType = soapType;
	}
	public int getMockBatch() {
		return mockBatch;
	}
	public void setMockBatch(int mockBatch) {
		this.mockBatch = mockBatch;
	}
	public int getElasticSearchPort() {
		return elasticSearchPort;
	}
	public void setElasticSearchPort(int elasticSearchPort) {
		this.elasticSearchPort = elasticSearchPort;
	}


}
