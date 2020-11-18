package com.example.demo.dataAccessObject;

import com.example.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepo extends JpaRepository<Job, Integer> {

}
