package com.udacity.jdnd.livecodingbatch.writer;

import com.udacity.jdnd.livecodingbatch.model.TaskOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class TaskItemWriter implements ItemWriter<TaskOutput> {
  private final String outputPath = "tasks_output.csv";

  @Override
  public void write(Chunk<? extends TaskOutput> chunk) throws Exception {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      for (TaskOutput t : chunk) {
        writer.write(t.toString());
        writer.write("\n");
      }
    } catch (IOException e) {
      log.error("Error when writing output File");
    }
  }
}
