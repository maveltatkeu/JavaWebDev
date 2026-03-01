package com.kora.batch.events.writer;

import com.kora.batch.events.model.ExternalPerson;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

public class ExternalPersonWriter implements ItemStreamWriter<ExternalPerson> {

  private final String outputFileName;
  private FlatFileItemWriter<ExternalPerson> delegate;

  public ExternalPersonWriter(String outputFileName) {
    this.outputFileName = outputFileName;
  }

  private void initializeDelegate() {
    BeanWrapperFieldExtractor<ExternalPerson> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[]{"personId", "firstname", "lastname",
        "siret", "city", "salary", "externalCategory", "homeRent"});

    DelimitedLineAggregator<ExternalPerson> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(extractor);

    this.delegate = new FlatFileItemWriterBuilder<ExternalPerson>()
        .name("externalPersonWriter")
        .resource(new FileSystemResource(outputFileName))
        .lineAggregator(aggregator)
        .headerCallback(writer -> writer.write(
            "personId,firstname,lastname,siret,city,salary,externalCategory,homeRent"))
        .append(false)
        .shouldDeleteIfExists(true)
        .build();
  }

  @Override
  public void write(Chunk<? extends ExternalPerson> chunk) throws Exception {
    delegate.write(chunk);
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    initializeDelegate();
    delegate.open(executionContext);
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    delegate.update(executionContext);
  }

  @Override
  public void close() throws ItemStreamException {
    delegate.close();
  }
}