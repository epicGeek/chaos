package com.nokia.ices.app.dhss.kpi.task;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.App;
import com.nokia.ices.app.dhss.domain.kpi.KpiConfig;
import com.nokia.ices.app.dhss.kpi.config.CustomSettings;
import com.nokia.ices.app.dhss.kpi.service.KpiTaskService;


@EnableJms
@Component
@EnableScheduling
public class TaskKPI {
	private static Map<String, Map<String, Object>> equipmentUnitMap = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());
	private static Map<String, Map<String, Object>> equipmentUnitMapUnitNameKey = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());
	private static Map<String, Map<String, Object>> equipmentNeMap = Collections
			.synchronizedMap(new HashMap<String, Map<String, Object>>());

	private static final SimpleDateFormat standardTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	private static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
	private Logger LOGGER = LogManager.getLogger(TaskKPI.class);
	@Autowired
	private KpiTaskService kpiTaskService;
	@Autowired
	private CustomSettings customSettings;


	@Scheduled(cron = "${dhss.kpi.test-conn-cron-exp}")
	public void refreshDatabaseLink() {
		Boolean isAllDatabaseAvailable = kpiTaskService.connectVertify();
		if(isAllDatabaseAvailable){
			LOGGER.info("All DB is available.");
		}else{
			LOGGER.info("Not all DB is available.Please check.");
		}
	}

	//@Scheduled(cron = "0 25 * * * ?")
	public void execKPITask() {
		Long startTimeStamp = System.currentTimeMillis();
		String startTime = new DateTime(startTimeStamp).toString(standardTimeFormat.toPattern());
		LOGGER.info(startTime);
		// load 网元信息缓存
		equipmentUnitMap.clear();
		equipmentUnitMapUnitNameKey.clear();

		List<Map<String,Object>> equipmentUnitMapList = kpiTaskService.getEquipmentUnitMapList();
		equipmentUnitMapList.forEach(unitItem -> {
			equipmentUnitMap.put(unitItem.get("co_gid").toString(), unitItem);
			equipmentUnitMapUnitNameKey.put(unitItem.get("unit_name").toString(), unitItem);
		});
		LOGGER.info("equipmentUnitMap is updated,size =" + equipmentUnitMap.size());
		equipmentNeMap.clear();
		equipmentNeMap = kpiTaskService.getEquipmentNeMap();
		LOGGER.info("equipmentNeMap is updated,size =" + equipmentNeMap.size());
		// load 网元信息缓存结束
		List<KpiConfig> kpiDefineList = kpiTaskService.getKpiConfigList();
		LOGGER.info("Kpi congig count:"+kpiDefineList.size());
		Map<String, Integer> kpiDataMap = kpiTaskService.loadDataByKpiDefineList(kpiDefineList);
		
		kpiDataMap.forEach((key, value) -> {
			LOGGER.info(key + " -> " + value);
		});
		Long endTimeStamp = System.currentTimeMillis();
		Long periodTime = (endTimeStamp - startTimeStamp);
		String endTime = new DateTime(endTimeStamp).toString(standardTimeFormat.toPattern());
		LOGGER.info(endTime + " Cost:" + periodTime);

	}

	
	@JmsListener(destination = "KPI-TASK-CONSUMER")	
	public void execKPITask(String message) {
		LOGGER.info(App.getLOGGER_HEAD() + "start");
		try {
			LOGGER.info("Received:"+message);
			JSONObject jsonObj=new JSONObject(message);
			String taskParam = jsonObj.getString("taskParam");
			LOGGER.info(App.getLOGGER_HEAD()+"message received:"+message);
			LOGGER.info(App.getLOGGER_HEAD()+"Task Param is:"+taskParam);
			if(taskParam.equals("start")){
				Integer saveMonth = customSettings.getHoldKpiDataMonth();
				LOGGER.info("All data is save for "+ saveMonth + " months!");
				kpiTaskService.deleteOldData();
				execKPITask();
			}else{
				LOGGER.info("Received wrong task param:"+taskParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			LOGGER.info(App.getLOGGER_HEAD() + "end");
		}
	}
	public static Map<String, Map<String, Object>> getEquipmentUnitMapUnitNameKey() {
		return equipmentUnitMapUnitNameKey;
	}

	public static void setEquipmentUnitMapUnitNameKey(Map<String, Map<String, Object>> equipmentUnitMapUnitNameKey) {
		TaskKPI.equipmentUnitMapUnitNameKey = equipmentUnitMapUnitNameKey;
	}

	public KpiTaskService getKpiTaskService() {
		return kpiTaskService;
	}

	public void setKpiTaskService(KpiTaskService kpiTaskService) {
		this.kpiTaskService = kpiTaskService;
	}

	public static Map<String, Map<String, Object>> getEquipmentUnitMap() {
		return equipmentUnitMap;
	}

	public static void setEquipmentUnitMap(Map<String, Map<String, Object>> equipmentUnitMap) {
		TaskKPI.equipmentUnitMap = equipmentUnitMap;
	}

	public static Map<String, Map<String, Object>> getEquipmentNeMap() {
		return equipmentNeMap;
	}

	public static void setEquipmentNeMap(Map<String, Map<String, Object>> equipmentNeMap) {
		TaskKPI.equipmentNeMap = equipmentNeMap;
	}

}
