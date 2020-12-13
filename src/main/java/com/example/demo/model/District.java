package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@JsonIgnoreProperties({"districting", "precincts"})
public class District {
    private int districtId;
    private int numberOfCounties;
    private Set<Precinct> precincts = new HashSet<>();
    private Set<Integer> precinctIds = new HashSet<>();
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

//    @ManyToMany(mappedBy = "districts")
    @Transient
    public Set<Precinct> getPrecincts() {
        return precincts;
    }

    public void setPrecincts(Set<Precinct> precincts) {
        this.precincts = precincts;
    }

    @ElementCollection
    @CollectionTable(name="PrecinctIds", joinColumns=@JoinColumn(name="district_id"))
    @Column(name="precinct_id")
    public Set<Integer> getPrecinctIds() {
        return precinctIds;
    }

    public void setPrecinctIds(Set<Integer> precinctIds) {
        this.precinctIds = precinctIds;
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
}
