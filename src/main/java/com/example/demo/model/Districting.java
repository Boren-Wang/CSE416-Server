package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Districting {
    @Id
    private int districtingId;

    public int getDistrictingId() {
        return districtingId;
    }

    public void setDistrictingId(int districtingId) {
        this.districtingId = districtingId;
    }
}
