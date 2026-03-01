package com.udacity.tdd.swagger.service;

import com.udacity.tdd.swagger.model.Project;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProjectService {
  Project saveProject(Project project);

  Project updateProject(Project project);

  Project getProject(String projId);

  List<Project> getProjects();

  void deleteProject(String projId);

  CompletableFuture<Project> processProject(Project project);
}
