package com.udacity.tdd.livecodingtdd.repository;

import com.udacity.tdd.livecodingtdd.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, String> {
}
