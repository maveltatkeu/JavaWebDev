package com.kora.batch.events.config;

import com.kora.batch.events.cache.ExternalPersonCache;
import com.kora.batch.events.listener.CacheCleanupListener;
import com.kora.batch.events.model.CategorizedPerson;
import com.kora.batch.events.model.ExternalPerson;
import com.kora.batch.events.model.Person;
import com.kora.batch.events.processor.CategoryProcessor;
import com.kora.batch.events.processor.ExternalPersonProcessor;
import com.kora.batch.events.reader.CacheReader;
import com.kora.batch.events.reader.FilteringPersonReader;
import com.kora.batch.events.writer.CategorizedPersonWriter;
import com.kora.batch.events.writer.ExternalPersonWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final ExternalPersonCache externalPersonCache;
  private final TaskExecutor parallelTaskExecutor;
  private final CacheCleanupListener cacheCleanupListener;

  public BatchConfig(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ExternalPersonCache externalPersonCache,
                     TaskExecutor parallelTaskExecutor, CacheCleanupListener cacheCleanupListener) {
    this.jobRepository = jobRepository;
    this.transactionManager = transactionManager;
    this.externalPersonCache = externalPersonCache;
    this.parallelTaskExecutor = parallelTaskExecutor;
    this.cacheCleanupListener = cacheCleanupListener;
  }

  @Bean
  public Job categorizePersonsJob() {
    // Create parallel flows
    Flow standardFlow = new FlowBuilder<Flow>("standardFlow")
        .start(standardStep())
        .build();

    Flow externalFlow = new FlowBuilder<Flow>("externalFlow")
        .start(externalStep())
        .build();

    // Combine flows in parallel
    Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
        .split(parallelTaskExecutor)
        .add(standardFlow, externalFlow)
        .build();

    return new JobBuilder("categorizePersonsJob", jobRepository)
        .start(parallelFlow)
        .end()
        .listener(cacheCleanupListener)
        .build();
  }

  @Bean
  public Step standardStep() {
    return new StepBuilder("standardStep", jobRepository)
        .<Person, CategorizedPerson>chunk(10, transactionManager)
        .reader(filteringReader())
        .processor(categoryProcessor())
        .writer(categorizedWriter())
        .build();
  }

  @Bean
  public Step externalStep() {
    return new StepBuilder("externalStep", jobRepository)
        .<Person, ExternalPerson>chunk(10, transactionManager)
        .reader(cacheReader())
        .processor(externalProcessor())
        .writer(externalWriter())
        .build();
  }

  @Bean
  public FilteringPersonReader filteringReader() {
    return new FilteringPersonReader(externalPersonCache);
  }

  @Bean
  public CacheReader cacheReader() {
    return new CacheReader(externalPersonCache);
  }

  @Bean
  public CategoryProcessor categoryProcessor() {
    return new CategoryProcessor();
  }

  @Bean
  public ExternalPersonProcessor externalProcessor() {
    return new ExternalPersonProcessor();
  }

  @Bean
  public CategorizedPersonWriter categorizedWriter() {
    return new CategorizedPersonWriter("categories.csv");
  }

  @Bean
  public ExternalPersonWriter externalWriter() {
    return new ExternalPersonWriter("external.csv");
  }
}