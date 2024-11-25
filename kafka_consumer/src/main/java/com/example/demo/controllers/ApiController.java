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
    public Map<String, Object> getScoreStatisticsBySubject(@RequestParam("subject") String subject) {
        return statisticsService.getScoreStatisticsBySubject(subject);
    }

    /**
     * Endpoint để lấy biểu đồ phân phối điểm của một môn học dưới dạng HTML.
     *
     * @param subject Tên môn học.
     * @return HTML của biểu đồ phân phối điểm.
     */
    @GetMapping("/score-distribution-chart")
    public String getScoreDistributionChart(@RequestParam("subject") String subject) {
        return statisticsService.getScoreDistributionChart(subject);
    }
}