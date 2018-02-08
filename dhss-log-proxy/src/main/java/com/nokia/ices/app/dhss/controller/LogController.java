package com.nokia.ices.app.dhss.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nokia.ices.app.dhss.config.TaskSetting;
import com.nokia.ices.app.dhss.jms.producer.MessageProducer;
import com.nokia.ices.app.dhss.vo.OutterPlatformLogger;

@RestController
@RequestMapping("/api/v1")
public class LogController {

	private static final Logger logger = LoggerFactory.getLogger(LogController.class);
	// private static final String logContentTemplate = "%s|%s|%s|%s";
	private static final DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private TaskSetting taskSetting;

	@Autowired
	private MessageProducer messageProducer;

	@RequestMapping(value = "/log", method = RequestMethod.POST)
	public boolean triggerLogger(@RequestBody OutterPlatformLogger outterPlatformLogger, HttpServletRequest request) {
		String realRemoteAddress = (generateRemoteAddress(request));
		outterPlatformLogger.setRemoteAddress(realRemoteAddress);
		fillOutterPlatformLogger(outterPlatformLogger);
		try {
			sendEventLog(outterPlatformLogger);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@RequestMapping(value = "/setting", method = RequestMethod.GET)
	public Map<String, String> status() {
		return taskSetting.getUrlMap();
	}

	private void fillOutterPlatformLogger(OutterPlatformLogger outterPlatformLogger) {
		logger.info(outterPlatformLogger.toString());
		Map<String,String> urlMapping = taskSetting.getUrlMap();
	    String requestURL = outterPlatformLogger.getRequestURL();
		if(urlMapping.containsKey(requestURL)){
			String appModule = urlMapping.get(outterPlatformLogger.getRequestURL());
			outterPlatformLogger.setAppModule(appModule);
		}else {
			urlMapping.forEach((urlPattern,appModule) -> {
				if(requestURL.indexOf(urlPattern)!=-1) {
					outterPlatformLogger.setAppModule(appModule);
				}
			});
		}

	}

	private void sendEventLog(OutterPlatformLogger outterPlatformLogger) {
		logger.info("Log start");

		Long startTimestamp = System.currentTimeMillis();
		DateTime datetime = new DateTime(startTimestamp);
		Map<String, Object> messageBody = new HashMap<String, Object>();

		messageBody.put("sessionId", outterPlatformLogger.getToken());
		messageBody.put("appName", outterPlatformLogger.getAppName());
		messageBody.put("appModule", outterPlatformLogger.getAppModule());
		messageBody.put("eventContent", outterPlatformLogger.getEventContent());
		messageBody.put("remoteAddress", outterPlatformLogger.getRemoteAddress());
		messageBody.put("time", datetime.toString(format));
		messageBody.put("user", outterPlatformLogger.getUser());
		String desQName = taskSetting.getDesQName();
		messageProducer.sendTextMessage(desQName, messageBody);

		logger.info("Log end during :" + (System.currentTimeMillis() - startTimestamp));
	}
	/** 
	 * @Title: getIpAddr  
	 * @author kaka  www.zuidaima.com 
	 * @Description: 获取客户端IP地址   
	 * @param @return     
	 * @return String    
	 * @throws 
	 */  
	private String generateRemoteAddress(HttpServletRequest request) {   
	       String ip = request.getHeader("x-forwarded-for");   
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	           ip = request.getHeader("Proxy-Client-IP");   
	       }   
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	           ip = request.getHeader("WL-Proxy-Client-IP");   
	       }   
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {   
	           ip = request.getRemoteAddr();   
	           if(ip.equals("127.0.0.1")){     
	               //根据网卡取本机配置的IP     
	               InetAddress inet=null;     
	               try {     
	                   inet = InetAddress.getLocalHost();     
	               } catch (UnknownHostException e) {     
	                   e.printStackTrace();     
	               }     
	               ip= inet.getHostAddress();     
	           }  
	       }   
	       // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割  
	       if(ip != null && ip.length() > 15){    
	           if(ip.indexOf(",")>0){     
	               ip = ip.substring(0,ip.indexOf(","));     
	           }     
	       }     
	       return ip;   
	}  
}
