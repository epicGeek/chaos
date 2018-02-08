package com.nokia.ices.app.dhss.kpi.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConfigurationProperties
public class JdbcDataSourceSettings {

	private static final Logger logger = LoggerFactory.getLogger(JdbcDataSourceSettings.class);

	/****** primary database ******/
	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource")
	public DataSourceProperties configDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource primaryDataSource() {

		logger.info("########################");
		logger.info("#                      #");
		logger.info("#  init maria Database #");
		logger.info("#                      #");
		logger.info("########################");
		return configDataSourceProperties().initializeDataSourceBuilder().build();
	}

	@Bean(name = "jdbcTemplatePrimary")
	@Primary
	public JdbcTemplate jdbcTemplatePrimary() {
		return new JdbcTemplate(primaryDataSource());
	}

	/****** 2nd database ******/
	@Bean
	@ConfigurationProperties("kpi.jdbc.source.datasource")
	public DataSourceProperties sourceDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "jdbcTemplateSource")
	public JdbcTemplate jdbcTemplateSource() {
		return new JdbcTemplate(sourceDataSource());
	}

	@Bean
	@ConfigurationProperties(prefix = "kpi.jdbc.source.datasource")
	public DataSource sourceDataSource() {
		logger.info("##############################");
		logger.info("#                            #");
		logger.info("# init OSS Database (Source) #");
		logger.info("#                            #");
		logger.info("##############################");
		return sourceDataSourceProperties().initializeDataSourceBuilder().build();
	}

}
