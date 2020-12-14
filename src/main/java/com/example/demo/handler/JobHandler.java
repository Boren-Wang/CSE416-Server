package com.example.demo.handler;

import com.example.demo.dataAccessObject.JobRepo;
import com.example.demo.enumerate.Status;
import com.example.demo.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Service
public class JobHandler {
    @Autowired
    JobRepo jobRepo;

    @Autowired
    SeawulfHandler sh;

    public Job submitJob(Job job) throws Exception {
//        int maxJobId = jobRepo.getMaxId();
//        job.setJobId(maxJobId++);

        int jobId = sh.dispatchJob(job);
        if(jobId==-1) {
            FileInputStream in = new FileInputStream("src/main/resources/application.properties");
            Properties props = new Properties();
            props.load(in);
            in.close();

            String max_job_id = props.getProperty("max_job_id");
            job.setJobId(Integer.valueOf(max_job_id)+1);

            // update max_job_id in application.properties
            FileOutputStream out = new FileOutputStream("src/main/resources/application.properties");
            props.setProperty("max_job_id", String.valueOf(job.getJobId()));
            props.store(out, null);
            out.close();

            job.setStatus("Run in server");
        } else {
            job.setJobId(jobId);
            job.setStatus("Pending");
        }
        jobRepo.save(job);
//        System.out.println("Submitted job: "+job);
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

    public List<Job> getJobHistory() throws Exception {
        List<Job> jobHistory = jobRepo.findAll();
        return jobHistory;
    }

    public String getJobUpdates() throws Exception {
//        asynchronously get all completed jobs and process their results
//        sh.getCompletedJobs();

        List<Job> jobHistory = jobRepo.findAll();
        Map<Integer, String> dict = sh.getUpdates(); // dict stores the current status of each job
        for(Job job : jobHistory) {
            if(dict.get(job.getJobId())!=null) {
                job.setStatus(dict.get(job.getJobId())); // update the status of each job
                jobRepo.save(job);
            }
        }
        String result = dict.get(-1);
        return result;
    }

    public Job updateJob(Job job) {
        jobRepo.save(job);
        return job;
    }

    public void cancelJob(int jobId) throws Exception {
        sh.cancelJob(jobId);
        Optional<Job> jobOptional = jobRepo.findById(jobId);
        if(jobOptional.isPresent()) {
            jobRepo.delete(jobOptional.get());
        }
        System.out.println("Canceled job: "+jobId);
    }

    public void deleteJob(int jobId) throws Exception {
        Optional<Job> jobOptional = jobRepo.findById(jobId);
        if(jobOptional.isPresent()) {
            jobRepo.delete(jobOptional.get());
        }
        System.out.println("Deleted job: "+jobId);
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
//            for(Precinct precinct : district.getPrecincts()){
//                precinct.setDistrict(district);
//            }
        }

        jobRepo.save(job);
    }

    public void setExtreme(int jobId, Districting extreme) {
        Job job = getJob(jobId);
        job.setAverage(extreme);

        for(District district : extreme.getDistricts()){
            district.setDistricting(extreme);
//            for(Precinct precinct : district.getPrecincts()){
//                precinct.setDistrict(district);
//            }
        }

        jobRepo.save(job);
    }

    public void setRandom(int jobId, Districting random) {
        Job job = getJob(jobId);
        job.setAverage(random);

        for(District district : random.getDistricts()){
            district.setDistricting(random);
//            for(Precinct precinct : district.getPrecincts()){
//                precinct.setDistrict(district);
//            }
        }

        jobRepo.save(job);
    }
}
