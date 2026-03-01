package com.udacity.tdd.studentmanagmnt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_student_number", columnList = "studentNumber", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String firstName;

  @Column(nullable = false, length = 50)
  private String lastName;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, unique = true, length = 20)
  private String studentNumber;

  @Column(nullable = false)
  private LocalDate dateOfBirth;

  @Column(length = 20)
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private StudentStatus status = StudentStatus.ACTIVE;

  @Column(nullable = false)
  private String major;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @Version
  private Long version;
}