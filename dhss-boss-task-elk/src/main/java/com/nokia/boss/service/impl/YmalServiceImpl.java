package com.nokia.boss.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.nokia.boss.bean.IgnoreFile;
import com.nokia.boss.bean.LogBase;
import com.nokia.boss.bean.LogMark;
import com.nokia.boss.service.YmalService;
import com.nokia.boss.settings.CustomSettings;
import com.nokia.boss.task.LoadStaticData;

@Service
public class YmalServiceImpl implements YmalService {
	@Autowired
	private CustomSettings customSettings;
	/**
	 * 重置line，如果找到该soapName，重置行号
	 * 
	 * @param soapName
	 * @param line
	 *            If the line number is Null, the line number is obtained,
	 *            otherwise the line number is reset
	 */
	@Override
	public int saveOrGetLine(String soapName, String fileName, Integer line, int type, boolean isAdd) {
		List<LogBase> list = LoadStaticData.listLogBase;
		int newLine = 0;
		boolean isExistsSoap = false;
		boolean isExistsType = false;
		for (LogBase lb : list) {
			//如果soap和type都找到，则更新行号，如果行号为NULL则不更新
			if (lb.getSoap_name().equals(soapName)) {
				isExistsSoap = true;
				for (LogMark lm : lb.getLogMark()) {
					if (lm.getType() == type) {
						isExistsType = true;
						if (line == null) {
							newLine = lm.getStart_line();
						} else {
							lm.setStart_line(line);
							newLine = line;
						}
						if (fileName != null) {
							lm.setFile_name(fileName);
						}
						break;
					} 
				}
			}
		}
		// 如果soapName不存在，就添加一条soapName记录，如果soap存在但是type不存在则添加一条type记录
		if(isAdd){
			if (!isExistsSoap){
				LogBase lb = new LogBase();
				LogMark lm = new LogMark();
				lm.setFile_name(fileName);
				lm.setStart_line(0);
				lm.setType(type);
				List<LogMark> lmList = new ArrayList<>();
				lmList.add(lm);
				lb.setSoap_name(soapName);
				lb.setLogMark(lmList);
				list.add(lb);
			}
			if(!isExistsType){
				for (LogBase lb : list) {
					if (lb.getSoap_name().equals(soapName)) {
						LogMark lm = new LogMark();
						lm.setFile_name(fileName);
						lm.setStart_line(0);
						lm.setType(type);
						lb.getLogMark().add(lm);
					}
				}
			}
		}
		// 写入文件
		reWriteToYaml(list);
		return newLine;
	}

	/**
	 * 如果没有这个文件就添加进去，否则返回true，表示已经有这个文件;
	 * 
	 * @param soapName
	 * @param fileName
	 * @param analysedTime
	 * @return
	 */
	@Override
	public boolean saveIgnoreFile(String soapName, String fileName, DateTime analysedTime) {
		List<LogBase> list = LoadStaticData.listLogBase;
		boolean isExists = false;
		for (LogBase lb : list) {
			if (lb.getSoap_name().equals(soapName)) {
				if (lb.getIgnoreFile() == null) {
					IgnoreFile iFile = new IgnoreFile();
					iFile.setAnalysed_time(analysedTime);
					iFile.setFile_name(fileName);
					List<IgnoreFile> lgnoreFileList = new ArrayList<IgnoreFile>();
					lb.setIgnoreFile(lgnoreFileList);
					lb.getIgnoreFile().add(iFile);
					// 写入文件
					reWriteToYaml(list);
				} else {
					for (IgnoreFile iFile : lb.getIgnoreFile()) {
						if (iFile.getFile_name().equals(fileName)) {
							isExists = true;
							break;
						}
					}
				}
			}
		}

		return isExists;

	}

	private void reWriteToYaml(List<LogBase> list) {
		try {
			File dumpFile = new File(customSettings.getDefaultConfig().getDataPath());
			Yaml yaml = new Yaml();
			yaml.dump(list, new FileWriter(dumpFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getIgnoreFileCount(String soapName, String fileName) {
		List<LogBase> list = LoadStaticData.listLogBase;
		int size = 0;
		for (LogBase lb : list) {
			if (lb.getSoap_name().equals(soapName)) {
				if (lb.getIgnoreFile() == null) {
					return 0;
				}
				for (IgnoreFile iFile : lb.getIgnoreFile()) {
					if (iFile.getFile_name().equals(fileName)) {
						size++;
					}
				}
			}
		}

		return size;
	}

	@Override
	public void cleanOldIgnoreFile(Integer saveIgnoreDataDay) {
		List<LogBase> list = LoadStaticData.listLogBase;
		DateTime start = DateTime.now();
		start = start.plusDays(-saveIgnoreDataDay);
		boolean isUpdate = false;
		for (LogBase lb : list) {
			if (lb.getIgnoreFile() == null) {
				lb.setIgnoreFile(new ArrayList<IgnoreFile>());
				isUpdate = true;
			} else {
				for (IgnoreFile lf : lb.getIgnoreFile()) {
					if (lf.getAnalysed_time().isBefore(start)) {
						isUpdate = true;
						lb.getIgnoreFile().remove(lf);
					}
				}
			}

		}
		if (isUpdate) {
			// 写入文件
			reWriteToYaml(list);
		}
	}

	@Override
	public void resetMark(int type) {
		List<LogBase> list = LoadStaticData.listLogBase;
		boolean isDelete = false;
		for (LogBase lb : list) {
			for (LogMark lm : lb.getLogMark()) {
				if (lm.getType() == type) {
					lb.getLogMark().remove(lm);
					isDelete = true;
				}
			}
		}
		if (isDelete) {
			// 写入文件
			reWriteToYaml(list);
		}

	}

	@Override
	public void resetAllMark() {
		List<LogBase> list = LoadStaticData.listLogBase;
		for (LogBase lb : list) {
			lb.setLogMark(new ArrayList<LogMark>());
		}
		reWriteToYaml(list);

	}

}
