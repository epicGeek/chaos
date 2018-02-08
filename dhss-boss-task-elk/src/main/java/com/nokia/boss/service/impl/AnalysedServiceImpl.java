package com.nokia.boss.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nokia.boss.bean.MessageItem;
import com.nokia.boss.service.AnalysedSerivice;
import com.nokia.boss.service.ElasticSearchBulkService;
import com.nokia.boss.service.YmalService;
import com.nokia.boss.settings.CustomSettings;
import com.nokia.boss.util.AnalyseUtils;

/**
 * 
 * 
 * @author Pei Nan 解析BOSS日志实现类
 */
@Component
public class AnalysedServiceImpl implements AnalysedSerivice {
	private static final Logger LOGGER = LogManager.getLogger(AnalysedServiceImpl.class);
	private static Map<String, String> TASKID_AND_HLRSN_MAP = new HashMap<>();
	@Autowired
	ElasticSearchBulkService esService;
	@Autowired
	YmalService ymalService;

	@Autowired
	CustomSettings customSettings;

	@Override
	public void soapLogDataAnalysis(File soapLogFile, String soapGwName, Map<String, MessageItem> errMap)
			throws IOException {// successLogFileName 变化的文件名，是相对路径
		File successDataFile = AnalyseUtils.extractAnalysisTarget(soapLogFile, soapGwName);// 获取解析目标
		int startLine = ymalService.saveOrGetLine(soapGwName, null, null, 0, false);
		Map<String, Object> analysisTarget = AnalyseUtils.getSoapLogAnalysisTarget(successDataFile, startLine);// 根据起始行，获取需要解析解析日志文本
		int endLine = (int) analysisTarget.get("endLine");// 获取结束行，作为下一个周期的起始行。
		if (startLine > endLine) {
			LOGGER.info("Mark error.Reset this mark to 0");
			startLine = 0;
			ymalService.saveOrGetLine(soapGwName, null, startLine, 0, false);

			analysisTarget = AnalyseUtils.getSoapLogAnalysisTarget(successDataFile, startLine);
		}
		StringBuilder targetText = (StringBuilder) analysisTarget.get("targetText");
		if (targetText.length() == 0) {
			LOGGER.info("NOT ENOUGH SOAP LOG INFO,EXIT PROGRAM..");
			DecimalFormat df = new DecimalFormat("##.##");
			LOGGER.debug("File name:" + soapLogFile.getAbsolutePath());
			LOGGER.debug("File size:" + df.format(soapLogFile.length() / (1024.0 * 1024.0)) + "Mb");
			return;
		}

		AnalyseUtils.originalDataTransformer(targetText, customSettings.getDefaultConfig().getHlrsnTransform(),
				esService, errMap, customSettings);// 把原始日志转换为LOAD数据
		afterLoad("success", soapLogFile.getName(), successDataFile, soapGwName, endLine);// 入库后的处理动作
	}
	@Override
	public Map<String, MessageItem> errLogDataAnalysis(File errLogFile, String soapGwName)
			throws IOException, DocumentException {
		File failureDataFile = AnalyseUtils.extractAnalysisTarget(errLogFile, soapGwName);
		int startLine = getStartLine("failure", soapGwName, errLogFile.getName());
		Map<String, Object> analysisTarget = AnalyseUtils.getErrAnalysisTarget(failureDataFile, startLine);
		int endLine = (int) analysisTarget.get("endLine");
		if (startLine > endLine) {
			LOGGER.info("Mark error.Reset this err mark to 0");
			startLine = 0;
			ymalService.saveOrGetLine(soapGwName, null, 0, 1, false);
			analysisTarget = AnalyseUtils.getErrAnalysisTarget(failureDataFile, startLine);
		}
		StringBuilder targetText = (StringBuilder) analysisTarget.get("targetText");
		if (targetText.length() == 0) {
			DecimalFormat df = new DecimalFormat("##.##");
			LOGGER.info("NOT ENOUGH ERR LOG INFO,EXIT PROGRAM..");
			LOGGER.debug("File name:" + errLogFile.getAbsolutePath());
			LOGGER.debug("File size:" + df.format(errLogFile.length() / (1024.0 * 1024.0)) + "Mb");
			return null;
		}
		Map<String, MessageItem> result = AnalyseUtils.getErrorInfoMap(targetText,
				customSettings.getDefaultConfig().getHlrsnTransform());
		afterLoad("failure", errLogFile.getName(), failureDataFile, soapGwName, endLine);
		return result;

	}

