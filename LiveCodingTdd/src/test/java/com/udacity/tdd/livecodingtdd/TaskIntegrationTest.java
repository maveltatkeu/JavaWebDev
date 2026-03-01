package com.udacity.tdd.livecodingtdd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("Creer une tache et retourner 201 created")
  public void shouldCreateTask() {
    //Given
    Map<String, Object> taskRequest = new HashMap<>();

    taskRequest.put("title", "Preparer l'entretien");
    taskRequest.put("description", "Reviser le TDD");
    taskRequest.put("duration", 60);
    taskRequest.put("status", "TODO");
    taskRequest.put("startDate", LocalDate.now());

    //When
    ResponseEntity<Map> response = restTemplate.postForEntity("/api/tasks",taskRequest, Map.class);

    //Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody().get("id")).isNotNull();

  }
}
