package com.kora.batch.events.batch;

import com.kora.batch.events.config.BatchTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@SpringBatchTest
@Import(BatchTestConfig.class)
@TestPropertySource(properties = "spring.batch.job.enabled=false")
class BatchIntegrationTest {

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private Job categorizePersonsJob;

  @BeforeEach
  void setUp() {
    jobLauncherTestUtils.setJob(categorizePersonsJob);
  }

  @Test
  void testFullJobExecution() throws Exception {
    // Given
    JobParameters params = new JobParametersBuilder()
        .addLong("run.id", System.currentTimeMillis())
        .toJobParameters();

    // When
    JobExecution execution = jobLauncherTestUtils.launchJob(params);

    // Then
    assertEquals(BatchStatus.COMPLETED, execution.getStatus());
    assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());

    // Verify output files
    File categoriesFile = new File("categories.csv");
    File externalFile = new File("external.csv");

    assertTrue(categoriesFile.exists(), "categories.csv should exist");
    assertTrue(externalFile.exists(), "external.csv should exist");

    List<String> categoriesLines = readLines(categoriesFile);
    List<String> externalLines = readLines(externalFile);

    // Verify headers
    assertEquals("personId,firstname,lastname,siret,city,salary,calculatedCategory",
        categoriesLines.get(0));
    assertEquals("personId,firstname,lastname,siret,city,salary,externalCategory,homeRent",
        externalLines.get(0));

    // Verify no external persons in categories file
    for (int i = 1; i < categoriesLines.size(); i++) {
      String[] fields = categoriesLines.get(i).split(",");
      String siret = fields[3];
      assertFalse(siret.startsWith("7"),
          "Categories file should not contain external siret: " + siret);
    }

    // Verify only external persons in external file
    for (int i = 1; i < externalLines.size(); i++) {
      String[] fields = externalLines.get(i).split(",");
      String siret = fields[3];
      assertTrue(siret.startsWith("7"),
          "External file should only contain external siret: " + siret);
    }

    // Cleanup
    categoriesFile.delete();
    externalFile.delete();
  }

  @Test
  void testParallelStepExecution() throws Exception {
    JobExecution execution = jobLauncherTestUtils.launchJob();

    // Verify both steps executed
    List<String> stepNames = execution.getStepExecutions().stream()
        .map(StepExecution::getStepName)
        .toList();

    assertTrue(stepNames.contains("standardStep"));
    assertTrue(stepNames.contains("externalStep"));
  }

  private List<String> readLines(File file) throws Exception {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    return lines;
  }
}