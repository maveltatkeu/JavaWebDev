package com.kora.batch.events.reader;

import com.kora.batch.events.cache.ExternalPersonCache;
import com.kora.batch.events.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheReader implements ItemStreamReader<Person> {

  private static final Logger logger = LoggerFactory.getLogger(CacheReader.class);

  private final ExternalPersonCache cache;
  private List<Person> externalPersons;
  private AtomicInteger currentIndex;

  public CacheReader(ExternalPersonCache cache) {
    this.cache = cache;
  }

  @Override
  public Person read() {
    if (currentIndex == null || externalPersons == null) {
      return null;
    }

    int index = currentIndex.getAndIncrement();
    if (index < externalPersons.size()) {
      Person person = externalPersons.get(index);
      logger.debug("Reading external person from cache: {}", person.getPersonId());
      return person;
    }

    return null;
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    logger.info("Opening cache reader, waiting for cache population...");
    cache.awaitPopulation();
    this.externalPersons = cache.getAllExternalPersons();
    this.currentIndex = new AtomicInteger(0);
    logger.info("Cache reader ready with {} external persons", externalPersons.size());
  }

  @Override
  public void update(ExecutionContext executionContext) throws ItemStreamException {
    // No state to update
  }

  @Override
  public void close() throws ItemStreamException {
    this.externalPersons = null;
    this.currentIndex = null;
    logger.info("Cache reader closed");
  }
}