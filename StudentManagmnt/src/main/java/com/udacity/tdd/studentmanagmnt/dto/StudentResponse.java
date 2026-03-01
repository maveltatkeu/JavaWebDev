package com.udacity.tdd.studentmanagmnt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.udacity.tdd.studentmanagmnt.model.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String studentNumber,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth,
    String phoneNumber,
    StudentStatus status,
    String major,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime updatedAt
) {
}