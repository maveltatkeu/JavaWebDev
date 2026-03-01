package com.kora.batch.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {
  private Long personId;
  private String firstname;
  private String lastname;
  private String siret;
  private String city;
  private Double salary;
  private String category;

  @Override
  public String toString() {
    return "Person{" +
        "personId=" + personId +
        ", firstname='" + firstname + '\'' +
        ", lastname='" + lastname + '\'' +
        ", siret='" + siret + '\'' +
        ", city='" + city + '\'' +
        ", salary=" + salary +
        ", category='" + category + '\'' +
        '}';
  }

  public boolean isExternal() {
    try {
      return this.siret.startsWith("7");
    } catch (Exception e) {
      return false;
    }
  }
}
