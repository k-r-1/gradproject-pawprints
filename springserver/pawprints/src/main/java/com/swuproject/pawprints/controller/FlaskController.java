package com.swuproject.pawprints.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.swuproject.pawprints.service.FlaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FlaskController {

    @Autowired
    private FlaskService flaskService;

    @PostMapping("/find_similar_sightings")
    public List<Map<String, Object>> findSimilarSightings(@RequestBody Map<String, String> requestBody) {
        return flaskService.findSimilarSightings(requestBody);
    }
}
