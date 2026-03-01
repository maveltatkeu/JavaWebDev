package com.udacity.jdnd.livespring.controller;

import com.udacity.jdnd.livespring.model.Task;
import com.udacity.jdnd.livespring.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    return ResponseEntity.ok(taskService.saveTask(task));
  }

  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    return ResponseEntity.ok(taskService.getTasks());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Task> getTask(@PathVariable("id") String id) {
    return ResponseEntity.ok(taskService.getTask(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@RequestBody Task task, @PathVariable("id") String id) {
    return ResponseEntity.ok(taskService.updateTask(id, task));
  }

  @PostMapping("/{id}/process")
  public CompletableFuture<Task> process(@RequestBody Task task) {
    return taskService.processTask(task);
  }


  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteTask(@PathVariable("id") String id) {
      taskService.deleteTask(id);
      return ResponseEntity.ok("Task Deleted SUCCESSFULLY!");

  }
}
