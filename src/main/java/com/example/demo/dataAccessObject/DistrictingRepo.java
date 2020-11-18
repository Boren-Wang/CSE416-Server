package com.example.demo.dataAccessObject;

import com.example.demo.model.Districting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictingRepo extends JpaRepository<Districting, Integer> {

}
