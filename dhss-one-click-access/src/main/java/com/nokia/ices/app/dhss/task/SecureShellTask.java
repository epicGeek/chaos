package com.nokia.ices.app.dhss.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nokia.ices.app.dhss.bean.SessionHolder;
import com.nokia.ices.app.dhss.bean.UserSession;

public class SecureShellTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SecureShellTask.class);

	InputStream outFromChannel;
	UserSession userSession;

	public SecureShellTask(UserSession userSession2, InputStream outFromChannel2) {
		this.userSession = userSession2;
		this.outFromChannel = outFromChannel2;
	}

	@Override
	public void run() {

		InputStreamReader isr = new InputStreamReader(outFromChannel);
		BufferedReader br = new BufferedReader(isr);
		String sessionId = userSession.getSessionId();
		try {
			char[] buff = new char[1024];
			int read;
			while ((read = br.read(buff)) != -1) {
				SessionHolder.getOutput(sessionId).append(buff, 0, read);
//				logger.info(SessionHolder.getOutput(sessionId).toString());
				Thread.sleep(50);
			}
			SessionHolder.removeOutput(sessionId);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
	}

}
