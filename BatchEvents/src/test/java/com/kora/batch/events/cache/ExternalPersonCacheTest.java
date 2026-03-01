package com.kora.batch.events.cache;


import com.kora.batch.events.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalPersonCacheTest {

  private ExternalPersonCache cache;

  @BeforeEach
  void setUp() {
    cache = new ExternalPersonCache();
  }

  @Test
  void addExternalPerson_shouldAddOnlyExternalPersons() {
    // Given
    Person external = createPerson(1L, "71111111111111");
    Person standard = createPerson(2L, "12345678901234");

    // When
    cache.addExternalPerson(external);
    cache.addExternalPerson(standard);

    // Then
    assertEquals(1, cache.size());
  }

  @Test
  void addExternalPerson_nullPerson_shouldNotFail() {
    assertDoesNotThrow(() -> cache.addExternalPerson(null));
    assertTrue(cache.isEmpty());
  }

  @Test
  void awaitPopulation_shouldBlockUntilMarkedComplete() throws InterruptedException {
    // Given
    ExecutorService executor = Executors.newSingleThreadExecutor();
    CountDownLatch latch = new CountDownLatch(1);

    // When
    executor.submit(() -> {
      cache.awaitPopulation();
      latch.countDown();
    });

    Thread.sleep(100);
    assertEquals(1, latch.getCount());

    cache.markPopulationComplete(0);

    // Then
    assertTrue(latch.await(1, TimeUnit.SECONDS));
    executor.shutdown();
  }

  @Test
  void getAllExternalPersons_shouldReturnDefensiveCopy() {
    // Given
    cache.addExternalPerson(createPerson(1L, "71111111111111"));
    cache.markPopulationComplete(1);

    // When
    List<Person> first = cache.getAllExternalPersons();
    List<Person> second = cache.getAllExternalPersons();

    // Then
    assertNotSame(first, second);
    assertEquals(first, second);
  }

  @Test
  void clear_shouldResetCache() {
    // Given
    cache.addExternalPerson(createPerson(1L, "71111111111111"));
    cache.markPopulationComplete(1);

    // When
    cache.clear();

    // Then
    assertTrue(cache.isEmpty());
    assertFalse(cache.isPopulationComplete());
  }

  private Person createPerson(Long id, String siret) {
    Person person = new Person();
    person.setPersonId(id);
    person.setFirstname("Test");
    person.setLastname("User");
    person.setSiret(siret);
    person.setCity("Paris");
    person.setSalary(50000.0);
    return person;
  }
}