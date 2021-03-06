package com.nokia.pgw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Entry {
	private static final Logger LOGGER = LogManager.getLogger(Entry.class);
	private static String SERVICE_NAME = "";
	private static String PLAN_NAME = "";
	private static String LOGGER_HEAD = "";
//TEST INPUT PARAMs
	public static void main(String[] args) {
		if (args.length == 2) {
			LOGGER.info(args);
			
			setSERVICE_NAME(args[0]);
			setPLAN_NAME(args[1]);
			setLOGGER_HEAD(getSERVICE_NAME() + "|" + getPLAN_NAME() + "|");
			System.out.println(getLOGGER_HEAD());
			SpringApplication.run(Entry.class);
		} else {
			setLOGGER_HEAD("PGW TASK| -  ");
			LOGGER.info(Entry.getLOGGER_HEAD()+"ARGS NOT ENOUGH");
			SpringApplication.run(Entry.class);
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
