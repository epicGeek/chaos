package com.nokia.boss.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nokia.boss.bean.SoapGwLogin;
import com.nokia.boss.service.ElasticSearchBulkService;
import com.nokia.boss.service.SyncFileService;
import com.nokia.boss.service.TaskProcService;
import com.nokia.boss.service.YmalService;
import com.nokia.boss.settings.CustomSettings;
import com.nokia.boss.task.LoadStaticData;
import com.nokia.boss.util.FileProcUtils;

@Service
public class TaskProcServiceImpl implements TaskProcService {
	private static final Logger LOGGER = LogManager.getLogger(TaskProcServiceImpl.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
	@Autowired
	private YmalService ymalService;
	@Autowired
	private SyncFileService syncFileService;
	@Autowired
	private CustomSettings customSettings;
	@Autowired
	private ElasticSearchBulkService esSearchService;
	@Override
	public void executeEntry() throws IOException, InterruptedException, DocumentException, ParseException {
		String bossVersion = customSettings.getDefaultConfig().getBossVersion();
		LOGGER.info("*********************");
		LOGGER.info("BOSS version:{}", bossVersion);
		LOGGER.info("*********************");
		LOGGER.info("Checking pattern..");
		Integer startMinute = Integer.valueOf(sdf.format(new Date()));
		
		if (startMinute < 15 || LoadStaticData.getCURR_ERR_PATTERN().contains(FileProcUtils.DATE_YY_MM_DD)) {
			esSearchService.deleteESData();
			checkPatternFile();
			
		} else {
			LOGGER.info("It's the first period of the day.Still rsync yesterday's file.");
		}
		syncDataFiles(bossVersion);
		afterSyncAndAnalyse(startMinute);
	}

	/**
	 * 每次同步前会检查规范文件，是否是同步今日的数据。如果不是，更改文件。
	 * 
	 * @param bossVersion
	 * @throws IOException
	 */
	private void checkPatternFile() throws IOException {
		String soapPath = customSettings.getDefaultConfig().getRuleFileAbsSoapPath();
		String errPath = customSettings.getDefaultConfig().getRuleFileAbsErrPath();
		FileProcUtils.writePatternFile(soapPath, 0);
		FileProcUtils.writePatternFile(errPath, 1);
	}

	
	private void syncDataFiles(String bossVersion)
			throws IOException, InterruptedException, DocumentException, ParseException {
		List<SoapGwLogin> sgwList = customSettings.getLoginInfoList();
		List<Future<String>> fuList = new ArrayList<>();
		for (SoapGwLogin sgw : sgwList) {
			// 多线程同时获取和解析
			if (customSettings.getDefaultConfig().isUseDefaultUser()) {
				LOGGER.info("Default user name and password is configured");
				sgw.setUserName(customSettings.getDefaultConfig().getDefaultUser());
				sgw.setPassword(customSettings.getDefaultConfig().getDefaulPassword());
			}
			fuList.add(syncFileService.syncFileTask(sgw, customSettings.getDefaultConfig()));
		}
		try {
			for (Future<String> fu : fuList) {
				while (true) {
					if (fu.isDone()) {
						LOGGER.info(fu.get());
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void afterSyncAndAnalyse(Integer startMinute) {
		if (startMinute < 15) {
			LOGGER.info("After first period of the day.");
			LOGGER.info("Reset all mark and clean old ignoreFile.");
			ymalService.resetAllMark();
			ymalService.cleanOldIgnoreFile(customSettings.getDefaultConfig().getSaveIgnoreDataDay());
			List<File> rsyncDirList = LoadStaticData.getBossDir().get("rsync");
			FileProcUtils.deleteOldRsynDir(rsyncDirList, 1);
		}
	}

}
