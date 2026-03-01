package com.udacity.tdd.livecodingtdd.controller;

import com.udacity.tdd.livecodingtdd.model.Job;
import com.udacity.tdd.livecodingtdd.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

  @Autowired
  private JobService jobService;

  @Operation(summary = "Créer un nouveau Job", description = "Retourne le Job créée avec son ID unique")
  @ApiResponse(responseCode = "201", description = "Job créée avec succès")
  @ApiResponse(responseCode = "400", description = "Données invalides")
  @PostMapping
  public ResponseEntity<Job> createNewJob(@RequestBody Job job) {
    return new ResponseEntity<>(jobService.saveJob(job), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Job>> getAllJobs() {
    return new ResponseEntity<>(jobService.getJobs(), HttpStatus.OK);
  }

  @GetMapping("/{jobId}")
  public ResponseEntity<Job> getJob(@PathVariable("jobId") String jobId) {
    return new ResponseEntity<>(jobService.getJob(jobId), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Job> updateJob(@RequestBody Job job) {
    return new ResponseEntity<>(jobService.updateJob(job), HttpStatus.OK);
  }

  @DeleteMapping("/{jobId}")
  public ResponseEntity<String> deleteJob(@PathVariable("jobId") String jobId) {
    jobService.deleteJob(jobId);
    return ResponseEntity.ok("Job deleted Successfully");
  }

  @PostMapping("/process")
  public CompletableFuture<Job> processJob(@RequestBody Job job) {
    return jobService.processJob(job);
  }


}
