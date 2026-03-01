package com.kora.batch.events.listener;

import com.kora.batch.events.cache.ExternalPersonCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CacheCleanupListener implements JobExecutionListener {

  private static final Logger logger = LoggerFactory.getLogger(CacheCleanupListener.class);

  private final ExternalPersonCache cache;

  public CacheCleanupListener(ExternalPersonCache cache) {
    this.cache = cache;
  }

  @Override
  public void beforeJob(JobExecution jobExecution) {
    logger.info("Starting job: {}, cleaning cache", jobExecution.getJobInstance().getJobName());
    cache.clear();
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    logger.info("Job completed with status: {}, cleaning cache", jobExecution.getStatus());
    cache.clear();
  }
}