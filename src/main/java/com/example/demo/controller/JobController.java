package com.example.demo.controller;

import com.example.demo.dao.JobRepo;
import com.example.demo.enumerate.Status;
import com.example.demo.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class JobController {
    @Autowired
    JobRepo repo;

    @GetMapping("/jobs")
    public List<Job> getJobs() {
        return repo.findAll();
    }

    @GetMapping("/job/{jobId}")
    public Optional<Job> getJob(@PathVariable("jobId") int jobId) {
        return repo.findById(jobId);
    }

    @PostMapping("/job")
    public Job addJob(Job job) { // add @RequestBody if body is raw
        job.setStatus(Status.NEW);
        repo.save(job);
        return job;
    }

    @PutMapping("/job")
    public Job updateJob(Job job) { // add @RequestBody if body is raw
        repo.save(job);
        return job;
    }

    @DeleteMapping("/job/{jobId}")
    public String deleteJob(@PathVariable("jobId") int jobId) {
        Job job = repo.getOne(jobId);
        repo.delete(job);
        return "Deleted job "+jobId;
    }
}
