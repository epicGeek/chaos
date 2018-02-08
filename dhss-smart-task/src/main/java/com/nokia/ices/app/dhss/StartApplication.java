package com.nokia.ices.app.dhss;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Hello world!
 *
 */
@SpringBootApplication
public class StartApplication {
	private final static Logger logger = LogManager.getLogger(StartApplication.class);
	
	public static String  moduleStr;
	
	public static void main(String[] args)  {
		moduleStr = (args != null && args.length >= 2) ? (args[0] + "|" + args[1] + "|") : "";
//		moduleStr = "dhsssmarttask|day autu|";
		logger.info(moduleStr+"start");
        SpringApplication.run(StartApplication.class);
    }
    
}
