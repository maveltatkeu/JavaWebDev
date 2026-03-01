package com.udacity.jdnd.livespring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.jdnd.livespring.model.Status;
import com.udacity.jdnd.livespring.model.Task;
import com.udacity.jdnd.livespring.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private TaskRepository repository;

  @Autowired
  private ObjectMapper mapper;

  private Task savedTask;

  @BeforeEach
  void setup() {
    repository.deleteAll();
    savedTask = repository.save(
        Task.builder()
            .title("Initial title")
            .description("Initial descr001")
            .duration(234)
            .status(Status.CREATED)
            .build()
    );
  }

  // =========================
  // GET ALL
  // =========================
  @Test
  void shouldReturnAllTasks() throws Exception {
    mockMvc.perform(get("/v1/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].title").value("Initial title"));
  }

  // =========================
  // GET BY ID - OK
  // =========================
  @Test
  void shouldReturnTaskById() throws Exception {
    mockMvc.perform(get("/v1/tasks/{id}", savedTask.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(savedTask.getId()))
        .andExpect(jsonPath("$.title").value("Initial title"));
  }

  // =========================
  // GET BY ID - NOT FOUND
  // =========================
  @Test
  void shouldReturn404WhenTaskNotFound() throws Exception {
    mockMvc.perform(get("/v1/tasks/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Task not found with id: 999"))
        .andExpect(jsonPath("$.path").value("/v1/tasks/999"));
  }

  // =========================
  // CREATE
  // =========================
  @Test
  void shouldCreateTask() throws Exception {
    Task task = Task.builder()
        .title("New task")
        .description("New description")
        .build();

    mockMvc.perform(post("/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(task)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.title").value("New task"));
  }

  // =========================
  // UPDATE
  // =========================
  @Test
  void shouldUpdateTask() throws Exception {
    Task updated = Task.builder()
        .title("Updated title")
        .description("Updated description")
        .status(Status.PENDING)
        .build();

    mockMvc.perform(put("/v1/tasks/{id}", savedTask.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated title"))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  // =========================
  // DELETE
  // =========================
  @Test
  void shouldDeleteTask() throws Exception {
    mockMvc.perform(delete("/v1/tasks/{id}", savedTask.getId()))
        .andExpect(status().isOk());

    mockMvc.perform(get("/v1/tasks/{id}", savedTask.getId()))
        .andExpect(status().isNotFound());
  }

  // =========================
  // ASYNC PROCESS
  // =========================
//  @Test
//  void shouldProcessTaskAsync() throws Exception {
//    mockMvc.perform(post("/v1/tasks/{id}/process", savedTask.getId())
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(mapper.writeValueAsString(savedTask)))
//        .andExpect(status().isOk())
//        .andExpect(jsonPath("$.status").value("COMPLETED"));
//  }
}