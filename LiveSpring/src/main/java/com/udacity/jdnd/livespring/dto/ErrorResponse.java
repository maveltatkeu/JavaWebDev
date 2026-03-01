package com.udacity.jdnd.livespring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class ErrorResponse {

  private String error;
  private String message;
  private String path;
  private int status;
  private LocalDateTime timeStamp;
}
