package com.example.demo.controller;

import com.example.demo.handler.JobHandler;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobController {
    @Autowired
    JobHandler jh;

    @PostMapping("api/job")
    public Job addJob(@RequestBody Job job) throws Exception{
        jh.submitJob(job);
        return job;
    }

    @GetMapping("api/jobs")
    public List<Job> getJobs() throws Exception {
        return jh.getJobHistory();
    }

    @GetMapping("api/job/{jobId}")
    public Job getJob(@PathVariable("jobId") int jobId) {
        return jh.getJob(jobId);
    }

    @PutMapping("api/job")
    public Job updateJob(@RequestBody Job job) {
        jh.updateJob(job);
        return job;
    }

    @PostMapping("api/job/{jobId}/cancel")
    public String cancelJob(@PathVariable("jobId") int jobId) throws Exception {
        jh.cancelJob(jobId);
        return "Canceled job "+jobId;
    }

    @DeleteMapping("api/job/{jobId}")
    public String deleteJob(@PathVariable("jobId") int jobId) throws Exception {
        jh.deleteJob(jobId);
        return "Deleted job "+jobId;
    }
}
