package com.kora.batch.events.reader;

import com.kora.batch.events.cache.ExternalPersonCache;
import com.kora.batch.events.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;

public class FilteringPersonReader implements ItemStreamReader<Person> {

  private static final Logger logger = LoggerFactory.getLogger(FilteringPersonReader.class);

  private final FlatFileItemReader<Person> delegate;
  private final ExternalPersonCache cache;
  private int externalCount = 0;
  private int standardCount = 0;

  public FilteringPersonReader(ExternalPersonCache cache) {
    this.cache = cache;
    this.delegate = createDelegate();
  }

  private FlatFileItemReader<Person> createDelegate() {
    return new FlatFileItemReaderBuilder<Person>()
        .name("personFileReader")
        .resource(new ClassPathResource("persons.csv"))
        .delimited()
        .names("personId", "firstname", "lastname", "siret", "city", "salary", "category")
        .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
          setTargetType(Person.class);
        }})
        .linesToSkip(1)
        .build();
  }

  @Override
  public Person read() throws Exception {
    Person person = delegate.read();

    if (person == null) {
      // End of file - mark cache population complete
      cache.markPopulationComplete(externalCount);
      logger.info("File reading complete. Standard: {}, External: {}", standardCount, externalCount);
      return null;
    }

    if (person.isExternal()) {
      cache.addExternalPerson(person);
      externalCount++;
      // Return null to skip this item in standard flow
      // We need to read next for standard flow
      return read(); // Recursive call to get next standard person
    } else {
      standardCount++;
      return person;
    }
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    cache.clear();
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