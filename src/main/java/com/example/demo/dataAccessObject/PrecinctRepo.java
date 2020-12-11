package com.example.demo.dataAccessObject;

import com.example.demo.model.Precinct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrecinctRepo extends JpaRepository<Precinct, Integer> {

}
