package com.example.demo.controller;

import com.example.demo.dataAccessObject.PrecinctRepo;
import com.example.demo.enumerate.State;
import com.example.demo.model.Demographics;
import com.example.demo.model.Precinct;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class JsonController {
    @Autowired
    PrecinctRepo precinctRepo;

    @PostMapping("api/json/precincts/{stateName}")
    public void populatePrecincts(@PathVariable("stateName") String stateName) throws Exception{
        System.out.println("Populating precincts into database!");
        String state;
        if(stateName.equals("GA")) {
            state = State.GEORGIA.name();
        } else if(stateName.equals("LA")) {
            state = State.LOUISIANA.name();
        } else if(stateName.equals("MI")) {
            state = State.MISSISSIPPI.name();
        } else {
            return;
        }
        File file = new File("src/main/resources/algorithm/"+stateName+".json");
        String content = FileUtils.readFileToString(file);
        //对基本类型的解析
        JSONObject obj = new JSONObject(content);
        JSONArray precincts = obj.getJSONArray("features");
        System.out.println("Length: "+precincts.length());
        for(int i=0; i<precincts.length(); i++) {
            JSONObject properties = ((JSONObject) precincts.get(i)).getJSONObject("properties");
            int precinctId = properties.getInt("ID");
            if(precinctRepo.findById(precinctId).isPresent()) {
                System.out.println("Precinct "+precinctId+" is already in the database.");
                continue;
            }
            System.out.println("Populating precinct "+precinctId);
            String countyName = properties.getString("CTYNAME");
            int totalPopulation = properties.getInt("TOTPOP");
            int vap = properties.getInt("VAP");
            int hvap = properties.getInt("HVAP");
            int wvap = properties.getInt("WVAP");
            int bvap = properties.getInt("BVAP");
            int aminvap = properties.getInt("AMINVAP"); // American Indians and Alaska Natives
            int asianvap = properties.getInt("ASIANVAP");
            int nhpivap = properties.getInt("NHPIVAP"); // Native Hawaiians and other Pacific Islanders

            Demographics demographics = new Demographics();
            demographics.setPopulation(totalPopulation);
            demographics.setVotingAgePopulation(vap);
            demographics.setHispanicVap(hvap);
            demographics.setWhiteVap(wvap);
            demographics.setBlackVap(bvap);
            demographics.setAMINVap(aminvap);
            demographics.setAsianVap(asianvap);
            demographics.setNHPIVap(nhpivap);

            Precinct precinct = new Precinct();
            precinct.setPrecinctId(precinctId);
            precinct.setState(state);
            precinct.setCountyName(countyName);
            precinct.setDemographics(demographics);

            System.out.println("Persisting precinct "+precinctId);
            precinctRepo.save(precinct);
        }
    }
}
