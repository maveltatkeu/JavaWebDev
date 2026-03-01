package com.udacity.tdd.livecodingtdd.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;

@Entity
@Data
public class Job {
  @Id
  @UuidGenerator(style = UuidGenerator.Style.AUTO)
  private String id;

  private String jobTitle;
  private String jobDescription;
  private double jobSalary;
  private String jobLocation;
  private LocalDate jobStartDate;

  @Enumerated(EnumType.STRING)
  private JobStatus jobStatus = JobStatus.CREATED;

}
