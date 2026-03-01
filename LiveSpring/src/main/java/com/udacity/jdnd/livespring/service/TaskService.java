package com.udacity.jdnd.livespring.service;

import com.udacity.jdnd.livespring.exceptions.NotFoundException;
import com.udacity.jdnd.livespring.model.Status;
import com.udacity.jdnd.livespring.model.Task;
import com.udacity.jdnd.livespring.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  public Task saveTask(Task task) {
    return taskRepository.save(task);
  }

  public Task getTask(String id) {
    return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
  }

  public List<Task> getTasks() {
    return taskRepository.findAll();
  }

  public Task updateTask(String id, Task updtTask) {
    return taskRepository.findById(id).map(t -> {
      t.setTitle(updtTask.getTitle());
      t.setDescription(updtTask.getDescription());
      t.setDuration(updtTask.getDuration());
      t.setStatus(updtTask.getStatus());
      return taskRepository.save(t);
    }).orElseThrow(() -> new NotFoundException("Update Task Not found"));
  }

  public void deleteTask(String id) {
    if (!taskRepository.existsById(id)) {
      throw new NotFoundException("Task not found with id " + id);
    }
    taskRepository.deleteById(id);
  }

  @Async
  public CompletableFuture<Task> processTask(Task task) {

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    task.setStatus(Status.PENDING);
    taskRepository.save(task);

    try {
      TimeUnit.SECONDS.sleep(8);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    task.setStatus(Status.CHECKING);
    taskRepository.save(task);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    task.setStatus(Status.COMPLETED);
    taskRepository.save(task);
    return CompletableFuture.completedFuture(task);
  }
}
