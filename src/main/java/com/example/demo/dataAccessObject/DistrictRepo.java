package com.example.demo.dataAccessObject;

import com.example.demo.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepo extends JpaRepository<District, Integer> {
}
