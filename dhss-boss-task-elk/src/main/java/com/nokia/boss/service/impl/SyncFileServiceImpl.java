package com.nokia.boss.service.impl;

import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.nokia.boss.bean.DefaultConfig;
import com.nokia.boss.bean.SoapGwLogin;
import com.nokia.boss.service.AnalysedSerivice;
import com.nokia.boss.service.SyncFileService;
import com.nokia.boss.service.YmalService;
import com.nokia.boss.util.GsonUtils;
import com.nokia.boss.util.SyncCallable;

/**
 * 同步文件和解析文件
 * 
 * @author pactera
 *
 */
@Service("syncFileService")
public class SyncFileServiceImpl implements SyncFileService {

	private static final Logger LOGGER = LogManager.getLogger(SyncFileServiceImpl.class);
	@Autowired
	private YmalService ymalService;

	@Autowired
	private AnalysedSerivice analysedSerivice;

	@Async
	@Override
	public Future<String> syncFileTask(SoapGwLogin soapGwLogin, DefaultConfig defaultConfig) {
		LOGGER.info("syncFileTask, parementer={}", GsonUtils.toJson(soapGwLogin));
		Future<String> future;
		try {
			SyncCallable sc = new SyncCallable(soapGwLogin, defaultConfig, ymalService, analysedSerivice);
			future = new AsyncResult<String>(sc.syncFile());
		} catch (Exception e) {
			String message = soapGwLogin.getSoapGwName() + ":" + e.getMessage();
			future = new AsyncResult<String>(message);
		}
		return future;
	}

}
