package com.kora.batch.events.writer;


import com.kora.batch.events.model.CategorizedPerson;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

public class CategorizedPersonWriter implements ItemStreamWriter<CategorizedPerson> {

  private final String outputFileName;
  private FlatFileItemWriter<CategorizedPerson> delegate;

  public CategorizedPersonWriter(String outputFileName) {
    this.outputFileName = outputFileName;
  }

  private void initializeDelegate() {
    BeanWrapperFieldExtractor<CategorizedPerson> extractor = new BeanWrapperFieldExtractor<>();
    extractor.setNames(new String[]{"personId", "firstname", "lastname",
        "siret", "city", "salary", "calculatedCategory"});

    DelimitedLineAggregator<CategorizedPerson> aggregator = new DelimitedLineAggregator<>();
    aggregator.setDelimiter(",");
    aggregator.setFieldExtractor(extractor);

    this.delegate = new FlatFileItemWriterBuilder<CategorizedPerson>()
        .name("categorizedPersonWriter")
        .resource(new FileSystemResource(outputFileName))
        .lineAggregator(aggregator)
        .headerCallback(writer -> writer.write(
            "personId,firstname,lastname,siret,city,salary,calculatedCategory"))
        .append(false)
        .shouldDeleteIfExists(true)
        .build();
  }

  @Override
  public void write(Chunk<? extends CategorizedPerson> chunk) throws Exception {
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