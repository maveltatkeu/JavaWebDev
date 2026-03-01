package com.udacity.tdd.livecodingtdd.service;

import com.udacity.tdd.livecodingtdd.exception.JobNotFoundException;
import com.udacity.tdd.livecodingtdd.model.Job;
import com.udacity.tdd.livecodingtdd.model.JobStatus;
import com.udacity.tdd.livecodingtdd.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService{

  private final JobRepository repository;

  @Override
  public Job saveJob(Job job) {
    return repository.save(job);
  }

  @Override
  public Job getJob(String jobId) {
    return repository.findById(jobId).orElseThrow(() -> new JobNotFoundException("The Job you are looking does mot exist. ID=" + jobId));
  }

  @Override
  public List<Job> getJobs() {
    return repository.findAll();

  }

  @Override
  public Job updateJob(Job updtJob) {
    return repository.findById(updtJob.getId()).map(job -> {
      job.setJobLocation(updtJob.getJobLocation());
      job.setJobDescription(updtJob.getJobDescription());
      job.setJobStartDate(updtJob.getJobStartDate());
      job.setJobSalary(updtJob.getJobSalary());
      job.setJobStatus(updtJob.getJobStatus());
      job.setJobTitle(updtJob.getJobTitle());

      return repository.save(job);
    }).orElseThrow(() -> new JobNotFoundException("Failed to update the job with ID: " + updtJob.getId() + " Job not exist"));
  }

  @Override
  public void deleteJob(String jobId) {
    if (!repository.existsById(jobId))
      throw new JobNotFoundException("Failed to delete job with ID: " + jobId + " Job not exist");

    repository.deleteById(jobId);
  }

  @Async
  @Override
  public CompletableFuture<Job> processJob(Job job) {

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException ex) {
      log.error("Failed to start the Job");
    }

    job.setJobStatus(JobStatus.STARTED);
    repository.save(job);

    try {
      TimeUnit.SECONDS.sleep(7);
    } catch (InterruptedException ex) {
      log.error("Failed to process the Job");
    }

    job.setJobStatus(JobStatus.PENDING);
    repository.save(job);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException ex) {
      log.error("Failed to complete the Job");
    }

    job.setJobStatus(JobStatus.COMPLETED);
    repository.save(job);

    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException ex) {
      log.error("Failed to close the Job");
    }

    job.setJobStatus(JobStatus.CLOSED);
    repository.save(job);

    return CompletableFuture.completedFuture(job);
  }
}
