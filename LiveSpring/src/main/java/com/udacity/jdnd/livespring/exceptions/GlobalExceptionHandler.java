package com.udacity.jdnd.livespring.exceptions;

import com.udacity.jdnd.livespring.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      NotFoundException ex,
      HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.builder()
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .timeStamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(
      Exception ex,
      HttpServletRequest request) {

    ErrorResponse response = ErrorResponse.builder()
        .timeStamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .path(request.getRequestURI())
        .message(ex.getMessage())
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }


}
