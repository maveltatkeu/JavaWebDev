package com.udacity.jdnd.livecodingbatch.model;

import lombok.Data;

@Data
public class TaskInput {
  private Long id;
  private String title;
  private int priority;
}
