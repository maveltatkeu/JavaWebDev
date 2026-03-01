package com.udacity.tdd.studentmanagmnt.dto;


import org.hibernate.annotations.processing.Pattern;

public record StudentRequest(
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name contains invalid characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name contains invalid characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,

    @NotBlank(message = "Student number is required")
    @Pattern(regexp = "^[A-Z]{2}\\d{6}$", message = "Student number must be in format XX123456")
    String studentNumber,

    @NotNull(message = "Date of birth is required")
    @ValidStudentAge(min = 16, max = 100, message = "Student must be between 16 and 100 years old")
    java.time.LocalDate dateOfBirth,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @NotBlank(message = "Major is required")
    @Size(max = 50, message = "Major cannot exceed 50 characters")
    String major
) {}