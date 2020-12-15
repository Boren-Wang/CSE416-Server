package com.example.demo.controller;

import com.example.demo.handler.AlgorithmHandler;
import com.example.demo.handler.MapHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    AlgorithmHandler ah;

    @GetMapping("api/test/{jobId}")
    public void test(@PathVariable("jobId") int jobId) throws Exception{
        ah.processResult(jobId);
    }
}
