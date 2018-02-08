package com.nokia.ices.app.pgw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class PgwDataQueryServerApp {
	private static final Logger LOGGER = LogManager.getLogger(PgwDataQueryServerApp.class);
	public static void main(String[] args) {
		if(args.length==2){
			String arg0 = args[0];
			String arg1 = args[1];
			LOGGER.info(arg0+"|"+arg1+"|start");
			SpringApplication.run(PgwDataQueryServerApp.class);
		}else{
			LOGGER.info("Args not input.");
			SpringApplication.run(PgwDataQueryServerApp.class);
		}
	}
}
