package com.example.demo.handler;

import com.example.demo.dataAccessObject.JobRepo;
import com.example.demo.model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class SeawulfHandler {
    private final int THRESHOLD = 8;

    @Autowired
    JobRepo jobRepo;

    @Autowired
    AlgorithmHandler ah;

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
                    Double.toString(job.getPopulationDifference()),
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
                    Double.toString(job.getPopulationDifference()),
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
                    } else if (status.equals("R")) {
                        dict.put(jobId, "Running");
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

    public void getCompletedJobs() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/list_results.sh");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Map<Integer, String> dict = new HashMap<>();
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
                if(line.contains("json")) {
                    String jobId = line.split(Pattern.quote("."))[0];
                    Optional<Job> job = jobRepo.findById(Integer.valueOf(jobId));
                    if(job.isPresent()) {
                        if(job.get().getStatus()=="Processing" || job.get().getStatus()=="Completed") {
                            continue;
                        } else {
                            System.out.println("Update: job "+job.get().getJobId()+" is completed!");
                            job.get().setStatus("Processing");
                            // move result json to server
                            moveFileToServer(job.get().getJobId());

                            // start processing the result
//                            ah.processResult(job.get().getJobId());
                        }
                    }
                }
            }
            String result = builder.toString();
            System.out.println(result);
            for(int key : dict.keySet()) {
                System.out.println(Integer.toString(key)+"-"+dict.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveFileToServer(int jobId) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("bash", "src/main/resources/move_file.sh", Integer.toString(jobId));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            Map<Integer, String> dict = new HashMap<>();
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
            for(int key : dict.keySet()) {
                System.out.println(Integer.toString(key)+"-"+dict.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}