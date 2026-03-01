package com.udacity.tdd.swagger.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProjectNotFoundException.class)
  public ProblemDetail handleProjectNotFound(
      ProjectNotFoundException ex,
      HttpServletRequest request) {

    ProblemDetail prob = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    prob.setTitle("Project not Found!");
    prob.setDetail(ex.getMessage() + " Check your data and try again!");
    prob.setProperty("TimeStamp", Instant.now());

    return prob;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(
      ProjectNotFoundException ex,
      HttpServletRequest request) {

    ProblemDetail prob = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    prob.setTitle("Internal Error!");
    prob.setDetail("And unexpected error occured: " + ex.getMessage());
    prob.setProperty("TimeStamp", Instant.now());

    return prob;
  }
}
