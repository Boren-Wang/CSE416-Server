package com.example.demo.dao;

import com.example.demo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface JobRepo extends JpaRepository<Job, Integer> {

}
