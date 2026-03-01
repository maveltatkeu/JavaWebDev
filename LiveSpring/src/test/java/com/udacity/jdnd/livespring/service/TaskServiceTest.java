package com.udacity.jdnd.livespring.service;

import com.udacity.jdnd.livespring.model.Status;
import com.udacity.jdnd.livespring.model.Task;
import com.udacity.jdnd.livespring.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {

  private final TaskRepository repository = mock(TaskRepository.class);
  private final TaskService service = new TaskService(repository);

  @Test
  public void testCreateTask() {

    Task task = Task.builder()
        .title("Task title")
        .description("Task description")
        .duration(20)
        .status(Status.CREATED).build();

    Task createdTask = Task.builder()
        .id("123")
        .title("Task title")
        .description("Task description")
        .duration(20)
        .status(Status.CREATED).build();

    when(service.saveTask(task)).thenReturn(createdTask);

    var result = service.saveTask(task);

    assertNotNull(result.getId());
    verify(repository, times(1)).save(task);
  }
}