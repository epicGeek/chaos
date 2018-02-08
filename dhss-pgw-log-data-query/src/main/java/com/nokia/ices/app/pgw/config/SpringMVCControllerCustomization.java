package com.nokia.ices.app.pgw.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class SpringMVCControllerCustomization implements EnvironmentAware{
    
	private static final Logger logger = LoggerFactory.getLogger(SpringMVCControllerCustomization.class);

	private Environment environment;
	

	public Environment getEnvironment() {
		return environment;
	}

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
        		String basePath = environment.getProperty("spring.data.rest.base-path");
        		registry.addMapping( (basePath==null?"":basePath) + "/**" )
                .allowedMethods("*")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .exposedHeaders("Ices-Access-Token,Location")
                .allowCredentials(false).maxAge(3600);
        		logger.debug(basePath);
            }
        };
    }

	@Override
	public void setEnvironment(Environment env) {
		this.environment = env;
	}
}