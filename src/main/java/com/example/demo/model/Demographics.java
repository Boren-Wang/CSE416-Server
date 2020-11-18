package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Demographics {
    private int demographicsId;
    private int population;
    private int votingAgePopulation;
    private int minoritiesVap;
    private int asianVap;
    private int blackVap;
    private int whiteVap;
    private int nativeVap;
    private int hispanicVap;

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

    public int getNativeVap() {
        return nativeVap;
    }

    public void setNativeVap(int nativeVap) {
        this.nativeVap = nativeVap;
    }

    public int getHispanicVap() {
        return hispanicVap;
    }

    public void setHispanicVap(int hispanicVap) {
        this.hispanicVap = hispanicVap;
    }

    @Override
    public String toString() {
        return "Demographics{" +
                "demographicsId=" + demographicsId +
                ", population=" + population +
                ", votingAgePopulation=" + votingAgePopulation +
                ", minoritiesVap=" + minoritiesVap +
                ", asianVap=" + asianVap +
                ", blackVap=" + blackVap +
                ", whiteVap=" + whiteVap +
                ", nativeVap=" + nativeVap +
                ", hispanicVap=" + hispanicVap +
                '}';
    }
}
