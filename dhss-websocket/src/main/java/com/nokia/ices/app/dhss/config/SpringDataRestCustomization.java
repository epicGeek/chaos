package com.nokia.ices.app.dhss.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.stereotype.Component;

@Component
public class SpringDataRestCustomization extends RepositoryRestConfigurerAdapter {

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		String basePath = config.getBasePath().toString();
		config.getCorsRegistry()
		.addMapping(basePath + "/**")
		.allowedOrigins("*")
		.allowedMethods("*")
		.allowedHeaders("*")
		.exposedHeaders("Ices-Access-Token,Location")
		.allowCredentials(false).maxAge(3600);
	}
}