package com.example.demo.model;

import com.example.demo.enumerate.Minority;
import com.example.demo.enumerate.State;
import com.example.demo.enumerate.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "result"})
public class Job {
    private int jobId;
//    private int seawulfId;
    private State state;
    private String status;
    private int numberOfDistrictings;
    private double compactnessGoal;
    private double populationDifference;
    private Set<Minority> minorities = new HashSet<>();
//    private Result result;
    private List<Box> summary;
    private Districting random;
    private Districting average;
    private Districting extreme;
    private String summaryFilePath;

    @Id
//    @GeneratedValue
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

//    public int getSeawulfId() {
//        return seawulfId;
//    }
//
//    public void setSeawulfId(int seawulfId) {
//        this.seawulfId = seawulfId;
//    }

    @Enumerated(EnumType.STRING)
    @Column(name="state_name")
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumberOfDistrictings() {
        return numberOfDistrictings;
    }

    public void setNumberOfDistrictings(int numberOfDistrictings) {
        this.numberOfDistrictings = numberOfDistrictings;
    }

    public double getCompactnessGoal() {
        return compactnessGoal;
    }

    public void setCompactnessGoal(double compactnessGoal) {
        this.compactnessGoal = compactnessGoal;
    }

    public double getPopulationDifference() {
        return populationDifference;
    }

    public void setPopulationDifference(double populationDifference) {
        this.populationDifference = populationDifference;
    }

    @ElementCollection(targetClass = Minority.class)
    @CollectionTable(name = "job_minority",
            joinColumns = @JoinColumn(name = "job_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "minority_name")
    public Set<Minority> getMinorities() {
        return minorities;
    }

    public void setMinorities(Set<Minority> minorities) {
        this.minorities = minorities;
    }

//    @OneToOne(cascade = {CascadeType.ALL})
//    public Result getResult() {
//        return result;
//    }
//
//    public void setResult(Result result) {
//        this.result = result;
//    }

    @OneToMany(mappedBy = "job", cascade = {CascadeType.ALL})
    public List<Box> getSummary() {
        return summary;
    }

    public void setSummary(List<Box> summary) {
        this.summary = summary;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    public Districting getRandom() {
        return random;
    }

    public void setRandom(Districting random) {
        this.random = random;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    public Districting getAverage() {
        return average;
    }

    public void setAverage(Districting average) {
        this.average = average;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    public Districting getExtreme() {
        return extreme;
    }

    public void setExtreme(Districting extreme) {
        this.extreme = extreme;
    }

    public String getSummaryFilePath() {
        return summaryFilePath;
    }

    public void setSummaryFilePath(String summaryFilePath) {
        this.summaryFilePath = summaryFilePath;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", state=" + state +
                ", status=" + status +
                ", numberOfDistrictings=" + numberOfDistrictings +
                ", compactnessGoal=" + compactnessGoal +
                ", populationDifference=" + populationDifference +
                ", minorities=" + minorities +
                ", summary=" + summary +
                ", random=" + random +
                ", average=" + average +
                ", extreme=" + extreme +
                '}';
    }
}
