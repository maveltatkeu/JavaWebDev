package com.udacity.jdnd.labc3;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;

@Data
@Entity
@Table
public class Dog {

  @Id
  private Long id;
  private String name;
  private List<String> breed;
  private String origin;
}