	@Override
	public void soapLogDataAnalysisUnicom(File soapLogFile, String soapGwName, Map<String, MessageItem> map)
			throws IOException {// successLogFileName
		// 变化的文件名，是相对路径
		File successDataFile = AnalyseUtils.extractAnalysisTarget(soapLogFile, soapGwName);// 获取解析目标
		int startLine = getStartLine("success", soapGwName, soapLogFile.getName());// startLine-从日志文件的第几行开始解析
		Map<String, Object> analysisTarget = AnalyseUtils.getSoapLogAnalysisTarget(successDataFile, startLine);// 根据起始行，获取需要解析解析日志文本
		int endLine = (int) analysisTarget.get("endLine");// 获取结束行，作为下一个周期的起始行。
		if (startLine > endLine) {
			LOGGER.info("Mark error.Reset this mark to 0");
			startLine = 0;
			// String sql = "Update soap_mark set start_line = 0 where soap_name
			// = ?";
			// jdbcTemplate.update(sql, soapGwName);
			ymalService.saveOrGetLine(soapGwName, null, 0, 0, false);
			analysisTarget = AnalyseUtils.getSoapLogAnalysisTarget(successDataFile, startLine);
		}
		StringBuilder targetText = (StringBuilder) analysisTarget.get("targetText");
		if (targetText.length() == 0) {
			LOGGER.info("NOT ENOUGH SOAP LOG INFO,EXIT PROGRAM..");
			DecimalFormat df = new DecimalFormat("##.##");
			LOGGER.info("File name:" + soapLogFile.getAbsolutePath());
			LOGGER.info("File size:" + df.format(soapLogFile.length() / (1024.0 * 1024.0)) + "Mb");
			return;
		}

		AnalyseUtils.originalDataTransformerUnicom(targetText, esService, map, customSettings);// 把原始日志转换为LOAD数据
		afterLoad("success", soapLogFile.getName(), successDataFile, soapGwName, endLine);// 入库后的处理动作
	}

	@Override
	public Map<String, MessageItem> errLogDataAnalysisUnicom(File errLogFile, String soapGwName)
			throws IOException, ParseException {
		File failureDataFile = AnalyseUtils.extractAnalysisTarget(errLogFile, soapGwName);
		int startLine = getStartLine("failure", soapGwName, errLogFile.getName());
		Map<String, Object> analysisTarget = AnalyseUtils.getErrAnalysisTarget(failureDataFile, startLine);
		int endLine = (int) analysisTarget.get("endLine");
		StringBuilder targetText = (StringBuilder) analysisTarget.get("targetText");
		if (startLine > endLine) {
			LOGGER.info("Mark error.Reset this err mark to 0");
			startLine = 0;
			ymalService.saveOrGetLine(soapGwName, null, startLine, 1, false);
			analysisTarget = AnalyseUtils.getErrAnalysisTarget(failureDataFile, startLine);

		}
		if (targetText.length() == 0) {
			DecimalFormat df = new DecimalFormat("##.##");
			LOGGER.info("NOT ENOUGH ERR LOG INFO,EXIT PROGRAM..");
			LOGGER.debug("File name:" + errLogFile.getAbsolutePath());
			LOGGER.debug("File size:" + df.format(errLogFile.length() / (1024.0 * 1024.0)) + "Mb");
			return null;
		}
		// HANDLE ERR TARGET
		LOGGER.debug("TASK ID AND HLRSN MAP SIZE:" + TASKID_AND_HLRSN_MAP.size());
		afterLoad("failure", errLogFile.getName(), failureDataFile, soapGwName, endLine);
		Map<String, MessageItem> result = AnalyseUtils.errCaseCucLogAnalyser(targetText, TASKID_AND_HLRSN_MAP);
		return result;

	}

