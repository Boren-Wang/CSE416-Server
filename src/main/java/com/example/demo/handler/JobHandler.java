package com.example.demo.handler;

import com.example.demo.dataAccessObject.JobRepo;
import com.example.demo.enumerate.Status;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobHandler {
    @Autowired
    JobRepo jobRepo;

    public Job submitJob(Job job) {
        job.setStatus(Status.NEW);
        jobRepo.save(job);

        // Seawulf

        return job;
    }

    public Job getJob(int jobId) {
        Optional<Job> jobOptional = jobRepo.findById(jobId);

        if(jobOptional.isPresent()) {
            return jobOptional.get();
        } else {
            return null;
        }
    }

    public List<Job> getJobHistory() {
        return jobRepo.findAll();
    }

    public Job updateJob(Job job) {
        jobRepo.save(job);
        return job;
    }

    public void cancelJob(int jobId) {
        // Seawulf
    }

    public void deleteJob(int jobId) {
        Job job = jobRepo.getOne(jobId);
        jobRepo.delete(job);
    }

    public void setSummary(int jobId, List<Box> summary) {
        Job job = getJob(jobId);
        job.setSummary(summary);

        for(Box box : summary) {
            box.setJob(job);
        }

        jobRepo.save(job);
    }

    public void setAverage(int jobId, Districting average) {
        Job job = getJob(jobId);
        job.setAverage(average);

        for(District district : average.getDistricts()){
            district.setDistricting(average);
            for(Precinct precinct : district.getPrecincts()){
                precinct.setDistrict(district);
            }
        }

        jobRepo.save(job);
    }

    public void setExtreme(int jobId, Districting extreme) {
        Job job = getJob(jobId);
        job.setAverage(extreme);

        for(District district : extreme.getDistricts()){
            district.setDistricting(extreme);
            for(Precinct precinct : district.getPrecincts()){
                precinct.setDistrict(district);
            }
        }

        jobRepo.save(job);
    }

    public void setRandom(int jobId, Districting random) {
        Job job = getJob(jobId);
        job.setAverage(random);

        for(District district : random.getDistricts()){
            district.setDistricting(random);
            for(Precinct precinct : district.getPrecincts()){
                precinct.setDistrict(district);
            }
        }

        jobRepo.save(job);
    }
}
