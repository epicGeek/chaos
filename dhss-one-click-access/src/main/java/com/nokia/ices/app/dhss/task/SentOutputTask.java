package com.nokia.ices.app.dhss.task;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.bean.ResponseMessage;
import com.nokia.ices.app.dhss.bean.SessionHolder;
import com.nokia.ices.app.dhss.config.OneClickAccessCustomSettings;

/**
 * class to send output to web socket client
 */
@Component
@EnableScheduling
public class SentOutputTask {

	private static Logger logger = LoggerFactory.getLogger(SentOutputTask.class);

	private static Map<String, Integer> outputMonitor = new ConcurrentHashMap<String, Integer>();

	private static String wsUrlTemplate = "/queue/terminal";

	private static final String ANSI_COLOR = "\\x1B(\\(B)?\\[([0-9]{1,2}(;[0-9]{1,2})?)?[m|K|H]";

	private static final String ANSI_B = "\\x1B\\(B";

	@Autowired
	private OneClickAccessCustomSettings appCustomSettings;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Scheduled(fixedDelay = 10000)
	public void runClean() {
		Long currentTime = System.currentTimeMillis();
		logger.debug("outputMonitor:" + outputMonitor.size() + " outputMap:" + SessionHolder.getAllOutput().size()
				+ " sessionMap:" + SessionHolder.getAllSession().size());
		SessionHolder.getAllSession().forEach((sessionId, userSession) -> {
			Long lastOptime = userSession.getLastOperateTimeStamp();
			if (currentTime - lastOptime > appCustomSettings.getClientIdleInterval()) {
				String disconnectNotice = "DHSS: idle " + appCustomSettings.getClientIdleInterval() + " ms,disconnect.";
				String endpointUrl = wsUrlTemplate + "/" + sessionId;
				simpMessagingTemplate.convertAndSend(endpointUrl, new ResponseMessage("disconnect", disconnectNotice));
				logger.debug(sessionId + "->" + disconnectNotice);
				SessionHolder.destroy(sessionId);
				outputMonitor.remove(sessionId);

			}
		});

	}

	private void saveLog(String sessionId) {
		StringBuilder output = SessionHolder.getOutput(sessionId);
		String outPutFilePath = appCustomSettings.getLogStoragePath() + sessionId + ".log";
		File logFile = FileUtils.getFile(outPutFilePath);
		try {
			FileUtils.writeStringToFile(logFile, outputLogFilter(output.toString()), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Scheduled(fixedDelay = 100)
	public void runTask() {
		Map<String, StringBuilder> sessionMap = SessionHolder.getAllOutput();
		sessionMap.forEach((sessionId, out) -> {
			if (!outputMonitor.containsKey(sessionId)) {
				outputMonitor.put(sessionId, 0);
			}
			Integer originContentLength = outputMonitor.get(sessionId);
			if (originContentLength != out.length()) {
				String appendContent = out.substring(originContentLength);
				outputMonitor.put(sessionId, out.length());
				simpMessagingTemplate.convertAndSend(wsUrlTemplate + "/" + sessionId,
						new ResponseMessage(appendContent));
				saveLog(sessionId);
			}
		});
	}

	private String outputLogFilter(String originText) {
		String afterFilter = originText.replaceAll(ANSI_COLOR, "").replaceAll(ANSI_B, "");
		String[] lines = StringUtils.split(afterFilter, "\n");
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			if (line.contains("[") && line.contains("@") && line.contains("]$")) {
				line = line.substring(line.lastIndexOf("["));
			}
			sb.append(line + "\n");
		}
		return sb.toString();
	}
}
