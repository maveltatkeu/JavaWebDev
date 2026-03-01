package com.udacity.tdd.studentmanagmnt.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(StudentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException ex, WebRequest request) {
    log.warn("Student not found: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, WebRequest request) {
    log.warn("Duplicate entry: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    log.warn("Validation failed: {}", errors);

    ValidationErrorResponse response = new ValidationErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        errors,
        LocalDateTime.now(),
        request.getDescription(false)
    );

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {

    log.warn("Constraint violation: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred. Please contact support.", request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String message, WebRequest request) {

    ErrorResponse response = new ErrorResponse(
        status.value(),
        status.getReasonPhrase(),
        message,
        LocalDateTime.now(),
        request.getDescription(false)
    );

    return ResponseEntity.status(status).body(response);
  }


}