package com.example.demo.controller;

import com.example.demo.dao.BoxRepo;
import com.example.demo.dao.JobRepo;
import com.example.demo.enumerate.Status;
import com.example.demo.model.Box;
import com.example.demo.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class JobController {
    @Autowired
    JobRepo jobRepo;

    @Autowired
    BoxRepo boxRepo;

    @GetMapping("api/jobs")
    public List<Job> getJobs() {
        return jobRepo.findAll();
    }

    @GetMapping("api/job/{jobId}")
    public Optional<Job> getJob(@PathVariable("jobId") int jobId) {
        return jobRepo.findById(jobId);
    }

    @PostMapping("api/job")
    public Job addJob(@RequestBody Job job) { // add @RequestBody if body is raw
        job.setStatus(Status.NEW);
        jobRepo.save(job);
        List<Box> summary = job.getSummary();
        for(Box box: summary) {
            box.setJob(job);
            boxRepo.save(box);
        }
        return job;
    }

    @PutMapping("api/job")
    public Job updateJob(@RequestBody Job job) { // add @RequestBody if body is raw
        jobRepo.save(job);
        return job;
    }

    @DeleteMapping("api/job/{jobId}")
    public String deleteJob(@PathVariable("jobId") int jobId) {
        Job job = jobRepo.getOne(jobId);
        jobRepo.delete(job);
        return "Deleted job "+jobId;
    }
}
