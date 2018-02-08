package com.nokia.boss.service;

import java.util.concurrent.Future;

import com.nokia.boss.bean.DefaultConfig;
import com.nokia.boss.bean.SoapGwLogin;

public interface SyncFileService {

	public Future<String> syncFileTask(SoapGwLogin sgw, DefaultConfig defaultConfig);

}