package com.nokia.pgw.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.nokia.pgw.service.expt.PgwRemoteDirNotMatchedException;

/**
 * 
 * @author Pei Nan
 *	
 */
public interface PgwAnalysisService {
	public List<Map<String,Object>> deviceBasicInfo();
	public List<String> getAllRsyncCommand(); //台湾指令
	public List<String> getAllRsyncCommandForMainland(); //大陆指令
	public List<String> getRsyncInfo(String rsyncCmd);
	public void loadDataToDB();
	public void analysisTargetFile();
	public void handlePartition();
	public void clearTempFile();
	public void handleHalfAutoMode();
	public void rsyncCommandDryRunModeTester() throws IOException, InterruptedException;
	public List<File> getPgwSpmlFileList() throws IOException, InterruptedException, PgwRemoteDirNotMatchedException;
	public void manualMode();
	public void checkMatchPatternFile() throws IOException;
}
