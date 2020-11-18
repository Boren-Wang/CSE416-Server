package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "districting"})
public class District {
    private int districtId;
    private int numberOfCounties;
    private Set<Precinct> precincts;
    private Districting districting;
    private Demographics demographics;

    @Id
    @GeneratedValue
    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getNumberOfCounties() {
        return numberOfCounties;
    }

    public void setNumberOfCounties(int numberOfCounties) {
        this.numberOfCounties = numberOfCounties;
    }

    @OneToMany(mappedBy = "district", cascade = {CascadeType.ALL})
    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(Set<Precinct> precincts) {
        this.precincts = precincts;
    }

    @ManyToOne
    @JoinColumn(name = "districting_id")
    public Districting getDistricting() {
        return districting;
    }

    public void setDistricting(Districting districting) {
        this.districting = districting;
    }

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "demographics_id")
    public Demographics getDemographics() {
        return demographics;
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }

    @Override
    public String toString() {
        return "District{" +
                "districtId=" + districtId +
                ", numberOfCounties=" + numberOfCounties +
                ", precincts=" + precincts +
                ", districting=" + districting +
                ", demographics=" + demographics +
                '}';
    }
}
