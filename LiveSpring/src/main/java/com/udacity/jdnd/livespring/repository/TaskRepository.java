package com.udacity.jdnd.livespring.repository;

import com.udacity.jdnd.livespring.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
  Task getTaskById(String id);
}
