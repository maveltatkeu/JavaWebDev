package com.udacity.tdd.livecodingtdd.service;

import com.udacity.tdd.livecodingtdd.model.Job;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public interface JobService {

  Job saveJob(Job job);

  Job getJob(String jobId);

  List<Job> getJobs();

  Job updateJob(Job updtJob);

  void deleteJob(String jobId);

  CompletableFuture<Job> processJob(Job job);
}
