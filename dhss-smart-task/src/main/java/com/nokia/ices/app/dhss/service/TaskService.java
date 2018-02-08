package com.nokia.ices.app.dhss.service;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import com.nokia.ices.app.dhss.domain.alarm.AlarmMonitor;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResult;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckResultTmp;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;

public interface TaskService {
	
	/**
	 * 根据Name查询要执行的任务信息
	 * @param id
	 * @return
	 */
	public SmartCheckJob findSmartCheckJob(String name);
	
	/**
	 * 根据任务查询所有单元
	 * @param smartCheckJob
	 * @return
	 */
	public Set<EquipmentUnit> findListBySmartCheckJob(SmartCheckJob smartCheckJob);
	
	/**
	 * 根据任务查询所有指令
	 * @param smartCheckJob
	 * @return
	 */
	public Set<CommandCheckItem> findSetBySmartCheckJob(SmartCheckJob smartCheckJob);
	
	/**
	 * 保存执行任务的历史记录
	 * @param smartCheckScheduleResult
	 * @return
	 */
	public SmartCheckScheduleResult saveSmartCheckSchedule(SmartCheckJob job,int size);
	
	
	/**
	 * 执行任务
	 * @param smartCheckScheduleResult
	 * @param equipmentUnitSet
	 * @param checkItemSet
	 */
	public void execJob(SmartCheckScheduleResult smartCheckScheduleResult,Set<EquipmentUnit> equipmentUnitSet,Set<CommandCheckItem> checkItemSet,String moduleStr);
	
	/**
	 * 修改任务下次执行时间
	 * @param jobType
	 * @param startDateTime
	 * @return
	 */
	public  SmartCheckJob getNextExecuteTime(SmartCheckJob job)throws ParseException;
	
	/**
	 * 根据uuid查询临时记录
	 * @param session
	 * @return
	 */
	public SmartCheckResultTmp getSmartCheckResultTmpByUUID(Map<String,Object> session);
	
	/**
	 * 执行指令成功，保存临时记录，继续发给scriptserver
	 * @param smartCheckResultTmp
	 * @return
	 */
	public SmartCheckResultTmp saveSmartCheckResultTmp(SmartCheckResultTmp smartCheckResultTmp);
	
	/**
	 * 根据ID查询任务记录
	 * @return
	 */
	public SmartCheckScheduleResult getSmartCheckScheduleResultById(Long id);
	
	/**
	 * 保存、修改任务历史记录
	 * @param smartCheckScheduleResult
	 * @return
	 */
	public SmartCheckScheduleResult saveSmartCheckScheduleResult(SmartCheckScheduleResult smartCheckScheduleResult);
	
	/**
	 * 指令执行异常添加告警
	 * @param monitor
	 * @return
	 */
	public AlarmMonitor saveAlarmMonitor(AlarmMonitor monitor);
	
	/**
	 * 保存一条执行结果
	 * @return
	 */
	public SmartCheckResult saveSmartCheckResult(SmartCheckResult smartCheckResult);

	SmartCheckJob saveJobNextDate(SmartCheckJob job);
	
	public Integer findSmartCheckResultErrorSize(Long id);
	
	
	public boolean deleteSmartCheckTempData();
	

}
