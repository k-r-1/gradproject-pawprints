package com.swuproject.pawprints.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MatchingService {

    private final RestTemplate restTemplate;

    public MatchingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> findSimilarSightings(Map<String, String> request) {
        String flaskUrl = "https://pawprintssightreport-3v3tlfcd4q-uc.a.run.app/find_similar_sightings";
        return restTemplate.postForObject(flaskUrl, request, List.class);
    }
}
