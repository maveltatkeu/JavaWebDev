package com.udacity.tdd.swagger.service;

import com.udacity.tdd.swagger.exception.ProjectNotFoundException;
import com.udacity.tdd.swagger.model.Project;
import com.udacity.tdd.swagger.model.ProjectStatus;
import com.udacity.tdd.swagger.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

  private final ProjectRepository repository;

  @Override
  public Project saveProject(Project project) {
    return repository.save(project);
  }

  @Override
  public Project updateProject(Project project) {
    return repository.findById(project.getId())
        .map(proj -> {
          proj.setProjectStatus(project.getProjectStatus());
          proj.setDuration(project.getDuration());
          proj.setDescription(project.getDescription());
          proj.setPerson(project.getPerson());
          proj.setStartDate(project.getStartDate());
          proj.setTitle(project.getTitle());

          return repository.save(proj);
        }).orElseThrow(() -> new ProjectNotFoundException("Failed to update the project with ID: " + project.getId()));
  }

  @Override
  public Project getProject(String projId) {
    return repository.findById(projId)
        .orElseThrow(() -> new ProjectNotFoundException("Failed to get the project with ID: " + projId));
  }

  @Override
  public List<Project> getProjects() {
    return repository.findAll();
  }

  @Override
  public void deleteProject(String projId) {
    if (!repository.existsById(projId))
      throw new ProjectNotFoundException("Failed to delete the project with ID: " + projId);

    repository.deleteById(projId);
  }

  @Async
  @Override
  public CompletableFuture<Project> processProject(Project project) {

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      log.error("Failled to START the project!");
    }

    project.setProjectStatus(ProjectStatus.STARTED);
    repository.save(project);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      log.error("Failled to PROCESS the project!");
    }

    project.setProjectStatus(ProjectStatus.PENDING);
    repository.save(project);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      log.error("Failled to COMPLETE the project!");
    }

    project.setProjectStatus(ProjectStatus.COMPLETED);
    repository.save(project);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      log.error("Failled to VALIDATE the project!");
    }

    project.setProjectStatus(ProjectStatus.VALIDATED);
    repository.save(project);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      log.error("Failled to CLOSE the project!");
    }

    project.setProjectStatus(ProjectStatus.CLOSED);
    repository.save(project);

    return CompletableFuture.completedFuture(project);
  }
}
