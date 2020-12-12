package com.example.demo.dataAccessObject;

import com.example.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobRepo extends JpaRepository<Job, Integer> {
    @Query(value = "SELECT job_id FROM job ORDER BY job_id DESC LIMIT 0, 1", nativeQuery = true)
    int getMaxId();
}
