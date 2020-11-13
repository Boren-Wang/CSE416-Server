package com.example.demo.model;

import com.example.demo.enumerate.Minority;
import com.example.demo.enumerate.State;
import com.example.demo.enumerate.Status;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Job {
    private int jobId;
    private State state;
    private Status status;
    private int numberOfDistrictings;
    private double compactnessGoal;
    private int populationDifference;
    private Set<Minority> minorities;
    private List<Box> summary;
    private Districting random;
    private Districting average;
    private Districting extreme;

    @Id
    @GeneratedValue
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @Enumerated(EnumType.STRING)
    @Column(name="state_name")
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
}
