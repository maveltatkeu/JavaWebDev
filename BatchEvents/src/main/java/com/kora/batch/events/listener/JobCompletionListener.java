package com.kora.batch.events.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job started: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("Job completed successfully!");
            System.out.println("Output written to: categories.csv");
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            System.out.println("Job failed with status: " + jobExecution.getStatus());
        }
    }
}