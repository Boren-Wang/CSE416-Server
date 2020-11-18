package com.example.demo.handler;

import com.example.demo.model.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlgorithmHandler {
    private int jobId;
    private JobHandler jh = new JobHandler();

    public int computeNumberOfCounties(District district) {
        Set<Integer> countyIds = new HashSet<>();

        for(Precinct p : district.getPrecincts()) {
            int countyId = p.getCountyId();
            if(!countyIds.contains(countyId)) {
                countyIds.add(countyId);
            }
        }

        return countyIds.size();
    }

    public String generateSummaryFile(Job job) {
        return "";
    }

    public void sortDistrictsForEachDistricting(List<Districting> districtings) {
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
