/* package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.service.MatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/find_similar_sightings")
    public List<Map<String, Object>> findSimilarSightings(@RequestBody Map<String, String> request) {
        return matchingService.findSimilarSightings(request);
    }
} */
