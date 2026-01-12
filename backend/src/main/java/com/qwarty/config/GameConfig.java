package com.qwarty.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class GameConfig {

    @Bean
    public ScheduledExecutorService gameScheduler() {
        return Executors.newScheduledThreadPool(10);
    }

    @PreDestroy
    public void shutdownScheduler() {
        gameScheduler().shutdown();
    }

}
 