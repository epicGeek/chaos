package com.nokia.boss.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SyncFile {

	private List<File> soapLogTarget = new ArrayList<File>();
	private List<File> soapLogGZTarget = new ArrayList<File>();
	private List<File> errLogTarget = new ArrayList<File>();
	private List<File> errLogGzTarget = new ArrayList<File>();
	private List<File> errLogLongNameTarget = new ArrayList<File>();
	private List<File> soapFileList = new ArrayList<>();
	private List<File> errFileList = new ArrayList<>();

	public List<File> getSoapFileList() {
		//合并
		Collections.sort(soapLogGZTarget);
		for (File file : soapLogGZTarget) {
			soapFileList.add(file);
		}
		for (File file : soapLogTarget) {
			soapFileList.add(file);
		}
		return soapFileList;
	}

	public void setSoapFileList(List<File> soapFileList) {
		this.soapFileList = soapFileList;
	}

	public List<File> getErrFileList() {
		Map<Integer, File> allErrLogNameMap = new HashMap<>();
		for (File errLogGz : errLogGzTarget) {
			String fileName = errLogGz.getName().replace(".gz", "");
			Integer tailNumber = Integer.valueOf(fileName.split("\\.")[fileName.split("\\.").length - 1]);// tail
																											// number
			allErrLogNameMap.put(tailNumber, errLogGz);
		}
		for (File errLogLongName : errLogLongNameTarget) {
			String fileName = errLogLongName.getName();
			Integer tailNumber = Integer.valueOf(fileName.split("\\.")[fileName.split("\\.").length - 1]);// tail
																											// number
			allErrLogNameMap.put(tailNumber, errLogLongName);
		}
		Set<Integer> keySet = allErrLogNameMap.keySet();
		for (Integer tailNumber : keySet) {
			errFileList.add(allErrLogNameMap.get(tailNumber));
		}
		if (errLogTarget.size() == 1) {
			if (errLogTarget.get(0).getName().equals("BOSS_ERR_CASE.log")
					|| errLogTarget.get(0).getName().equals("CUC_BOSS_ERR_CASE.log")) {
				errFileList.add(errLogTarget.get(0));
			}
		}
		return errFileList;
	}

	public void setErrFileList(List<File> errFileList) {
		this.errFileList = errFileList;
	}

	public List<File> getSoapLogTarget() {
		return soapLogTarget;
	}

	public void setSoapLogTarget(List<File> soapLogTarget) {
		this.soapLogTarget = soapLogTarget;
	}

	public List<File> getSoapLogGZTarget() {
		return soapLogGZTarget;
	}

	public void setSoapLogGZTarget(List<File> soapLogGZTarget) {
		this.soapLogGZTarget = soapLogGZTarget;
	}

	public List<File> getErrLogTarget() {
		return errLogTarget;
	}

	public void setErrLogTarget(List<File> errLogTarget) {
		this.errLogTarget = errLogTarget;
	}

	public List<File> getErrLogGzTarget() {
		return errLogGzTarget;
	}

	public void setErrLogGzTarget(List<File> errLogGzTarget) {
		this.errLogGzTarget = errLogGzTarget;
	}

	public List<File> getErrLogLongNameTarget() {
		return errLogLongNameTarget;
	}

	public void setErrLogLongNameTarget(List<File> errLogLongNameTarget) {
		this.errLogLongNameTarget = errLogLongNameTarget;
	}

}
