package com.example.demo.handler;

import com.example.demo.dataAccessObject.PrecinctRepo;
import com.example.demo.model.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlgorithmHandler {
    private int jobId;
    private Result result;

    @Autowired
    JobHandler jh;

    @Autowired
    PrecinctRepo precinctRepo;

    // 通知状态 -> 转送文件 -> 处理结果
    public void processResult() throws IOException {
        processResultJson();
        System.out.println(this.result);
        computeNumberOfCountiesForEachDistrict();
        System.out.println(this.result);
    }

    public void processResultJson() throws IOException {
        this.result = new Result();

        File file = new File("src/main/resources/results/result.json");
        String content = FileUtils.readFileToString(file);
        JSONArray plans = new JSONArray(content);

        // for each plan in result
        for(int i=0; i<plans.length(); i++) {
            Districting districting = new Districting();
            JSONArray districts = (JSONArray) plans.get(i);
            // for each district in each plan:
            for(int j=0; j<districts.length(); j++) {
                District district = new District();
                JSONArray precincts = (JSONArray) districts.get(i);
                // for each precinct in each district:
                for(int k=0; k<precincts.length(); k++) {
                    int precinctId = (int) precincts.get(k);
                    district.getPrecincts().add(precinctRepo.getOne(precinctId));
                }
                districting.getDistricts().add(district);
            }
            this.result.getDistrictings().add(districting);
        }
        System.out.println(this.result);
    }

    public void computeNumberOfCountiesForEachDistrict() {
        List<Districting> districtings = this.result.getDistrictings();

        for(Districting districting : districtings) {
            for(District district : districting.getDistricts()) {
                Set<Integer> countyIds = new HashSet<>();
                for (Precinct p : district.getPrecincts()) {
                    int countyId = p.getCountyId();
                    if (!countyIds.contains(countyId)) {
                        countyIds.add(countyId);
                    }
                }
                district.setNumberOfCounties(countyIds.size());
            }
        }
    }

    // need to be able to tell which job the result.json belongs to
    // --> need to separate jobId and seawulfId
    // --> set jobId manually before dispatching the job
    // --> result.json should be renamed to jobId.json
    public void computeDemographicsForEachDistrict() {

    }

    public String generateSummaryFile(Job job) {
        return "";
    }

    public void sortDistrictsForEachDistricting() {
        List<Districting> districtings = this.result.getDistrictings();
        for(Districting districting : districtings) {
            List<District> districts = districting.getDistricts();
            districts.sort((d1, d2) -> d1.getDemographics().getMinoritiesVap() - d2.getDemographics().getMinoritiesVap());
        }
    }

    public void generateSummary(List<Districting> districtings) {
        // generate summary
        List<Box> summary = null;

        // update job
        jh.setSummary(jobId, summary);
    }

    public void determineAverage(List<Districting> districtings) {
        // determine the average districting
        Districting average = null;

        // update average
        jh.setAverage(jobId, average);
    }
}
