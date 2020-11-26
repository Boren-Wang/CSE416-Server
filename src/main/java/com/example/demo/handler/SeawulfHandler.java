package com.example.demo.handler;

import com.example.demo.model.Job;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                if(line.contains("Submitted batch job")) {
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
            System.out.println("Run in Seawulf");
            ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/trigger.sh");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            jobId = printProcessOutput(process);
//            System.out.println("jobId: "+jobId);
            return jobId;
        } else {
            System.out.println("Run in Server");
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

    public Map<Integer, String> getUpdates() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/update.sh");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Map<Integer, String> dict = new HashMap<>();
            StringBuilder builder = new StringBuilder();
            String line = null;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if(lineNumber>0 && line!="") {
                    List<String> parts = Arrays.asList(line.trim().split(" "));
//                    System.out.println(parts);
                    int jobId = Integer.valueOf(parts.get(0));
                    String status = parts.get(5);
                    if(status.equals("PD")) {
                        dict.put(jobId, "Pending");
                    } else {
                        dict.put(jobId, status);
                    }
                }
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
                lineNumber++;
            }
            String result = builder.toString();
            System.out.println(result);
            return dict;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}