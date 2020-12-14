package com.example.demo.controller;

import com.example.demo.handler.JobHandler;
import com.example.demo.handler.SeawulfHandler;
import com.example.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@RestController
public class JobController {
    @Autowired
    JobHandler jh;

    @Autowired
    SeawulfHandler sh;

    @PostMapping("api/job")
    public Job addJob(@RequestBody Job job) throws Exception{
        jh.submitJob(job);
        return job;
    }

    @GetMapping("api/jobs")
    public List<Job> getJobs() throws Exception {
        return jh.getJobHistory();
    }

    @GetMapping("api/jobs/update")
    public String getJobUpdates() throws Exception {
        return jh.getJobUpdates();
    }

    @GetMapping("api/jobs/update/completed")
    public void getCompletedJobs() throws Exception {
        sh.getCompletedJobs();
    }

    @GetMapping("api/jobs/update/files/{jobId}")
    public void moveFiles(@PathVariable("jobId") int jobId) throws Exception {
        sh.moveFileToServer(jobId);
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

    @RequestMapping("/api/job/{jobId}/log")
    public String downloadJobLog(HttpServletResponse response, @PathVariable("jobId") int jobId) {
        File file = new File("src/main/resources/results/"+jobId+".log");
        if(!file.exists()){
            return "No log file for job "+jobId+" found";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + jobId + ".log" );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
            return "Download Error";
        }
        return "Download Success";
    }

    @RequestMapping("/api/job/{jobId}/summary")
    public String downloadJobSummary(HttpServletResponse response, @PathVariable("jobId") int jobId) {
        File file = new File("src/main/resources/results/"+jobId+"_summary.json");
        if(!file.exists()){
            return "No summary file for job "+jobId+" found";
        }
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + jobId + "_summary.json" );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
            return "Download Error";
        }
        return "Download Success";
    }
}
