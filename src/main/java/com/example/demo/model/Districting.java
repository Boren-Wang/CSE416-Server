package com.example.demo.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Districting {
    private int districtingId;
//    private Result result;
    private List<District> districts = new ArrayList<>();
    private String geojsonFilePath;

    @Id
    @GeneratedValue
    public int getDistrictingId() {
        return districtingId;
    }

    public void setDistrictingId(int districtingId) {
        this.districtingId = districtingId;
    }

//    @ManyToOne
//    @JoinColumn(name="result_id")
//    public Result getResult() {
//        return result;
//    }
//
//    public void setResult(Result result) {
//        this.result = result;
//    }

    @OneToMany(mappedBy = "districting", cascade = {CascadeType.ALL})
    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    public String getGeojsonFilePath() {
        return geojsonFilePath;
    }

    public void setGeojsonFilePath(String geojsonFilePath) {
        this.geojsonFilePath = geojsonFilePath;
    }

    @Override
    public String toString() {
        return "Districting{" +
                "districtingId=" + districtingId +
                ", districts=" + districts +
                ", geojsonFilePath='" + geojsonFilePath + '\'' +
                '}';
    }
}
