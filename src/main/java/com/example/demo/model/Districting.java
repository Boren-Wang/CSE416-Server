package com.example.demo.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Districting {
    private int districtingId;
    private Set<District> districts;

    @Id
    @GeneratedValue
    public int getDistrictingId() {
        return districtingId;
    }

    public void setDistrictingId(int districtingId) {
        this.districtingId = districtingId;
    }

    @OneToMany(mappedBy = "districting", cascade = {CascadeType.ALL})
    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }
}
