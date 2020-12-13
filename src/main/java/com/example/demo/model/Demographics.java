package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "demographicsId"})
public class Demographics {
    private int demographicsId;
    private int population;
    private int votingAgePopulation;
    private int minoritiesVap;
    private double minoritiesVapPercentage;
    private int asianVap;
    private int blackVap;
    private int whiteVap;
    private int hispanicVap;
    private int AMINVap; // American Indians and Alaska Natives
    private int NHPIVap; // Native Hawaiians and other Pacific Islanders

    @Id
    @GeneratedValue
    public int getDemographicsId() {
        return demographicsId;
    }

    public void setDemographicsId(int demographicsId) {
        this.demographicsId = demographicsId;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int pupulation) {
        this.population = pupulation;
    }

    public int getVotingAgePopulation() {
        return votingAgePopulation;
    }

    public void setVotingAgePopulation(int votingAgePopulation) {
        this.votingAgePopulation = votingAgePopulation;
    }

    public int getMinoritiesVap() {
        return minoritiesVap;
    }

    public void setMinoritiesVap(int minoritiesVap) {
        this.minoritiesVap = minoritiesVap;
    }

    public double getMinoritiesVapPercentage() {
        return minoritiesVapPercentage;
    }

    public void setMinoritiesVapPercentage(double minoritiesVapPercentage) {
        this.minoritiesVapPercentage = minoritiesVapPercentage;
    }

    public int getAsianVap() {
        return asianVap;
    }

    public void setAsianVap(int asianVap) {
        this.asianVap = asianVap;
    }

    public int getBlackVap() {
        return blackVap;
    }

    public void setBlackVap(int blackVap) {
        this.blackVap = blackVap;
    }

    public int getWhiteVap() {
        return whiteVap;
    }

    public void setWhiteVap(int whiteVap) {
        this.whiteVap = whiteVap;
    }

    public int getHispanicVap() {
        return hispanicVap;
    }

    public void setHispanicVap(int hispanicVap) {
        this.hispanicVap = hispanicVap;
    }

    public int getAMINVap() {
        return AMINVap;
    }

    public void setAMINVap(int AMINVap) {
        this.AMINVap = AMINVap;
    }

    public int getNHPIVap() {
        return NHPIVap;
    }

    public void setNHPIVap(int NHPIVap) {
        this.NHPIVap = NHPIVap;
    }

    @Override
    public String toString() {
        return "Demographics{" +
                "demographicsId=" + demographicsId +
                ", population=" + population +
                ", votingAgePopulation=" + votingAgePopulation +
                ", minoritiesVap=" + minoritiesVap +
                ", minoritiesVapPercentage=" + minoritiesVapPercentage +
                ", asianVap=" + asianVap +
                ", blackVap=" + blackVap +
                ", whiteVap=" + whiteVap +
                ", hispanicVap=" + hispanicVap +
                ", AMINVap=" + AMINVap +
                ", NHPIVap=" + NHPIVap +
                '}';
    }
}
