package com.udacity.tdd.studentmanagmnt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class WebConfig {
    // Additional configurations (CORS, interceptors, etc.)
}