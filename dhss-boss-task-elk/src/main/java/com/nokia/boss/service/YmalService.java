package com.nokia.boss.service;

import org.joda.time.DateTime;

public interface YmalService {
	/**
	 * 如果传入的line不为null则根据条件重置行号，否则返回行号，如果没有找到则返回0
	 * 
	 * @param soapName
	 * @param fileName  If the file name is not empty, it is changed to a new file name, otherwise it is not modified
	 * @param line   If the line number is empty, the line number is obtained, otherwise the line number is reset
	 * @param isAdd If the value is true, add a data to the Yaml
	 */
	public int saveOrGetLine(String soapName, String fileName,Integer line, int type,boolean isAdd) ;
   /**
    * 保存数据到忽略文件列表
    * @param soapName
    * @param fileName
    * @param analysedTime
    * @return
    */
	public boolean saveIgnoreFile(String soapName, String fileName, DateTime analysedTime);
    /**
     * 获取符合提交件的文件个数
     * @param soapName
     * @param fileName
     * @return
     */
	public int getIgnoreFileCount(String soapName, String fileName);
    /**
     * 清理旧数据
     * @param saveIgnoreDataDay
     */
	public void cleanOldIgnoreFile(Integer saveIgnoreDataDay);
    /**
     * 重置mark信息
     * @param type
     */
	public void resetMark(int type);
	/**
	 * 重置所有mark
	 */
	public void resetAllMark();
  
}
