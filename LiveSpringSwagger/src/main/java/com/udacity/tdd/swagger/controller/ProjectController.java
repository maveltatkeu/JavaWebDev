package com.udacity.tdd.swagger.controller;

import com.udacity.tdd.swagger.model.Project;
import com.udacity.tdd.swagger.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

  @Autowired
  private ProjectService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Project> createProject(@RequestBody @Valid Project project) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.saveProject(project));
  }

  @GetMapping
  public ResponseEntity<List<Project>> getAllProjects() {
    return new ResponseEntity<>(service.getProjects(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Project> getProject(@PathVariable("id") String id) {
    return ResponseEntity.ok(service.getProject(id));
  }

  @PutMapping
  public ResponseEntity<Project> updateProject(@RequestBody @Valid Project proj) {
    return ResponseEntity.ok(service.updateProject(proj));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteProject(@PathVariable("id") String id) {
    service.deleteProject(id);
    return ResponseEntity.ok("Project deleted SUCEESSFULLY!");
  }

  @PostMapping("/process")
  public CompletableFuture<Project> processProject(@RequestBody @Valid Project project) {
    return service.processProject(project);
  }

}
