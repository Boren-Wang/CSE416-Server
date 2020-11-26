package com.example.demo.handler;

import com.example.demo.model.Job;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SeawulfHandler {
    private final int THRESHOLD = 5000;

    private int printProcessOutput(Process process) {
        int jobId = -1;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.contains("Submitted job")) {
                   String jobIdString  = line.split("job ")[1];
                   jobId = Integer.valueOf(jobIdString);
                }
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobId;
    }

    public int dispatchJob(Job job) throws Exception{
        int jobId;
        if(job.getNumberOfDistrictings() > THRESHOLD) {
            ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/trigger.sh");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            jobId = printProcessOutput(process);
            System.out.println("jobId: "+jobId);
            return jobId;
        } else {
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/multiproc.py");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            jobId = -1;
            return jobId;
        }
    }

    public void cancelJob(int jobId) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/cancel.sh", Integer.toString(jobId));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        printProcessOutput(process);
    }
}