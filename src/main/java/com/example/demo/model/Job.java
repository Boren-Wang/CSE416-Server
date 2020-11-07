package com.example.demo.model;

import com.example.demo.enumerate.Minority;
import com.example.demo.enumerate.State;
import com.example.demo.enumerate.Status;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Job {
    @Id
//    @GeneratedValue(strategy= GenerationType.AUTO)
    private int jobId;
    private State state;
    private Status status;
    private int numberOfDistrictings;
    private double compactnessGoal;
    private int populationDifference;
//    private List<Minority> minorities;
//    private Summary summary;
//    private Districting random;
//    private Districting average;
//    private Districting extreme;

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
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

    public int getPopulationDifference() {
        return populationDifference;
    }

    public void setPopulationDifference(int populationDifference) {
        this.populationDifference = populationDifference;
    }

//    public List<Minority> getMinorities() {
//        return minorities;
//    }

//    public void setMinorities(List<Minority> minorities) {
//        this.minorities = minorities;
//    }

//    public Summary getSummary() {
//        return summary;
//    }
//
//    public void setSummary(Summary summary) {
//        this.summary = summary;
//    }

//    public Districting getRandom() {
//        return random;
//    }
//
//    public void setRandom(Districting random) {
//        this.random = random;
//    }
//
//    public Districting getAverage() {
//        return average;
//    }
//
//    public void setAverage(Districting average) {
//        this.average = average;
//    }
//
//    public Districting getExtreme() {
//        return extreme;
//    }
//
//    public void setExtreme(Districting extreme) {
//        this.extreme = extreme;
//    }
}
