package com.nokia.boss.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;

import com.nokia.boss.bean.DefaultConfig;
import com.nokia.boss.bean.MessageItem;
import com.nokia.boss.bean.SoapGwLogin;
import com.nokia.boss.bean.SyncFile;
import com.nokia.boss.service.AnalysedSerivice;
import com.nokia.boss.service.YmalService;
import com.nokia.boss.task.LoadStaticData;

public class SyncCallable {
	private SoapGwLogin soapGwLogin;
	private DefaultConfig defaultConfig;
	private YmalService ymalService;
	private AnalysedSerivice analysedSerivice;
	private static final Logger LOGGER = LogManager.getLogger(SyncCallable.class);

	public SyncCallable(SoapGwLogin soapGwLogin, DefaultConfig defaultConfig, YmalService ymalService,
			AnalysedSerivice analysedSerivice) {
		this.soapGwLogin = soapGwLogin;
		this.defaultConfig = defaultConfig;
		this.ymalService = ymalService;
		this.analysedSerivice = analysedSerivice;
	}

	public String syncFile() {
		List<String> fileList = new ArrayList<String>();

		String soapName = soapGwLogin.getSoapGwName();
		String rsyncDir = defaultConfig.getRsyncDataDir() + soapName + "/";
		String rsynCmd = defaultConfig.getRsyncCmd();
		String soapPassword = soapGwLogin.getPassword();
		String errRulePattern = defaultConfig.getRuleFileAbsErrPath();
		String soapRulePattern = defaultConfig.getRuleFileAbsSoapPath();
		String userName = soapGwLogin.getUserName();
		String soapIp = soapGwLogin.getSoapgwIp();
		// 此处裴楠反馈docker调用外部shell对部署不友好，建议在调用时新创建shell，在调用时写入该文件，调用完成后删除。
		String[] arr = { soapPassword, errRulePattern, errRulePattern, userName, soapIp, rsyncDir };
		String command = extracted(rsynCmd, arr);
		// String cmdPattern ="sshpass -p #password#";
		// 同步命令下发后，只能取当天的日志信息。
		LOGGER.info("Rsyn err log command:" + command);
		try {
			List<String> errFileList = FileProcUtils.exec(soapName, command);
			if (errFileList.size() > 0) {
				fileList.addAll(errFileList);
			}
			arr = new String[] { soapPassword, soapRulePattern, soapRulePattern, userName, soapIp, rsyncDir };
			command = extracted(rsynCmd, arr);
			LOGGER.info("Rsyn soap log command:" + command);
			List<String> soapfileList = FileProcUtils.exec(soapName, command);

			if (soapfileList.size() > 0) {
				fileList.addAll(soapfileList);
			}

			if (fileList.size() > 0) {
				bossHandlingStream(fileList);
				LOGGER.info("BOSS data analysis complete!");
				return "Sync-file-success:" + soapName;
			}
			return "Sync-file-not-found:" + soapName + ",file list is empty.";
		} catch (Exception e) {
			return "Sync-file-error:" + soapName + ",ErrorMessage:" + e.getMessage();
		}

	}

	private static String extracted(String rsynCmd, Object[] arr) {

		return MessageFormat.format(rsynCmd, arr);
	}

	/**
	 * 移动文件变化规则 左边是持续写入文件，右面是打包文件：
	 * 指令日志：BOSS_SOAP_Agent_BOSSA_main_yyyy-MM-dd.HH.mm.ss-SSS ->
	 * BOSS_SOAP_Agent_BOSSA_main_yyyy-MM-dd.HH.mm.ss-SSS.gz（写满到一定大小打包）
	 * 日期是创建文件的时间。 错误日志: BOSS_ERR_CASE.log ->
	 * BOSS_ERR_CASE.log.2016-10-01.45.gz(写满到一定大小打包)
	 * 
	 * 每天可能会产生几个写不满的文件，到23:59会改名，但不打包。
	 * 
	 * @throws ParseException
	 * 
	 */
	private void bossHandlingStream(List<String> changedFiles) throws IOException, DocumentException, ParseException {
		String soapName = soapGwLogin.getSoapGwName();
		LOGGER.info("Boss version:" + defaultConfig.getBossVersion() + ",SOAP-GW-NAME:" + soapName);
		// 现在，changedFiles里面存的是所有发生变动的文件名，可能包括backup路径。
		SyncFile sFile = new SyncFile();
		selectNotIgnoreTargetFile(sFile, changedFiles, soapName);
		// 应该根据文件的最后修改日期排序文件的顺序。先解析较旧的日志（GZ包），再解析持续写入的文件
		// 由于文件名称的问题，错误日志文件是优先被同步下来的
		checkMarkRecord();
		executeAnalysis(sFile);
	}

