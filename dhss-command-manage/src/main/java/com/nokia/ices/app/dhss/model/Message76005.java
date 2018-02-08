package com.nokia.ices.app.dhss.model;

public class Message76005{

	// 日志路径
	private String log_path;

	private String script_type;

	// 发送方监听的队列名称
	private String srcQ;

	// 接收方监听的队列名称
	private String destQ;

	// 当前请求的会话标识，使用不带分割符的小写uuid串
	private String session;
	
	private String reply_type;
	
	private String invariant;
	
	private String dynamic_script;
	
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLog_path() {
		return log_path;
	}

	public void setLog_path(String log_path) {
		this.log_path = log_path;
	}

	public String getScript_type() {
		return script_type;
	}

	public void setScript_type(String script_type) {
		this.script_type = script_type;
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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getReply_type() {
		return reply_type;
	}

	public void setReply_type(String reply_type) {
		this.reply_type = reply_type;
	}

	public String getInvariant() {
		return invariant;
	}

	public void setInvariant(String invariant) {
		this.invariant = invariant;
	}

	public String getDynamic_script() {
		return dynamic_script;
	}

	public void setDynamic_script(String dynamic_script) {
		this.dynamic_script = dynamic_script;
	}


}
