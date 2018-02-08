package com.nokia.ices.app.dhss.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nokia.ices.app.dhss.domain.ems.EmsMonitor;
import com.nokia.ices.app.dhss.service.TaskService;

@Component
@EnableScheduling
public class TaskApplication {
	
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private TaskService taskService;
	
	@Scheduled(cron="0 0 9 ? * *")
	public void deleTempData() throws ParseException{
		GregorianCalendar todayCal = new GregorianCalendar();
		String groupDate = format.format(todayCal.getTime().getTime()-1000*60*60*24);
		String thisDate = format.format(todayCal.getTime().getTime()-1000*60*5);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("executeTime_GE", format.parse(groupDate));
		map.put("executeTime_LT", format.parse(thisDate));
		List<EmsMonitor> list = taskService.findEmsMonitors(map);
		for (EmsMonitor emsMonitor : list) {
			if(!"0".equals(emsMonitor.getResultLevel())){
				taskService.noticeGroup(emsMonitor.getGroupId(), emsMonitor.getNotificationContent(),
						String.valueOf(emsMonitor.getMonitoredUnitId()), String.valueOf(emsMonitor.getMonitoredCommandId())
						, "(ALARM_REMIND)"+emsMonitor.getMonitoredUnitName(), false, "");
			}
		}
	}

}
