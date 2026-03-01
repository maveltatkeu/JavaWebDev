package com.kora.batch.events.config;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BatchTestConfig {
    
    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
    
    @Bean
    public JobParameters defaultJobParameters() {
        return new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();
    }
}