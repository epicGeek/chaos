package com.nokia.ices.app.dhss.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.ems.EmsCheckJob;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitor;
import com.nokia.ices.app.dhss.domain.ems.EmsMonitorHistory;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;

public interface TaskService {
	
	/**
	 * 根据id查询对应任务信息
	 * @param id
	 * @return
	 */
	public EmsCheckJob findEmsCheckJobById(Long id);

	/**
	 * 执行任务
	 * @param emsCheckJob
	 */
	public void execEmsJob(EmsCheckJob emsCheckJob);

	/**
	 * 获取单元信息
	 * @param map
	 * @return
	 */
	public List<EquipmentUnit> findUnits(Map<String, Object> map);

	/**
	 * 获取指令信息
	 * @param map
	 * @return
	 */
	public List<CommandCheckItem> findCommands(Map<String, Object> map);

	
	/**
	 * 修改任务下次执行时间
	 * @param emsCheckJob
	 * @throws ParseException
	 */
	public void updateJobExecTime(EmsCheckJob emsCheckJob) throws ParseException;

	/**
	 * 将指令执行结果发送到scriptscerver
	 * @param json
	 * @param equipmentUnit
	 * @param commandCheckItem
	 */
	public void sendMessageService(Map<String, Object> json, EquipmentUnit equipmentUnit, CommandCheckItem commandCheckItem);

	/**
	 * 查询历史记录
	 * @param map
	 * @return
	 */
	public List<EmsMonitor> findEmsMonitors(Map<String, Object> map);

	/**
	 * 发送短信逻辑
	 * @param groupId
	 * @param message
	 * @param unit
	 * @param item
	 * @param msg
	 * @param flag
	 * @param command
	 */
	public void noticeGroup(String groupId, String message, String unit, String item, String msg, boolean flag,
			String command);

	/**
	 * 检查是否重复
	 * @param unit
	 * @param item
	 * @return
	 */
	public int isNotCancel(String unit, String item);

	/**
	 * 保存执行结果
	 * @param emsMonitor
	 */
	public void saveEmsMonitor(EmsMonitor emsMonitor);

	/**
	 * 发送短信
	 * @param moblie
	 * @param smscontent
	 */
	public void sendMessageSms(String moblie, String smscontent);

	/**
	 * 添加执行历史记录
	 * @param emsMonitorHistory
	 */
	public void saveEmsMonitorHistory(EmsMonitorHistory emsMonitorHistory);

}
