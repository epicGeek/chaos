package com.nokia.ices.app.dhss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
public class InterceptorConfiguration {
    
	@Autowired
	private HandlerInterceptor securityHandlerInterceptor;
	
	@Bean
	public MappedInterceptor securityMappedInterceptor() {
		return new MappedInterceptor(new String[]{"/api/v1/**"}, securityHandlerInterceptor);
	}
}
