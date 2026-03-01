package com.kora.batch.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalPerson implements Serializable {
  private Long personId;
  private String firstname;
  private String lastname;
  private String siret;
  private String city;
  private Double salary;
  private String externalCategory;
  private Double homeRent;  // 30% of salary

  @Override
  public String toString() {
    return String.format("ExternalPerson{id=%d, category=%s, homeRent=%.2f}",
        personId, externalCategory, homeRent);
  }
}