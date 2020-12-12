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
        int seawulfId = -1;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.contains("Submitted batch job")) {
                   String seawulfIdString  = line.split("job ")[1];
                   seawulfId = Integer.valueOf(seawulfIdString);
                }
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seawulfId;
    }

    public int dispatchJob(Job job) throws Exception{
        int seawulfId;
        if(job.getNumberOfDistrictings() > THRESHOLD) {
            System.out.println("Run in seawulf");
            ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/trigger.sh",
                    Integer.toString(job.getJobId()),
                    job.getState().name(),
                    Integer.toString(job.getNumberOfDistrictings()),
                    Integer.toString(job.getPopulationDifference()),
                    Double.toString(job.getCompactnessGoal()));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            seawulfId = printProcessOutput(process);
//            System.out.println("jobId: "+jobId);
            return seawulfId;
        } else {
            System.out.println("Run in server");
            ProcessBuilder pb = new ProcessBuilder("python", "src/main/resources/algorithm/main.py",
                    Integer.toString(job.getJobId()),
                    job.getState().name(),
                    Integer.toString(job.getNumberOfDistrictings()),
                    Integer.toString(job.getPopulationDifference()),
                    Double.toString(job.getCompactnessGoal())).inheritIO();
            pb.redirectErrorStream(true);
            Process process = pb.start();
            printProcessOutput(process);
            seawulfId = -1;
            return seawulfId;
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
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if(!isFirstLine && line!="") {
                    List<String> parts = Arrays.asList(line.trim().split("\\s+"));
//                    System.out.println(parts);
                    int jobId = Integer.valueOf(parts.get(0));
                    String status = parts.get(4);
                    if(status.equals("PD")) {
                        dict.put(jobId, "Pending");
                    } else {
                        dict.put(jobId, status);
                    }
                }
                if(line.contains("JOBID")) {
                    isFirstLine = false;
                }
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
            for(int key : dict.keySet()) {
                System.out.println(Integer.toString(key)+"-"+dict.get(key));
            }
            return dict;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}