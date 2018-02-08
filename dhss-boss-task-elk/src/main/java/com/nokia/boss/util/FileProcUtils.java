package com.nokia.boss.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.nokia.boss.task.LoadStaticData;

public class FileProcUtils {
	private static final Logger LOGGER = LogManager.getLogger(FileProcUtils.class);

	private static final String BOSS_SOAP_AGENT = "BOSS_SOAP_Agent";
	private static final String BOSS_ERR_CASE_LOG = "BOSS_ERR_CASE.log";
	private static final String CUC_TELNET_AGENT = "CUC_Telnet_Agent";
	private static final String CUC_BOSS_ERR_CASE_LOG = "CUC_BOSS_ERR_CASE.log";
	private static final String DELETING = "deleting";
	private static final String BACKUP = "backup";
	public static final String DATE_YY_MM_DD = "yyyy-MM-dd";
	public static SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_YY_MM_DD);

	private static File createShell(String soapName, String command) throws IOException {
		// 将command写入临时文件
		// 这个路径在项目启动时已经创建了，此处只需要创建文件
		String shellPath = LoadStaticData.getRSYNC_DATA_DIR() + "/" + soapName + "/" + soapName + ".sh";
		File file = new File(shellPath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		fw.write(command);
		fw.close();
		return file;
	}
	public static File createShell(String content) throws IOException {
		// 将command写入临时文件
		// 这个路径在项目启动时已经创建了，此处只需要创建文件
		String shellPath = LoadStaticData.getRSYNC_DATA_DIR() + "/delete.sh";
		File file = new File(shellPath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.close();
		return file;
	}
	public static List<String> exec(String soapName, String command) {
		InputStreamReader stdISR = null;
		InputStreamReader errISR = null;
		Process process = null;
		List<String> changedFiles = new ArrayList<>();
		try {
			File shellFile = createShell(soapName, command);
			process = Runtime.getRuntime().exec("sh " + shellFile.getAbsolutePath());
			process.waitFor();
			String line = null;
			stdISR = new InputStreamReader(process.getInputStream());
			BufferedReader stdBR = new BufferedReader(stdISR);
			while ((line = stdBR.readLine()) != null) {
				if (line.contains(BOSS_SOAP_AGENT) || line.contains(BOSS_ERR_CASE_LOG)// cmcc
						|| line.contains(CUC_TELNET_AGENT) || line.contains(CUC_BOSS_ERR_CASE_LOG)) { // UNICOM
					if (!line.contains(DELETING)) { // 参数存在时，被删除的文件也会存在于命令返回信息里，需要去除
						if (line.contains(BACKUP)) {
							changedFiles.add("/" + line);// 如果包含文件夹名称，前面没有'/',拼一下
						} else {
							changedFiles.add(line);
						}
					}
				}
				if (line.contains("sent") || line.contains("total")) {
					LOGGER.info("Transport info:->" + line);
				}
			}

			errISR = new InputStreamReader(process.getErrorStream());
			BufferedReader errBR = new BufferedReader(errISR);
			while ((line = errBR.readLine()) != null) {
				if (!StringUtils.isEmpty(line.replaceAll(" ", ""))) {
					LOGGER.info("Execute command in:{}:ERR line:", soapName, line);
				}

			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stdISR != null) {
					stdISR.close();
				}
				if (errISR != null) {
					errISR.close();
				}
				if (process != null) {
					process.destroy();
				}
			} catch (IOException e) {
				LOGGER.error("Excute command:{} with error:{}", command, e.getMessage());
			}
		}
		return changedFiles;
	}

	public static void writePatternFile(String patternFileStr, int type) throws IOException, FileNotFoundException {
		FileReader fr = new FileReader(new File(patternFileStr));
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		StringBuilder patternText = new StringBuilder();
		while ((line = br.readLine()) != null) {
			patternText.append(line + "\n");
		}
		br.close();
		fr.close();
		String patternStr = patternText.toString().trim();
		if (patternStr.contains("#static")) {
			LOGGER.info("Pattern file is set to static mode.{}", patternStr);
			return;
		}
		String todayStr = DateUtils.getTargetTime().get("TODAY");
		String todayPattern = "";
		if (type == 0) {
			todayPattern = LoadStaticData.getCURR_SOAP_PATTERN().replace(DATE_YY_MM_DD, todayStr);
		} else {
			todayPattern = LoadStaticData.getCURR_ERR_PATTERN().replace(DATE_YY_MM_DD, todayStr);
		}

		if (!patternStr.equals(todayPattern)) {
			FileWriter fw = new FileWriter(patternFileStr);
			fw.write(todayPattern);
			fw.close();
			FileReader fr_ = new FileReader(patternFileStr);
			BufferedReader br_ = new BufferedReader(fr_);
			String line_ = null;
			LOGGER.info("Pattern rule file has been changed as:");
			while ((line_ = br_.readLine()) != null) {
				LOGGER.info(line_);
			}
			br_.close();
		} else {
			LOGGER.info("Pattern rule is correct.");
			FileReader fr__ = new FileReader(patternFileStr);
			BufferedReader br__ = new BufferedReader(fr__);
			String line__ = null;
			while ((line__ = br__.readLine()) != null) {
				LOGGER.info(line__);
			}
			br__.close();
		}
	}

	public static void deleteOldRsynDir(List<File> rsyncDirList, int day) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, -day);
		String yesterdayStr = sdfDate.format(now.getTime());
		LOGGER.info("Delete All Rsync Dir cache:");
		for (File rsyncDir : rsyncDirList) {
			LOGGER.info("Rsync Dir:" + rsyncDir.getAbsolutePath());
			File[] allFiles = rsyncDir.listFiles();
			for (File singleRsyncFile : allFiles) {
				try {
					if (singleRsyncFile.getName().contains(yesterdayStr)) {
						LOGGER.info("DELETE:" + singleRsyncFile.getAbsolutePath());
						singleRsyncFile.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
