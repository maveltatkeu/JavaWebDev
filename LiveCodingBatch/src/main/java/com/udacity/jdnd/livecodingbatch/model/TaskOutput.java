package com.udacity.jdnd.livecodingbatch.model;

import lombok.Data;

@Data
public class TaskOutput {
  private Long id;
  private String title;
  private int priority;
  private String category;

  @Override
  public String toString() {
    return id + "|" + title + "|" + priority + "|" + category;
  }
}
