package com.example.demo.handler;

import com.example.demo.dataAccessObject.DistrictingRepo;
import com.example.demo.model.Districting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class MapHandler {
    @Autowired
    DistrictingRepo districtingRepo;

    public String getStateGeojson(String stateName) throws Exception{
        String fileName = stateName+".json";
        String filePath = "src/main/resources/static/"+fileName;
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        return json;
    }

    public String getStateCurrentDistrictingGeojson(String stateName) throws Exception{
        String fileName = stateName+"_current_districting.json";
        String filePath = "src/main/resources/static/"+fileName;
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        return json;
    }

    public String getDistrictingGeojson(int districtingId) throws Exception{
        Districting districting = districtingRepo.getOne(districtingId);
        String geojsonFilePath = districting.getGeojsonFilePath();
        String json = new String(Files.readAllBytes(Paths.get(geojsonFilePath)));
        return json;
    }
}
