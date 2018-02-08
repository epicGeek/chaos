package com.nokia.ices.app.dhss.consumer;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.core.utils.JsonMapper;
import com.nokia.ices.app.dhss.domain.command.CommandCheckItem;
import com.nokia.ices.app.dhss.domain.equipment.EquipmentUnit;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.domain.smart.SmartCheckScheduleResult;
import com.nokia.ices.app.dhss.service.TaskService;

@Component
public class TaskConsumer {
	private final static Logger logger = LogManager.getLogger(CmdConsumer.class);
	
	@Autowired
	private TaskService taskService;
	
	@SuppressWarnings("unused")
	private final static String SMARTID = "smartId";
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	

	@SuppressWarnings({ "unchecked"})
	@JmsListener(destination = "smart-task-consumer", containerFactory = "jmsContainerFactory")
	public void message(Message message){
		String moduleStr = "";
		 
		try {
			TextMessage messages = (TextMessage) message;
			String msgBody =  messages.getText();
			Map<String, String> mapBody = (Map<String, String>) new JsonMapper().fromJson(msgBody, Map.class);
			
			moduleStr  = mapBody.get("taskName") + "|" + mapBody.get("serviceName") + "|";
			
			logger.info(moduleStr+"start");
			logger.info(moduleStr+"msgBody  --- ：msgBody:{}", msgBody);
			
			SmartCheckJob job = taskService.findSmartCheckJob(mapBody.get("serviceName"));
			
			if( job != null ){
				
				//获取任务的所有单元
				Set<EquipmentUnit> equipmentUnitSet = taskService.findListBySmartCheckJob(job);
				logger.info(moduleStr+"unit set:{}",new JsonMapper().toJson(equipmentUnitSet));
				//获取任务的所有指令
				Set<CommandCheckItem> checkItemSet = taskService.findSetBySmartCheckJob(job);
				logger.info(moduleStr+"check set:{}",new JsonMapper().toJson(checkItemSet));
				
				if(equipmentUnitSet.size() == 0 || checkItemSet.size() == 0){
					logger.info(moduleStr+"no unit or check");
				}else{
					//保存一条执行任务的历史记录
					SmartCheckScheduleResult SmartCheckSchedule = taskService.saveSmartCheckSchedule(job, equipmentUnitSet.size());
					logger.info(moduleStr+"save SmartCheckScheduleResult:{}",new JsonMapper().toJson(SmartCheckSchedule));
					//执行任务
					taskService.execJob(SmartCheckSchedule, equipmentUnitSet, checkItemSet,moduleStr);
					
					//修改当前任务的下次执行时间
					String nextime = mapBody.get("nextime");
					String currtime = mapBody.get("currTime");
					try {
						job.setExecDay(sdf.parse(currtime+":00"));
						job.setNextDay(sdf.parse(nextime+":00"));
						logger.info(moduleStr+"update job : {},nextTime:{}", job.getJobName(),job.getNextDay());
						taskService.saveJobNextDate(job);
						
					} catch (ParseException e) {
						e.printStackTrace();
						logger.error(moduleStr+e.getMessage());
					}
					
					
				}
				logger.info(moduleStr+"end");
			}else{
				logger.warn(moduleStr+"end");
			}
			
			
		} catch (JMSException e) {
			logger.error(moduleStr+e.getMessage());
		}
		
		taskService.deleteSmartCheckTempData();
	}
	

}












