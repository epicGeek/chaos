package com.nokia.boss.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.dom4j.DocumentException;

import com.nokia.boss.bean.MessageItem;

public interface AnalysedSerivice {

	public void soapLogDataAnalysis(File soapLogFile, String soapGwName, Map<String, MessageItem> map)
			throws IOException;

	public Map<String, MessageItem> errLogDataAnalysis(File errLogFile, String soapGwName)
			throws IOException, DocumentException;

	/******************* Unicom version ********************/
	public void soapLogDataAnalysisUnicom(File soapLogFile, String soapGwName, Map<String, MessageItem> map)
			throws IOException;

	public Map<String, MessageItem> errLogDataAnalysisUnicom(File errLogFile, String soapGwName)
			throws IOException, ParseException;

	public String updateErr(Map<String, MessageItem> errMap);
}
