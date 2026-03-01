package com.udacity.tdd.studentmanagmnt.exception;

public class DuplicateEmailException extends RuntimeException {
  public DuplicateEmailException(String message) {
    super(message);
  }
}
