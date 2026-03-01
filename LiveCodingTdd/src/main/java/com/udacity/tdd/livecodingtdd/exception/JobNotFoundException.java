package com.udacity.tdd.livecodingtdd.exception;

public class JobNotFoundException extends RuntimeException {

  public JobNotFoundException(String message) {
    super(message);
  }
}
