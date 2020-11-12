package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class MapController {

    @GetMapping("api/state/{stateName}")
    public String getStateGeoJSON(@PathVariable("stateName") String stateName) throws Exception{
        String fileName = stateName+".json";
        String filePath = "src/main/resources/static/"+fileName;
        String json = new String(Files.readAllBytes(Paths.get(filePath)));
        return json;
    }
}
