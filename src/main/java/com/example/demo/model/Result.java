package com.example.demo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Result {
    private int resultId;
    private String state;
    private List<Districting> districtings = new ArrayList<>();

    @Id
    @GeneratedValue
    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @OneToMany(mappedBy = "result", cascade = {CascadeType.ALL})
    public List<Districting> getDistrictings() {
        return districtings;
    }

    public void setDistrictings(List<Districting> districtings) {
        this.districtings = districtings;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultId=" + resultId +
                ", state='" + state + '\'' +
                ", districtings=" + districtings +
                '}';
    }
}
