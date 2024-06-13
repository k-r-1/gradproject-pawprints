package com.swuproject.pawprints.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class FlaskService {

    @Value("${flask.server.url}")
    private String flaskServerUrl;

    public List<Map<String, Object>> findSimilarSightings(Map<String, String> requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        String url = flaskServerUrl + "/find_similar_sightings";
        List<Map<String, Object>> response = restTemplate.postForObject(url, requestBody, List.class);
        return response;
    }
}
