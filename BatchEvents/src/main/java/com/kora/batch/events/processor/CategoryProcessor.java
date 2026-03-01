package com.kora.batch.events.processor;

import com.kora.batch.events.model.CategorizedPerson;
import com.kora.batch.events.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class CategoryProcessor implements ItemProcessor<Person, CategorizedPerson> {

  private static final Logger logger = LoggerFactory.getLogger(CategoryProcessor.class);

  @Override
  public CategorizedPerson process(Person person) {
    String calculatedCategory = determineCategory(person.getSalary());

    CategorizedPerson result = new CategorizedPerson();
    result.setPersonId(person.getPersonId());
    result.setFirstname(person.getFirstname());
    result.setLastname(person.getLastname());
    result.setSiret(person.getSiret());
    result.setCity(person.getCity());
    result.setSalary(person.getSalary());
    result.setCalculatedCategory(calculatedCategory);

    logger.debug("Processed standard person {}: {} -> {}",
        person.getPersonId(), person.getSalary(), calculatedCategory);

    return result;
  }

  private String determineCategory(Double salary) {
    if (salary == null) return "UNKNOWN";
    if (salary <= 20000) return "JUNIOR";
    if (salary <= 45000) return "MIDDLE";
    if (salary <= 75000) return "SENIOR";
    return "MASTER";
  }
}