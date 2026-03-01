package com.kora.batch.events.processor;

import com.kora.batch.events.model.ExternalPerson;
import com.kora.batch.events.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ExternalPersonProcessor implements ItemProcessor<Person, ExternalPerson> {

  private static final Logger logger = LoggerFactory.getLogger(ExternalPersonProcessor.class);
  private static final double HOME_RENT_PERCENTAGE = 0.30;

  @Override
  public ExternalPerson process(Person person) {
    String externalCategory = determineExternalCategory(person.getSalary());
    double homeRent = person.getSalary() * HOME_RENT_PERCENTAGE;

    ExternalPerson result = new ExternalPerson();
    result.setPersonId(person.getPersonId());
    result.setFirstname(person.getFirstname());
    result.setLastname(person.getLastname());
    result.setSiret(person.getSiret());
    result.setCity(person.getCity());
    result.setSalary(person.getSalary());
    result.setExternalCategory(externalCategory);
    result.setHomeRent(homeRent);

    logger.debug("Processed external person {}: {} -> {}, homeRent={}",
        person.getPersonId(), person.getSalary(), externalCategory, homeRent);

    return result;
  }

  private String determineExternalCategory(Double salary) {
    if (salary == null) return "UNKNOWN";
    if (salary <= 20000) return "EMPLOYEE";
    if (salary <= 45000) return "LEAD";
    if (salary <= 75000) return "DIRECTOR";
    return "EXECUTOR";
  }
}