package com.example.demo.controller;

import com.example.demo.handler.MapHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {
    @Autowired
    MapHandler mh;

    @GetMapping("api/state/{stateName}")
    public String getStateGeojson(@PathVariable("stateName") String stateName) throws Exception{
        return mh.getStateGeojson(stateName);
    }

    @GetMapping("api/state/{stateName}/districting")
    public String getStateCurrentDistrictingGeojson(@PathVariable("stateName") String stateName) throws Exception{
        return mh.getStateCurrentDistrictingGeojson(stateName);
    }

    @GetMapping("api/districting/{districtingId}")
    public String getDistrictingGeojson(@PathVariable("districtingId") int districtingId) throws Exception{
        return mh.getDistrictingGeojson(districtingId);
    }
}