	/**
	 * 
	 * @param logType
	 * @param filePureName
	 * @param analysedFile
	 * @param soapName
	 * @param startLine
	 */
	private void afterLoad(String logType, String filePureName, File analysedFile, String soapName, int startLine) {

		// 文件变化有三种情况，根据不同的情况来处理MARK逻辑
		// 1.如果解析的是GZ文件，应该重置MARK为0，并删除转换为文本后的文件（./cache/*.trans），并且将该文件名字加入IGNORE列表
		// 2.如果解析的不是GZ文件，但是文件名字含有今日时间戳，应该是每天最后一个文件。因为没有写到一定的大小，所以不打包。
		// 这种情况应该重置MARK，把文件添加到IGNORE列表，并且文件名+“.GZ”也添加到IGNORE列表
		// 3.如果文件名没改，更新MARK即可。
		// 参数STARTLINE实际上是解析后得到的ENDLINE，变为下一个周期的STARTLINE
		if (filePureName.contains(".gz")) { // ALL GZ
			afterGzAnalysed(logType, filePureName, soapName);
		} else {// NOT GZ
			if (logType.equalsIgnoreCase("failure")) {
				if (filePureName.endsWith("BOSS_ERR_CASE.log")) {
					LOGGER.debug(filePureName + " is still being wriring in.Update mark.");
					ymalService.saveOrGetLine(soapName, filePureName, startLine, 1, false);
					LOGGER.debug("Update error mark as:err_mark = " + startLine + ",soap name = " + soapName
							+ ", file name= " + filePureName);
				} else {
					LOGGER.debug(filePureName + " is not a continuesly writing in file,reset mark.");
					lastFileNotCompressed(logType, filePureName, soapName);
				}
			} else if (logType.equalsIgnoreCase("success")) {
				if (logType.equalsIgnoreCase("success")) {
					ymalService.saveOrGetLine(soapName, filePureName, startLine, 0, false);
					LOGGER.debug("Update soap mark as:soap_mark = " + startLine + ",soap name = " + soapName
							+ ", file name= " + filePureName);

				}
			}
		}
	}

	/**
	 * 处理的文件没压缩，但是不写入数据了 加入两条数据到IGNORE列表 本名和本命+".gz"
	 * 
	 * @param logType
	 * @param filePureName
	 * @param soapName
	 */
	public void lastFileNotCompressed(String logType, String filePureName, String soapName) {
		afterGzAnalysed(logType, filePureName, soapName);
		ymalService.saveIgnoreFile(soapName, filePureName + ".gz", DateTime.now());
		LOGGER.info("Add a pair of new ignore record:");
		LOGGER.debug("File name:");
		LOGGER.debug(filePureName + " AND " + filePureName + ".gz" + ",soap name:" + soapName);

	}

	/**
	 * gz文件解析之后的动作
	 * 
	 * @param logType
	 * @param filePureName
	 * @param analysedFile
	 * @param soapName
	 */
	public void afterGzAnalysed(String logType, String filePureName, String soapName) {
		if (logType.equalsIgnoreCase("success")) {
			// 重置MARK的行数为0
			ymalService.saveOrGetLine(soapName, "waiting next soap file", 0, 0, false);
			LOGGER.debug("soap_mark for:{}  has been reset to 0.", soapName);
		} else if (logType.equalsIgnoreCase("failure")) {
			ymalService.saveOrGetLine(soapName, "waiting next err file", 0, 1, false);
			LOGGER.debug("err_mark for:{} has been reset to 0.", soapName);
		}
		// 增加到IGNORE列表
		ymalService.saveIgnoreFile(soapName, filePureName, DateTime.now());

		LOGGER.info("A new file has been added to ignore file list:");
		LOGGER.info("File name:{},SOAP GW name:{}", filePureName, soapName);
	}

	/**
	 * BOSS MARK机制： 首先要知道，每套日志的数据都是唯一的写入一个文件里，直到它写满或者到了第二天。
	 * 也就是说，不符合文件名匹配规范的变化文件，全都无视，不会解析。
	 * 根据SOAP的名字来确定MARK的唯一性，所以正常来讲，每个MARK表里，同一台SOAP上的日志记录，有且只有一个（程序强制规定）
	 * 每台SOAP上BOSS日志有两套，BOSS_SOAP(下发指令日志)，BOSS_ERR_CASE.LOG（业务错误日志）
	 * //联通是另一套BOSS，机制差不多，文件名不一样
	 * SOAP_MARK存BOSS_SOAP日志的MARK，ERR_MARK存BOSS_ERR的MARK
	 * 如果监测到文件没有被压缩为GZ，只更新MARK字段即可。 如果文件被压缩为GZ，删除这条MARK记录
	 * 
	 * @param logType
	 * @return start line
	 */
	private int getStartLine(String logType, String soapGwName, String fileName) {
		int startLine = 0;
		if (logType.equalsIgnoreCase("failure")) {
			startLine = ymalService.saveOrGetLine(soapGwName, fileName, null, 1, false);

		} else if (logType.equalsIgnoreCase("success")) {
			startLine = ymalService.saveOrGetLine(soapGwName, fileName, null, 0, false);
		}
		return startLine;
	}
	@Override
	public String updateErr(Map<String, MessageItem> errMap) {
		esService.updateErr(errMap);
		return "success";
	}

}
