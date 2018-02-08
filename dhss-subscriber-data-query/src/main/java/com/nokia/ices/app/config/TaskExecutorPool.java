package com.nokia.ices.app.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@EnableAsync
@Configuration
public class TaskExecutorPool implements AsyncConfigurer{
	private static final Logger LOGGER = LogManager.getLogger(TaskExecutorPool.class);
	@Autowired
	private TaskThreadPoolConfig config;
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(config.getCorePoolSize());
		taskExecutor.setMaxPoolSize(config.getMaxPoolSize());
		taskExecutor.setQueueCapacity(config.getQueueCapacity());
		taskExecutor.setKeepAliveSeconds(config.getKeepAliveSeconds());
		
		taskExecutor.setThreadNamePrefix("subscriber-thread-");
		// 当pool已经达到maxPoolsize的时，不在新线程中执行任务，而是由调用者所在的线程来执行
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		taskExecutor.initialize();
		LOGGER.info(">>>>>Thread pool init completed>>>>>");
		return taskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {
			@Override
			public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
				LOGGER.error("=========================={}=======================", arg0.getMessage(), arg0);
				LOGGER.error("exception method:" + arg1.getName());
			}
		};
	}
}
