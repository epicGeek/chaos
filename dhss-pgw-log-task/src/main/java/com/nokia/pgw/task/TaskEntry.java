package com.nokia.pgw.task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.nokia.pgw.Entry;
import com.nokia.pgw.service.PgwAnalysisService;
import com.nokia.pgw.service.expt.PgwRemoteDirNotMatchedException;
import com.nokia.pgw.settings.CustomSetting;

@Component
@EnableJms
@EnableScheduling
public class TaskEntry {

	private Logger LOGGER = LogManager.getLogger(TaskEntry.class);

	@Autowired
	private CustomSetting customSetting;
	@Autowired
	private PgwAnalysisService pgwAnalysisService;
	
	@JmsListener(destination = "PGW-TASK-CONSUMER")
	private void entry(String message) throws IOException, InterruptedException {
		LOGGER.info(Entry.getLOGGER_HEAD() + "start");
		LOGGER.info("Received:" + message);
		try {
			JSONObject jsonObj = new JSONObject(message);
			String taskParam = jsonObj.getString("taskParam");
			LOGGER.info(Entry.getLOGGER_HEAD() + "message received:" + message);
			LOGGER.info(Entry.getLOGGER_HEAD() + "Task Param is:" + taskParam);
			if (taskParam.equals("start")) {

				LOGGER.info(Entry.getLOGGER_HEAD() + "PGW log analysis program starts.");
				if (customSetting.isDryRunMode() == false) {
					pgwAnalysisService.handlePartition();
					pgwLogAnalyzerEntry();
					pgwAnalysisService.clearTempFile();
				} else {
					pgwAnalysisService.rsyncCommandDryRunModeTester();
				}
				LOGGER.info(Entry.getLOGGER_HEAD() + "PGW log analysis program ends now...");
			} else {
				LOGGER.info("Received wrong taskParam:" + taskParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			LOGGER.info(Entry.getLOGGER_HEAD() + "end");
		}

	}

	//@Scheduled(cron = "${dhss.pgw-log.main-program-cron}")
//	private void entry() throws IOException, InterruptedException, PgwRemoteDirNotMatchedException {
//		LOGGER.info(Entry.getLOGGER_HEAD() + "PGW log analysis program starts.");
//		if (customSetting.isDryRunMode() == false) {
//			pgwAnalysisService.handlePartition();
//			pgwLogAnalyzerEntry();
//			pgwAnalysisService.clearTempFile();
//		} else {
//			pgwAnalysisService.rsyncCommandDryRunModeTester();
//		}
//		LOGGER.info(Entry.getLOGGER_HEAD() + "PGW log analysis program ends now...");
//
//	}

	private void pgwLogAnalyzerEntry() throws IOException, InterruptedException, PgwRemoteDirNotMatchedException {
		LOGGER.info(Entry.getLOGGER_HEAD() + "*******************");
		LOGGER.info(Entry.getLOGGER_HEAD() + "** DHSS-PGW-LOG ***");
		LOGGER.info(Entry.getLOGGER_HEAD() + "**PRODUCTION MODE**");
		LOGGER.info(Entry.getLOGGER_HEAD() + "*******************");
		pgwAnalysisService.checkMatchPatternFile();
		List<File> synchronizedPgwLogFileList = pgwAnalysisService.getPgwSpmlFileList();
		if (synchronizedPgwLogFileList.size() == 0) {
			LOGGER.info(Entry.getLOGGER_HEAD() + "No new PGW SPML file...");
		} else {
			LOGGER.info(Entry.getLOGGER_HEAD() + "Start to analysis files.");
			pgwAnalysisService.analysisTargetFile();
			LOGGER.info(Entry.getLOGGER_HEAD() + "All files have been analysed.");
			LOGGER.info(Entry.getLOGGER_HEAD() + "Start to load data to DB.");
			pgwAnalysisService.loadDataToDB();
		}
	}

}