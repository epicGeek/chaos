package com.nokia.ices.app.dhss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SpringcloudserviceApplication {

	public static void main(String[] args) {    
		SpringApplication.run(SpringcloudserviceApplication.class, args);
    }
}
