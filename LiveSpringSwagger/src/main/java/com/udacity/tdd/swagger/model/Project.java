package com.udacity.tdd.swagger.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;

@Entity
@Data
public class Project {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.AUTO)
  private String id;

  @Column(name = "title", length = 10,nullable = false)
  private String title;

  @Column(name = "description", length = 10,nullable = false)
  private String description;

  private Integer duration;
  private LocalDate startDate;
  private String person;

  @Enumerated(EnumType.STRING)
  private ProjectStatus projectStatus = ProjectStatus.STARTED;

}
