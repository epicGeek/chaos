package com.nokia.ices.app.dhss.event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import com.nokia.ices.app.dhss.domain.smart.SmartCheckJob;
import com.nokia.ices.app.dhss.service.SmartCheckService;


@RepositoryEventHandler(SmartCheckJob.class)
public class SmartCheckJobEventHandler {
	
	
	@Autowired
	private SmartCheckService  smartCheckService;
	
	
	public static Map<String, String> messageResult = new HashMap<String, String>();


	@HandleBeforeCreate
	public void handleBeforeCreate(SmartCheckJob smartCheckJob) {
		try {
			//增加方案
			smartCheckService.sendMessage(smartCheckJob,0);
			messageMap(smartCheckJob,0);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	@HandleBeforeSave
	public void handleBeforeSave(SmartCheckJob smartCheckJob) {
		try {
//			smartCheckJob.setUnit(smartCheckService.findSmartJobUnit(smartCheckJob));
//			smartCheckJob.setCheckItem(smartCheckService.findSmartJobCommandCheckItem(smartCheckJob));
			//修改方案
			smartCheckService.sendMessage(smartCheckJob,1);
			messageMap(smartCheckJob,0);
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	@HandleBeforeDelete
	public void HandleBeforeDelete(SmartCheckJob smartCheckJob){
		
		try {
			//删除方案
			smartCheckService.sendMessage(smartCheckJob,2);
			messageMap(smartCheckJob,1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private void messageMap(SmartCheckJob smartCheckJob,int type) throws InterruptedException{
		messageResult.put(smartCheckJob.getJobName(), null);
		while(true){
			Thread.sleep(1000);
			if(messageResult.get(smartCheckJob.getJobName()) != null){
				String [] message =  messageResult.get(smartCheckJob.getJobName()).split("_");
				if(type == 0 && message[0].equals("0")){
					try {
						smartCheckJob.setNextDay(sdf.parse(message[1]+":00"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				messageResult.remove(smartCheckJob.getJobName());
				break;
			}
		}
	}
	
	
	
	 
	
	
}
