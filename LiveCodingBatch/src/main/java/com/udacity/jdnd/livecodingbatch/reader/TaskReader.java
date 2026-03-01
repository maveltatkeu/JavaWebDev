package com.udacity.jdnd.livecodingbatch.reader;

import com.udacity.jdnd.livecodingbatch.model.TaskInput;
import com.udacity.jdnd.livecodingbatch.model.TaskOutput;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemWriter;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class TaskReader {

  @Bean
  public FlatFileItemReader<TaskInput> reader() {
    return new FlatFileItemReaderBuilder<TaskInput>()
        .name("taskItemReader")
        .resource(new ClassPathResource("input/tasks.csv"))
        .delimited()
        .delimiter("|")
        .names("id", "title", "priority")
        .linesToSkip(1)
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>(){
          @Override
          public void setTargetType(Class<? extends TaskInput> type) {
            super.setTargetType(type);
          }
        }).build();
  }

  @Bean
  public FlatFileItemWriter<TaskOutput> writerTaskk() {
    FlatFileItemWriter<TaskOutput> writer = new FlatFileItemWriter<>();
    writer.setResource(new FileSystemResource("output/tasks_processed.csv"));

    DelimitedLineAggregator<TaskOutput> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");

    BeanWrapperFieldExtractor<TaskOutput> extractor =
        new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[]{"id", "title", "priority", "category"});

    aggregator.setFieldExtractor(extractor);
    writer.setLineAggregator(aggregator);
    writer.setHeaderCallback(w ->
        w.write("id,title,priority,category"));

    return writer;
  }






}
