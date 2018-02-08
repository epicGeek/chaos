package com.nokia.boss;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import com.nokia.boss.settings.TaskThreadPoolConfig;
 

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({TaskThreadPoolConfig.class} ) // 开启配置属性支持 
public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	private static String SERVICE_NAME = "";
	private static String PLAN_NAME = "";
	private static String LOGGER_HEAD = "";

	public static void main(String[] args) {
		if (args.length == 2) {
			LOGGER.info(args);
			setSERVICE_NAME(args[0]);
			setPLAN_NAME(args[1]);
			setLOGGER_HEAD(getSERVICE_NAME() + "|" + getPLAN_NAME() + "|");
			SpringApplication.run(App.class);
		} else {
			setLOGGER_HEAD("BOSS TASK| -  ");
			LOGGER.info(App.getLOGGER_HEAD() + "ARGS NOT ENOUGH");
			SpringApplication.run(App.class);
		}
	}

	public static String getSERVICE_NAME() {
		return SERVICE_NAME;
	}

	public static void setSERVICE_NAME(String sERVICE_NAME) {
		SERVICE_NAME = sERVICE_NAME;
	}

	public static String getPLAN_NAME() {
		return PLAN_NAME;
	}

	public static void setPLAN_NAME(String pLAN_NAME) {
		PLAN_NAME = pLAN_NAME;
	}

	public static String getLOGGER_HEAD() {
		return LOGGER_HEAD;
	}

	public static void setLOGGER_HEAD(String lOGGER_HEAD) {
		LOGGER_HEAD = lOGGER_HEAD;
	}
}
