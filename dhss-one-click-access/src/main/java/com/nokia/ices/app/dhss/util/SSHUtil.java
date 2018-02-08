package com.nokia.ices.app.dhss.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nokia.ices.app.dhss.bean.SessionHolder;
import com.nokia.ices.app.dhss.bean.UserSession;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.task.SecureShellTask;

public class SSHUtil {
	public static final int SESSION_TIMEOUT = 60000;
	public static final int CHANNEL_TIMEOUT = 60000;
	public static final int SERVER_ALIVE_INTERVAL = 600000;
	
	public static UserSession createSession(String sessionId, EquipmentUnit unit,SimpMessagingTemplate messageTemplate) throws JSchException, IOException {
		JSch jsch = new JSch();
		
		Session session = jsch.getSession(unit.getLoginName(), unit.getServerIp(), unit.getServerPort());

		String password = unit.getLoginPassword();

		if (password != null && !password.trim().equals("")) {
			session.setPassword(password);
		}

		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
		session.setServerAliveInterval(SERVER_ALIVE_INTERVAL);
		session.connect(SESSION_TIMEOUT);
		Channel channel = session.openChannel("shell");
		((ChannelShell) channel).setPtyType("xterm");

		InputStream outFromChannel = channel.getInputStream();
		
		OutputStream inputToChannel = channel.getOutputStream();
		PrintStream commander = new PrintStream(inputToChannel, true);


		channel.connect();
		
		
		UserSession userSession = new UserSession();
		userSession.setSessionId(sessionId);
		userSession.setSession(session);
		userSession.setChannel(channel);
		userSession.setCommander(commander);
		userSession.setInputToChannel(inputToChannel);
		userSession.setOutFromChannel(outFromChannel);
		
		Long createTimestamp = System.currentTimeMillis();
		userSession.setLastOperateTimeStamp(createTimestamp);
		userSession.setCreateTimestamp(createTimestamp);
		
		SessionHolder.addSession(sessionId, userSession);

		Runnable run = new SecureShellTask(userSession, outFromChannel);
		Thread thread = new Thread(run);
		thread.start();

		return userSession;
	}

}
