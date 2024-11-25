// FILE: StatisticsService.java
package com.example.demo.service;

import com.example.demo.repository.StudentScoreRepository;
import com.example.demo.enums.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private StudentScoreRepository studentScoreRepository;

    private final String PYTHON_SERVICE_URL = "http://visualization_service:5000/create-chart";
    
    // Phương thức tính điểm trung bình
    public Double getAverageScoreBySubject(String subject) {
        return studentScoreRepository.findAverageBySubject(subject);
    }

    // Phương thức tính trung vị điểm
    public Double getMedianScoreBySubject(String subject) {
        return studentScoreRepository.findMedianBySubject(subject);
    }

    // Phương thức tính điểm tối đa
    public Double getMaxScoreBySubject(String subject) {
        return studentScoreRepository.findMaxBySubject(subject);
    }

    // Phương thức tính điểm tối thiểu
    public Double getMinScoreBySubject(String subject) {
        return studentScoreRepository.findMinBySubject(subject);
    }

    // Phương thức tính độ lệch chuẩn
    public Double getStandardDeviationBySubject(String subject) {
        return studentScoreRepository.findStandardDeviationBySubject(subject);
    }

    public Map<String, Object> getDetailedStatistics() {
        Subject[] subjects = Subject.values();
        
        Map<String, Object> detailedStats = new HashMap<>();

        for (Subject subject : subjects) {
            Map<String, Double> stats = new HashMap<>();
            try {
                String subjectName = subject.getColumnName();
                stats.put("average", getAverageScoreBySubject(subjectName));
                stats.put("median", getMedianScoreBySubject(subjectName));
                stats.put("max", getMaxScoreBySubject(subjectName));
                stats.put("min", getMinScoreBySubject(subjectName));
                stats.put("standardDeviation", getStandardDeviationBySubject(subjectName));
            } catch (IllegalArgumentException e) {
                // Xử lý môn học không hợp lệ, gán giá trị mặc định
                stats.put("average", 0.0);
                stats.put("median", 0.0);
                stats.put("max", 0.0);
                stats.put("min", 0.0);
                stats.put("standardDeviation", 0.0);
            }
            detailedStats.put(subject.getDisplayName(), stats);
        }

        return detailedStats;
    }

    /**
     * Thu thập dữ liệu thống kê chi tiết và trả về dưới dạng Map.
     *
     * @return Map chứa dữ liệu thống kê cho từng môn học.
     */
    public Map<String, Object> getStatisticsData() {
        Subject[] subjects = Subject.values();

        Map<String, Object> detailedStats = new HashMap<>();

        for (Subject subject : subjects) {
            Map<String, Double> stats = new HashMap<>();
            try {
                String subjectName = subject.getColumnName();
                stats.put("average", getAverageScoreBySubject(subjectName));
                stats.put("median", getMedianScoreBySubject(subjectName));
                stats.put("max", getMaxScoreBySubject(subjectName));
                stats.put("min", getMinScoreBySubject(subjectName));
                stats.put("standardDeviation", getStandardDeviationBySubject(subjectName));
            } catch (IllegalArgumentException e) {
                // Xử lý môn học không hợp lệ, gán giá trị mặc định
                stats.put("average", 0.0);
                stats.put("median", 0.0);
                stats.put("max", 0.0);
                stats.put("min", 0.0);
                stats.put("standardDeviation", 0.0);
            }
            detailedStats.put(subject.getDisplayName(), stats);
        }

        // In dữ liệu để kiểm tra
        System.out.println("Dữ liệu thống kê thu được: " + detailedStats);

        return detailedStats;
    }

    /**
     * Tạo biểu đồ bằng cách gửi dữ liệu thống kê đến dịch vụ Python.
     *
     * @return HTML của biểu đồ hoặc thông báo lỗi.
     */
    public String generateChart() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = getStatisticsData();

        // In requestBody ra để kiểm tra
        System.out.println("Dữ liệu gửi đến dịch vụ Python: " + requestBody);

        // Gửi dữ liệu đến dịch vụ Python
        Map<String, Object> response = restTemplate.postForObject(PYTHON_SERVICE_URL, requestBody, Map.class);

        if (response != null && response.containsKey("chart_html")) {
            return (String) response.get("chart_html");
        } else {
            return "<p>Không thể tạo biểu đồ.</p>";
        }
    }
}