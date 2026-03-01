package com.kora.batch.events.processor;

import com.kora.batch.events.model.CategorizedPerson;
import com.kora.batch.events.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryProcessorTest {

  private CategoryProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new CategoryProcessor();
  }

  @ParameterizedTest
  @CsvSource({
      "15000, JUNIOR",
      "20000, JUNIOR",
      "20001, MIDDLE",
      "35000, MIDDLE",
      "45000, MIDDLE",
      "45001, SENIOR",
      "60000, SENIOR",
      "75000, SENIOR",
      "75001, MASTER",
      "100000, MASTER"
  })
  void process_shouldCategorizeCorrectly(double salary, String expectedCategory) throws Exception {
    // Given
    Person person = createPerson(salary);

    // When
    CategorizedPerson result = processor.process(person);

    // Then
    assertNotNull(result);
    assertEquals(expectedCategory, result.getCalculatedCategory());
  }

  @Test
  void process_nullSalary_shouldReturnUnknown() throws Exception {
    // Given
    Person person = createPerson(null);

    // When
    CategorizedPerson result = processor.process(person);

    // Then
    assertEquals("UNKNOWN", result.getCalculatedCategory());
  }

  private Person createPerson(Double salary) {
    Person person = new Person();
    person.setPersonId(1L);
    person.setFirstname("John");
    person.setLastname("Doe");
    person.setSiret("12345678901234");
    person.setCity("Paris");
    person.setSalary(salary);
    return person;
  }
}