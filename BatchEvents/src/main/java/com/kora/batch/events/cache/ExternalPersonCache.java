package com.kora.batch.events.cache;

import com.kora.batch.events.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ExternalPersonCache {

  private static final Logger logger = LoggerFactory.getLogger(ExternalPersonCache.class);

  private final List<Person> externalPersons = new CopyOnWriteArrayList<>();
  private final AtomicBoolean populationComplete = new AtomicBoolean(false);
  private final AtomicInteger expectedCount = new AtomicInteger(-1);
  private final CountDownLatch populationLatch = new CountDownLatch(1);

  public void addExternalPerson(Person person) {
    if (person != null && person.isExternal()) {
      externalPersons.add(person);
      logger.debug("Added external person to cache: {}", person.getPersonId());
    }
  }

  public List<Person> getAllExternalPersons() {
    awaitPopulation();
    return new ArrayList<>(externalPersons);
  }

  public boolean isEmpty() {
    return externalPersons.isEmpty();
  }

  public int size() {
    return externalPersons.size();
  }

  public void markPopulationComplete(int count) {
    expectedCount.set(count);
    populationComplete.set(true);
    populationLatch.countDown();
    logger.info("External person cache population complete. Total: {}", count);
  }

  public void awaitPopulation() {
    try {
      boolean completed = populationLatch.await(5, TimeUnit.MINUTES);
      if (!completed) {
        throw new IllegalStateException("Timeout waiting for cache population");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while waiting for cache", e);
    }
  }

  public boolean isPopulationComplete() {
    return populationComplete.get();
  }

  public void clear() {
    externalPersons.clear();
    populationComplete.set(false);
    expectedCount.set(-1);
    // Reset latch for next job run
    logger.info("External person cache cleared");
  }
}