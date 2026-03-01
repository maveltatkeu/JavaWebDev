package com.udacity.tdd.swagger.exception;

public class ProjectNotFoundException extends RuntimeException {

  public ProjectNotFoundException(String message) {
    super(message);
  }
}
