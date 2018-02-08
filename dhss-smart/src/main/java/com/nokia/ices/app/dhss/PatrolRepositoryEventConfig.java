package com.nokia.ices.app.dhss;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nokia.ices.app.dhss.event.SmartCheckJobEventHandler;


@Configuration
public class PatrolRepositoryEventConfig {

    @Bean
    SmartCheckJobEventHandler smartCheckJobEventHandler() {
        return new SmartCheckJobEventHandler();
    }
}