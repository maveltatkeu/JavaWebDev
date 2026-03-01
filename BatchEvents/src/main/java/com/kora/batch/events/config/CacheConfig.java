package com.kora.batch.events.config;

import com.kora.batch.events.cache.ExternalPersonCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean
  public ExternalPersonCache externalPersonCache() {
    return new ExternalPersonCache();
  }
}