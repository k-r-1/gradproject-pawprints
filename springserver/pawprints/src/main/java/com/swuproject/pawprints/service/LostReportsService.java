package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.LostReports;
import com.swuproject.pawprints.repository.LostReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class LostReportsService {

    @Autowired
    private LostReportsRepository lostReportsRepository;

    public List<LostReports> getRandomLostReports() {
        List<LostReports> allReports = lostReportsRepository.findAll();
        Random random = new Random();
        return random.ints(0, allReports.size())
                .distinct()
                .limit(5)
                .mapToObj(allReports::get)
                .collect(Collectors.toList());
    }
}