	/**
	 * 执行同步流程。
	 * 
	 * @param bossVersion
	 * @param soapLoginInfoMap
	 * @param analysisTargetMap
	 * @throws IOException
	 * @throws DocumentException
	 * @throws ParseException
	 */
	private void executeAnalysis(SyncFile sFile) throws IOException, DocumentException, ParseException {
		List<File> errFileList = sFile.getErrFileList();
		List<File> soapFileList = sFile.getSoapFileList();
		LOGGER.info("ANALYSIS ORDER:");
		for (File file : soapFileList) {
			LOGGER.info(file.getAbsolutePath());
		}
		for (File file : errFileList) {
			LOGGER.info(file.getAbsolutePath());
		}
		Map<String, MessageItem> errMap = null;
		// 先解析ERR，后解析SOAP
		for (File errFile : errFileList) {
			LOGGER.info("ANALYSIS TARGET:" + errFile.getName());
			if (LoadStaticData.isCMCC()) {
				try {
					errMap = analysedSerivice.errLogDataAnalysis(errFile, soapGwLogin.getSoapGwName());

				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.debug(e.getMessage());
				}

			} else {
				try {
					errMap = analysedSerivice.errLogDataAnalysisUnicom(errFile, soapGwLogin.getSoapGwName());
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.debug(e.getMessage());
				}
			}

		}
		for (File soapFile : soapFileList) {
			LOGGER.info("ANALYSIS TARGET:" + soapFile.getName());
			if (LoadStaticData.isCMCC()) {
				try {
					analysedSerivice.soapLogDataAnalysis(soapFile, soapGwLogin.getSoapGwName(), errMap);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}

			} else {
				try {
					analysedSerivice.soapLogDataAnalysisUnicom(soapFile, soapGwLogin.getSoapGwName(), errMap);
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error(e.getMessage());
				}

			}
		}
		if (errMap.size() > 0) {
			String message = analysedSerivice.updateErr(errMap);
			LOGGER.info("update error log:{}", message);
		}

	}

	/**
	 * 筛选出没有被Ignore的文件
	 * 
	 * @param changedFiles
	 * @param soapName
	 * @return
	 */
	private void selectNotIgnoreTargetFile(SyncFile sFile, List<String> changedFiles, String soapName) {
		LOGGER.info("***** REMOTE SYNC FILE LIST SIZE:" + changedFiles.size() + " *****");
		for (String changedFile : changedFiles) {
			String pureName = changedFile.replace("/backup/", "");
			LOGGER.debug("According to rsync command call back info");// 被改变的文件
			LOGGER.debug("CHANGED FILE:" + changedFile + ",PURE NAME:" + pureName);// 被改变的文件
			// ignore表里存的文件名都是纯文件名，不含路径
			int fileSize = ymalService.getIgnoreFileCount(soapName, pureName);
			if (fileSize == 0) {
				// 如果查不到，说明没添加到IGNORE列表
				File fileNeedToAnalysis = new File(
						defaultConfig.getRsyncDataDir() + soapName + "/" + changedFile.replace(" ", ""));
				if (fileNeedToAnalysis.getName().contains("BOSS_ERR_CASE.log")) {
					// ERR_CASE的LOG
					if (fileNeedToAnalysis.getName().contains(".gz")) {// LOG的GZ包，一定包含时间
						sFile.getErrLogGzTarget().add(fileNeedToAnalysis);
					} else {// 不是GZ包
						if (fileNeedToAnalysis.getName().equals("BOSS_ERR_CASE.log")
								|| fileNeedToAnalysis.getName().equals("CUC_BOSS_ERR_CASE.log")) {// 正在写入的文件
							sFile.getErrLogTarget().add(fileNeedToAnalysis);
						} else {// 不是GZ包，不是正在写入的文件
							sFile.getErrLogLongNameTarget().add(fileNeedToAnalysis);
						}
					}
				} else if (fileNeedToAnalysis.getName().contains("BOSS_SOAP_Agent")
						|| fileNeedToAnalysis.getName().contains("CUC_Telnet_Agent")) {
					if (fileNeedToAnalysis.getName().contains(".gz")) {
						sFile.getSoapLogGZTarget().add(fileNeedToAnalysis);
					} else {
						sFile.getSoapLogTarget().add(fileNeedToAnalysis);
					}
				}
				LOGGER.info("File should be analysed:" + fileNeedToAnalysis.getAbsolutePath());

			} else {
				LOGGER.info("File name:" + pureName + " in ingore_file");
				LOGGER.info(changedFile + " IS IGNORED.");
			}
		}
	}

	public void checkMarkRecord() {
		// 如果当前soap记录不存在就创建
		ymalService.saveOrGetLine(soapGwLogin.getSoapGwName(), "waiting first err case file", null, 0, true);
		ymalService.saveOrGetLine(soapGwLogin.getSoapGwName(), "waiting first soap case file", null, 1, true);
	}
}
