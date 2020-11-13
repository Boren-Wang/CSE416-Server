package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "district"})
public class Precinct {
    private int precinctId;
    private int countyId;
    private District district;
    private Demographics demographics;

    @Id
    @GeneratedValue
    public int getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(int precinctId) {
        this.precinctId = precinctId;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    @ManyToOne
    @JoinColumn(name = "district_id")
    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
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
