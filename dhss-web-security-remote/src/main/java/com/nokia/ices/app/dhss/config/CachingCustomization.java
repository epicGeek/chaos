package com.nokia.ices.app.dhss.config;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingCustomization {

   @Bean
   CacheManager cacheManager() {

     SimpleCacheManager cacheManager = new SimpleCacheManager();
     cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("loginUser")));
     return cacheManager;
   }
 }