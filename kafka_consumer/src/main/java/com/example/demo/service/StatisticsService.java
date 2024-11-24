// FILE: StatisticsService.java
package com.example.demo.service;

import com.example.demo.repository.StudentScoreRepository;
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
        String[] subjects = {"Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD"};
        
        Map<String, Object> detailedStats = new HashMap<>();

        for (String subject : subjects) {
            Map<String, Double> stats = new HashMap<>();
            try {
                stats.put("average", getAverageScoreBySubject(subject));
                stats.put("median", getMedianScoreBySubject(subject));
                stats.put("max", getMaxScoreBySubject(subject));
                stats.put("min", getMinScoreBySubject(subject));
                stats.put("standardDeviation", getStandardDeviationBySubject(subject));
            } catch (IllegalArgumentException e) {
                // Xử lý môn học không hợp lệ, gán giá trị mặc định
                stats.put("average", 0.0);
                stats.put("median", 0.0);
                stats.put("max", 0.0);
                stats.put("min", 0.0);
                stats.put("standardDeviation", 0.0);
            }
            detailedStats.put(subject, stats);
        }

        return detailedStats;
    }

    public String generateChart() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> detailedStats = getDetailedStatistics();
    
        // Chuẩn bị dữ liệu để gửi tới dịch vụ Python
        Map<String, Double> requestBody = new HashMap<>();
    
        System.out.println(requestBody);
        for (Map.Entry<String, Object> entry : detailedStats.entrySet()) {
            String subject = entry.getKey();
            Map<String, Double> stats = (Map<String, Double>) entry.getValue();
            // Lấy giá trị bạn muốn gửi, ví dụ: giá trị trung bình
            Double average = stats.get("average");
            requestBody.put(subject, average);
        }
    
        // Gửi dữ liệu đến dịch vụ Python
        Map<String, Object> response = restTemplate.postForObject(PYTHON_SERVICE_URL, requestBody, Map.class);
    
        if (response != null && response.containsKey("chart_html")) {
            return (String) response.get("chart_html");
        } else {
            return "<p>Không thể tạo biểu đồ.</p>";
        }
    }
}