package com.udacity.jdnd.livespring.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Task {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.AUTO)
  private String id;
  private String title;
  private String description;
  private int duration;
  @Enumerated(EnumType.STRING)
  private Status status = Status.CREATED;
}
