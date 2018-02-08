package com.nokia.ices.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubscriberDataExportApplication {
	private static final Logger LOGGER = LogManager.getLogger(SubscriberDataExportApplication.class);
	public static void main(String[] args) {
		if(args.length==2){
			String firstArg = args[0];
			String secArg = args[1];
			LOGGER.info(firstArg+"|"+secArg+"|start");
			SpringApplication.run(SubscriberDataExportApplication.class, args);
		}else{
			LOGGER.info("NOT ENOUGH PARAMS,BUT STILL START PROGRAM.");
			SpringApplication.run(SubscriberDataExportApplication.class, args);
		}
	}
}
