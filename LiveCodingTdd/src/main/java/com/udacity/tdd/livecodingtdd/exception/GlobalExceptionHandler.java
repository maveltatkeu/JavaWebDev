package com.udacity.tdd.livecodingtdd.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JobNotFoundException.class)
  public ProblemDetail handleJobNotFoundException(
      JobNotFoundException ex, HttpServletRequest request) {

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

    problemDetail.setTitle("Failed to fetch your job!");
    problemDetail.setDetail("Check your data and retry! " + ex.getMessage());
    problemDetail.setProperty("Timestamp", Instant.now());

    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception ex, HttpServletRequest request){

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

    problemDetail.setTitle("Internal Error");
    problemDetail.setDetail("An error occured: " + ex.getMessage());
    problemDetail.setProperty("Timestamp", Instant.now());

    return problemDetail;
  }

}
