package com.nokia.ices.app.dhss.message;
/**
 * @author Mars
 * @date 2014-02-24
 *
 */
public class MessageBase {

	//发送方监听的队列名称
	private String srcQ;
	
	//接收方监听的队列名称
	private String destQ;
	
	//当前请求的会话标识，使用不带分割符的小写uuid串
	private String session;

    


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
	
   
}
