package com.nokia.boss.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.nokia.boss.App;
import com.nokia.boss.service.TaskProcService;

/**
 * 
 * @author Pei Nan 这个类的功能是完成数据的实时更新、解析、入库和最新周期的KPI计算。
 *
 */
@Component
@EnableJms
@ConfigurationProperties(prefix = "dhss.boss.task")
public class TaskBoss {
	private static final Logger LOGGER = LogManager.getLogger(TaskBoss.class);
	@Autowired
	TaskProcService taskProcService;
	
	@JmsListener(destination = "BOSS-TASK-CONSUMER")
	public void execBossTask(String message) {
		LOGGER.info(App.getLOGGER_HEAD() + "start");
		try {
			LOGGER.info("Received:" + message);
			JSONObject jsonObj = new JSONObject(message);
			String taskParam = jsonObj.getString("taskParam");
			LOGGER.info(App.getLOGGER_HEAD() + "message received:" + message);
			LOGGER.info(App.getLOGGER_HEAD() + "Task Param is:" + taskParam);
			if (taskParam.equals("start")) {
				taskProcService.executeEntry();
			} else {
				LOGGER.info("Received wrong task param:" + taskParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LOGGER.info(App.getLOGGER_HEAD() + "end");
		}
	}

}