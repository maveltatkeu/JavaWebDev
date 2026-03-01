package com.udacity.jdnd.livespring;

import com.udacity.jdnd.livespring.model.Task;
import com.udacity.jdnd.livespring.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskIntegrationTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void testCreateAndFind() {
        Task task = Task.builder().title("Int Test").description("desc").build();
        repository.save(task);

        Task fetched = repository.findById(task.getId()).orElseThrow();
        assertEquals("Int Test", fetched.getTitle());
    }
}
