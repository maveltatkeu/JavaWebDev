package com.kora.batch.events.processor;

import com.kora.batch.events.model.ExternalPerson;
import com.kora.batch.events.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExternalPersonProcessorTest {

  private ExternalPersonProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ExternalPersonProcessor();
  }

  @ParameterizedTest
  @CsvSource({
      "15000, EMPLOYEE, 4500.0",
      "20000, EMPLOYEE, 6000.0",
      "20001, LEAD, 6000.3",
      "35000, LEAD, 10500.0",
      "45000, LEAD, 13500.0",
      "45001, DIRECTOR, 13500.3",
      "60000, DIRECTOR, 18000.0",
      "75000, DIRECTOR, 22500.0",
      "75001, EXECUTOR, 22500.3",
      "100000, EXECUTOR, 30000.0"
  })
  void process_shouldCalculateCorrectly(double salary, String expectedCategory, double expectedHomeRent)
      throws Exception {
    // Given
    Person person = createPerson(salary);

    // When
    ExternalPerson result = processor.process(person);

    // Then
    assertNotNull(result);
    assertEquals(expectedCategory, result.getExternalCategory());
    assertEquals(expectedHomeRent, result.getHomeRent(), 0.01);
    assertEquals(salary * 0.30, result.getHomeRent(), 0.01);
  }

  private Person createPerson(Double salary) {
    Person person = new Person();
    person.setPersonId(1L);
    person.setFirstname("Jane");
    person.setLastname("Smith");
    person.setSiret("71234567890123");
    person.setCity("Lyon");
    person.setSalary(salary);
    return person;
  }
}