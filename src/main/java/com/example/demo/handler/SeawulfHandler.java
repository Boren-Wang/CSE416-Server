package com.example.demo.handler;

import com.example.demo.model.Job;
import org.springframework.stereotype.Service;

@Service
public class SeawulfHandler {
    private final int THRESHOLD = 5000;

    public void dispatchJob(Job job) throws Exception{
        if(job.getNumberOfDistrictings() > THRESHOLD) {
            // run in seawulf
//            ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/trigger.sh");
//            Process process = pb.start();
            System.out.println("Run in Seawulf!");
        } else {
            // run in server
//            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/algorithm.py");
//            pb.redirectErrorStream(true);
//            Process process = pb.start();
            System.out.println("Run in Server!");
        }
    }
}