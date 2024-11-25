package com.example.demo.controllers;

import com.example.demo.service.StatisticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RestController
public class ApiController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/detailed-scores")
    public Map<String, Object> getDetailedStatistics() {
        return statisticsService.getDetailedStatistics();
    }

    @GetMapping("/average-scores-chart")
    public String getDetailedStatisticsChart() {
        return statisticsService.generateChart();
    }

    @GetMapping("/distribution-by-subject")
    public Map<String, Object> getScoreDistributionBySubject(@RequestParam("subject") String subject) {
        return statisticsService.getScoreDistributionBySubject(subject);
    }

}