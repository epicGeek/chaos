package com.nokia.ices.app.dhss.message;


public class MessageService extends MessageBase{
	
	private String type;
	
	private String log_path;
	
	
	private String invariant;
	
	//网元名称
    private Integer msgCode;
    
    
    public Integer getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(Integer msgCode) {
        this.msgCode = msgCode;
    }
	
	

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


	public String getInvariant() {
		return invariant;
	}

	public void setInvariant(String invariant) {
		this.invariant = invariant;
	}


}
