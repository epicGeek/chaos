package com.nokia.ices.app.dhss.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.TimerTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class UserSession extends TimerTask{
	private Session session;
	private String sessionId;
	private Channel channel;
	private PrintStream commander;
	private InputStream outFromChannel;
	private OutputStream inputToChannel;
	private Long createTimestamp;
	private Long lastOperateTimeStamp;
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void destroy() {
		try {
			inputToChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		commander.close();
		channel.disconnect();
		session.disconnect();
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Long getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(Long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public PrintStream getCommander() {
		return commander;
	}
	public void setCommander(PrintStream commander) {
		this.commander = commander;
	}
	public InputStream getOutFromChannel() {
		return outFromChannel;
	}
	public void setOutFromChannel(InputStream outFromChannel) {
		this.outFromChannel = outFromChannel;
	}
	public OutputStream getInputToChannel() {
		return inputToChannel;
	}
	public void setInputToChannel(OutputStream inputToChannel) {
		this.inputToChannel = inputToChannel;
	}
	public Long getLastOperateTimeStamp() {
		return lastOperateTimeStamp;
	}
	public void setLastOperateTimeStamp(Long lastOperateTimeStamp) {
		this.lastOperateTimeStamp = lastOperateTimeStamp;
	}
	@Override
	public void run() {
		
	}
	
}
