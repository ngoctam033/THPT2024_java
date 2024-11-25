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

    private final String CREATE_CHART_SERVICE_URL = "http://visualization_service:5000/create-chart";
    private final String CREATE_DISTRIBUTION_CHART_SERVICE_URL = "http://visualization_service:5000/create-distribution-chart";
    // phương thức lấy tên môn học
    public String getSubjectName(String subject) {
        return studentScoreRepository.getSubjectName(subject);
    }

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

    // Phương thức tính tổng số sinh viên có điểm theo môn
    public Integer countStudentsWithScoreBySubject(String subject) {
        Integer count = studentScoreRepository.countStudentsWithScoreBySubject(subject);
        return (count != null) ? count.intValue() : 0;
    }

    // phương thức tính tổng số sinh viên có điểm dưới n theo môn
    public Integer countStudentsWithScoreBelowXBySubject(String subject, Double score) {
        Integer count = studentScoreRepository.countStudentsWithScoreBelowXBySubject(subject, score);
        return (count != null) ? count.intValue() : 0;
    }

    // phương thức tính tổng số sinh viên có điểm trong khoảng từ minScore đến maxScore với độ chia là step
    public Map<String, Integer> getScoreDistributionBySubjectWithStep(String subject, Double step){
        return studentScoreRepository.getScoreDistributionBySubjectWithStep(subject, step);
    }

    /**
     * Lấy phổ điểm của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Phổ điểm của môn học được chỉ định.
     */
    public Map<String, Object> getScoreStatisticsBySubject(String subject) {
        Map<String, Object> scoreDistributionBySubject = new HashMap<>();

        // lấy tên môn học
        String subjectName = getSubjectName(subject);
        // Lấy tổng số học sinh của môn học
        Integer totalStudents = countStudentsWithScoreBySubject(subject);
        // Lấy điểm trung bình của môn học
        Double averageScore = getAverageScoreBySubject(subject);
        // Lấy trung vị của môn học
        Double medianScore = getMedianScoreBySubject(subject);
        // Lấy tổng số sinh viên có điểm dưới 1
        Integer studentsBelowOne = countStudentsWithScoreBelowXBySubject(subject, 1.0);
        // Lấy tổng số sinh viên có điểm dưới 5
        Integer studentsBelowFive = countStudentsWithScoreBelowXBySubject(subject, 5.0);
        // Lấy phân phối điểm của môn học với step là 0.25
        Map<String, Integer> studentsInRange = getScoreDistributionBySubjectWithStep(subject, 0.25);

        // Bổ sung thông tin vào phổ điểm
        scoreDistributionBySubject.put("Môn học", subjectName);
        scoreDistributionBySubject.put("Tổng số học sinh", totalStudents);
        scoreDistributionBySubject.put("Điểm trung bình", averageScore);
        scoreDistributionBySubject.put("Trung vị", medianScore);
        scoreDistributionBySubject.put("Số học sinh dưới 1", studentsBelowOne);
        scoreDistributionBySubject.put("Số học sinh dưới 5", studentsBelowFive);
        scoreDistributionBySubject.put("Phân phối điểm", studentsInRange);

        return scoreDistributionBySubject;
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

    public String generateChart() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> detailedStats = getDetailedStatistics();
    
        // Chuẩn bị dữ liệu để gửi tới dịch vụ Python
        Map<String, Object> requestBody = new HashMap<>();
        
        for (Map.Entry<String, Object> entry : detailedStats.entrySet()) {
            String subject = entry.getKey();
            Map<String, Double> stats = (Map<String, Double>) entry.getValue();
            // Thêm tất cả các giá trị thống kê vào requestBody
            requestBody.put(subject, stats);
        }
    
        // Gửi dữ liệu đến dịch vụ Python
        Map<String, Object> response = restTemplate.postForObject(CREATE_CHART_SERVICE_URL, requestBody, Map.class);
    
        if (response != null && response.containsKey("chart_html")) {
            return (String) response.get("chart_html");
        } else {
            return "<p>Không thể tạo biểu đồ.</p>";
        }
    }

    /**
     * Gửi dữ liệu phổ điểm của một môn học đến dịch vụ Visualization và nhận lại biểu đồ.
     *
     * @param subject Tên môn học.
     * @return HTML của biểu đồ được tạo ra.
     */
    public String getScoreDistributionChart(String subject) {
        // Tạo đối tượng RestTemplate để thực hiện HTTP request
        RestTemplate restTemplate = new RestTemplate();        

        // Lấy dữ liệu phổ điểm với bước chia là 0.25
        Map<String, Object> scoreDistribution = getScoreStatisticsBySubject(subject);


        Map<String, Object> requestBody = scoreDistribution;


        try {
            Map<String, Object> response = restTemplate.postForObject(CREATE_DISTRIBUTION_CHART_SERVICE_URL, requestBody, Map.class);
            // Kiểm tra phản hồi từ dịch vụ
            if (response != null && response.containsKey("chart_html")) {
                return (String) response.get("chart_html");
            } else {
                return "<p>Không thể tạo biểu đồ.</p>";
            }
        } catch (Exception e) {
            // Xử lý ngoại lệ khi thực hiện HTTP request
            e.printStackTrace();
            return "<p>Đã xảy ra lỗi khi tạo biểu đồ.</p>";
        }
    }
}