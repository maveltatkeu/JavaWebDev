package com.kora.batch.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorizedPerson implements Serializable {
  private Long personId;
  private String firstname;
  private String lastname;
  private String siret;
  private String city;
  private Double salary;
  private String calculatedCategory;

  @Override
  public String toString() {
    return "CategorizedPerson{" +
        "personId=" + personId +
        ", firstname='" + firstname + '\'' +
        ", lastname='" + lastname + '\'' +
        ", calculatedCategory='" + calculatedCategory + '\'' +
        '}';
  }

}
