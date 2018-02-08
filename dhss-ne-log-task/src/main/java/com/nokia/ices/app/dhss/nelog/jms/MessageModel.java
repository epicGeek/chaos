package com.nokia.ices.app.dhss.nelog.jms;

import java.util.Map;

public class MessageModel {
	
	public MessageModel(){}
	
	public MessageModel(String app, String type, String srcQ, String destQ, String sessionid, String ne,
			String neConnType, String password, Integer port, Integer priority, String procotol, String username,
			String ip, Map<String, String> content, String hostname, String netFlag, String flag, String msg,
			String src, String unitType, Integer msgCode) {
		this.app = app;
		this.type = type;
		this.srcQ = srcQ;
		this.destQ = destQ;
		this.sessionid = sessionid;
		this.ne = ne;
		this.neConnType = neConnType;
		this.password = password;
		this.port = port;
		this.priority = priority;
		this.procotol = procotol;
		this.username = username;
		this.ip = ip;
		this.content = content;
		this.hostname = hostname;
		this.netFlag = netFlag;
		this.flag = flag;
		this.msg = msg;
		this.src = src;
		this.unitType = unitType;
		this.msgCode = msgCode;
	}

	private String app;
	
	private Integer cacheTime = 5;
	
	private Integer maxConnNum=8;
	
	private String type;
	
	private String srcQ;
	
	private String destQ;
	
	private String sessionid;
	
	private String ne;
	
	private String neConnType;
	
	private String password;
	
	private Integer port;
	
	private Integer priority = 5;
	
	private String procotol;
	
	private String username;
	
	private String ip;
	
	private Map<String, String> content;
	
	private String hostname;
	
	private String netFlag;
	
	private String vendor = "nokia";
	
	private String flag;
	
	private Integer retryInterval = 3;
	
	private Integer retryTimes = 2;
	
	private Integer needJump = 0;
	
	private Integer jumpCount = 0;
	
	private String callInterfaceName = "";
	
	private String msg;
	
	private String src;
	
	private Integer exculde = 0;
	
	private Integer taskNum = 71001;
	
	private String unitType;
	
	private Integer msgCode = 71000;
	
	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public Integer getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(Integer cacheTime) {
		this.cacheTime = cacheTime;
	}

	public Integer getMaxConnNum() {
		return maxConnNum;
	}

	public void setMaxConnNum(Integer maxConnNum) {
		this.maxConnNum = maxConnNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSrcQ() {
		return srcQ;
	}

	public void setSrcQ(String srcQ) {
		this.srcQ = srcQ;
	}

	public String getDestQ() {
		return destQ;
	}

	public void setDestQ(String destQ) {
		this.destQ = destQ;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getNe() {
		return ne;
	}

	public void setNe(String ne) {
		this.ne = ne;
	}

	public String getNeConnType() {
		return neConnType;
	}

	public void setNeConnType(String neConnType) {
		this.neConnType = neConnType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getProcotol() {
		return procotol;
	}

	public void setProcotol(String procotol) {
		this.procotol = procotol;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getNetFlag() {
		return netFlag;
	}

	public void setNetFlag(String netFlag) {
		this.netFlag = netFlag;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Integer getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(Integer retryInterval) {
		this.retryInterval = retryInterval;
	}

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Integer getNeedJump() {
		return needJump;
	}

	public void setNeedJump(Integer needJump) {
		this.needJump = needJump;
	}

	public Integer getJumpCount() {
		return jumpCount;
	}

	public void setJumpCount(Integer jumpCount) {
		this.jumpCount = jumpCount;
	}

	public String getCallInterfaceName() {
		return callInterfaceName;
	}

	public void setCallInterfaceName(String callInterfaceName) {
		this.callInterfaceName = callInterfaceName;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Integer getExculde() {
		return exculde;
	}

	public void setExculde(Integer exculde) {
		this.exculde = exculde;
	}

	public Integer getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(Integer taskNum) {
		this.taskNum = taskNum;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public Integer getMsgCode() {
		return msgCode;
	}

	public void setMsgCode(Integer msgCode) {
		this.msgCode = msgCode;
	}

	
	
	
	
	
	
	

}
