package com.nokia.ices.app.dhss.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.JSchException;
import com.nokia.ices.app.dhss.bean.SessionHolder;
import com.nokia.ices.app.dhss.bean.UserSession;
import com.nokia.ices.app.dhss.config.OneClickAccessCustomSettings;
import com.nokia.ices.app.dhss.domain.console.ConsoleConnectionInstance;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.repository.console.ConsoleConnectionInstanceRepository;
import com.nokia.ices.app.dhss.repository.equipment.EquipmentUnitRepository;
import com.nokia.ices.app.dhss.service.OneClickAccessService;
import com.nokia.ices.app.dhss.util.SSHUtil;

@Service
public class OneClickAccessServiceImpl implements OneClickAccessService {

	private static final Logger logger = LoggerFactory.getLogger(OneClickAccessServiceImpl.class);

	@Autowired
	private OneClickAccessCustomSettings appCustomSettings;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private EquipmentUnitRepository equipmentUnitRepository;

	@Autowired
	private ConsoleConnectionInstanceRepository consoleConnectionInstanceRepository;

	public void downloadLog(String filePath, HttpServletRequest request, HttpServletResponse response) {
		File downloadFile = new File(filePath);
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(downloadFile.getAbsolutePath());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		response.reset();
		response.setContentType("bin");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
		byte[] b = new byte[100];
		int len;
		try {
			while ((len = inStream.read(b)) > 0)
				response.getOutputStream().write(b, 0, len);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public EquipmentUnit findEquipmentUnitById(Long id) {
		return equipmentUnitRepository.findOne(id);
	}

	@Override
	public String generateSessionId(Long unitId, String token, String userName) {
		EquipmentUnit unit = equipmentUnitRepository.findOne(unitId);

		String sessionId = UUID.randomUUID().toString().replaceAll("-", "");

		connectHost(unit, sessionId);

		ConsoleConnectionInstance consoleConnectInstance = new ConsoleConnectionInstance();
		consoleConnectInstance.setStartTime(new Date());
		consoleConnectInstance.setSessionId(sessionId);
		consoleConnectInstance.setToken(token);
		consoleConnectInstance.setLoginUnitName(unit.getUnitName());
		consoleConnectInstance.setLoginUserName(unit.getLoginName());
		consoleConnectInstance.setIcesUserName(userName);
		consoleConnectInstance.setLogPath(appCustomSettings.getLogStoragePath() + "/" + sessionId + ".log");
		consoleConnectionInstanceRepository.save(consoleConnectInstance);

		return sessionId;
	}

	private void connectHost(EquipmentUnit unit, String sessionId) {
		// 判断是否直连
		if (!unit.isDirect()) {
			EquipmentUnit unitProxy = new EquipmentUnit();
			unitProxy.setLoginName(unit.getJumperUserName());
			unitProxy.setLoginPassword(unit.getJumperPassword());
			unitProxy.setServerIp(unit.getJumperIp());
			unitProxy.setServerPort(Integer.parseInt(unit.getJumperPort()));
			try {
				SSHUtil.createSession(sessionId, unitProxy, simpMessagingTemplate);
			} catch (JSchException | IOException e) {
				throw new RuntimeException(e.getMessage());
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			UserSession userSession = SessionHolder.getUserSession(sessionId);
			String loginCmd = "";
			if ("telnet".equalsIgnoreCase(unit.getServerProtocol())) {
				loginCmd = ("telnet " + unit.getServerIp());
			} else if ("ssh".equalsIgnoreCase(unit.getServerProtocol())) {
				loginCmd = ("ssh " + unit.getLoginName() + "@" + unit.getServerIp());
			} else {
				throw new RuntimeException("Unsupported protocol:" + unit.getServerProtocol());
			}
			userSession.getCommander().println(loginCmd);
		} else {
			connectUnit(unit, sessionId);
		}
	}

	private void connectUnit(EquipmentUnit unit, String sessionId) {
		if ("TELNET".equalsIgnoreCase(unit.getServerProtocol())) {
			EquipmentUnit unitProxy = appCustomSettings.getProxyServerForTelnet();
			try {
				SSHUtil.createSession(sessionId, unitProxy, simpMessagingTemplate);
			} catch (JSchException | IOException e) {
				throw new RuntimeException(e.getMessage());
			}

			int retry = 0;
			int waitInterval = 500;
			UserSession userSession = SessionHolder.getUserSession(sessionId);
			userSession.getCommander().println("telnet " + unit.getServerIp());
			Map<String, String> prompts = new HashMap<String, String>();

			if (unit.getUnitType().equalsIgnoreCase("OMU") || unit.getUnitType().equalsIgnoreCase("SGW")) {
				prompts.put("username", "SERNAME");
				prompts.put("password", "ASSWORD");
				prompts.put("success", "_>");
			} else {
				prompts.put("username", "ogin:");
				prompts.put("password", "assword:");
				prompts.put("success", ">");
			}
			while (retry <= 10) {
				String outputString = SessionHolder.getOutput(sessionId).toString();
				String enterSymbol = "\n";
				if (unit.getUnitType().equalsIgnoreCase("OMU") || unit.getUnitType().equalsIgnoreCase("SGW")) {
					int aaa = outputString.lastIndexOf("<");
					if (aaa != -1) {
						outputString = outputString.substring(0, aaa).trim();
					}
					enterSymbol = "\r\n";
				} else {
					outputString = outputString.trim();
				}
				logger.info("#####" + retry + "===\n" + outputString);
				if (outputString.endsWith(prompts.get("username"))) {
					logger.info("INPUTTING USERNAME");
					userSession.getCommander().print(unit.getLoginName());
					userSession.getCommander().print(enterSymbol);
					logger.info("USERNAME CONTINUE");
					try {
						Thread.sleep(waitInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				} else if (outputString.endsWith(prompts.get("password"))) {
					logger.info("INPUTTING PASSWORD");
					userSession.getCommander().print(unit.getLoginPassword());
					userSession.getCommander().print(enterSymbol);
					logger.info("PASSWORD CONTINUE");
					try {
						Thread.sleep(waitInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				} else if (outputString.endsWith(prompts.get("success"))) {
					logger.info("LOG IN SUCCESS");
					userSession.getCommander().print(enterSymbol);
					break;
				} else {
					logger.info("INPUT PROCESS WILL RETRY IN " + (retry * waitInterval / 1000) + " SECOND(S)");
					userSession.getCommander().print(enterSymbol);
					retry++;
				}
				try {
					Thread.sleep(retry * waitInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} else {
			try {
				SSHUtil.createSession(sessionId, unit, simpMessagingTemplate);
			} catch (JSchException | IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

}
