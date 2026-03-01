package com.udacity.jdnd.livecodingbatch.processor;

import com.udacity.jdnd.livecodingbatch.model.Categorie;
import com.udacity.jdnd.livecodingbatch.model.TaskInput;
import com.udacity.jdnd.livecodingbatch.model.TaskOutput;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class TaskItemProcessor implements ItemProcessor<TaskInput, TaskOutput> {
  @Override
  public @Nullable TaskOutput process(TaskInput item) throws Exception {
    TaskOutput t_out = new TaskOutput();
    t_out.setId(item.getId());
    t_out.setPriority(item.getPriority());
    t_out.setTitle(item.getTitle());
    t_out.setCategory(item.getPriority() == 3 ? Categorie.CRITICAL.name() : (item.getPriority() == 2 ? Categorie.IMPORTANT.name() : Categorie.NORMAL.name()));

    return t_out;
  }
}
