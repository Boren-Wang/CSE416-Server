package com.example.demo.dao;

import com.example.demo.model.Box;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoxRepo extends JpaRepository<Box, Integer> {

}
