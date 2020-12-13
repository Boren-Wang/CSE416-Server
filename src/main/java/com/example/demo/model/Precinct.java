package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "district"})
public class Precinct {
    private int precinctId;
    private String state;
    private int countyId;
    private String countyName;
    private Set<District> districts = new HashSet<>();
    private Demographics demographics;

    @Id
    public int getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(int precinctId) {
        this.precinctId = precinctId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

//    @ManyToMany
//    @JoinTable(
//            name = "Precinct_District",
//            joinColumns = { @JoinColumn(name = "precinct_id") },
//            inverseJoinColumns = { @JoinColumn(name = "district_id") }
//    )
//    public Set<District> getDistricts() {
//        return districts;
//    }
//
//    public void setDistricts(Set<District> districts) {
//        this.districts = districts;
//    }

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "demographics_id")
    public Demographics getDemographics() {
        return demographics;
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }
}
