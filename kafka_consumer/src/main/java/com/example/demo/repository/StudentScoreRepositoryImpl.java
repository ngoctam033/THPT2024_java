// FILE: StudentScoreRepositoryImpl.java
package com.example.demo.repository;

import com.example.demo.enums.Subject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class StudentScoreRepositoryImpl implements StudentScoreRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    // thêm phương thức getSubjectName để lấy tên môn học
    @Override
    public String getSubjectName(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return enumSubject.getDisplayName();
    }

    /**
     * Thực hiện truy vấn thống kê cho một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @param function Hàm thống kê (AVG, MIN, MAX, PERCENTILE_CONT, STDDEV, COUNT).
     * @return Kết quả của hàm thống kê.
     */
    private Double executeStatisticQuery(Subject subject, String function) {
        String column = subject.getColumnName();

        String sql;
        if ("PERCENTILE_CONT".equals(function)) {
            // sử dụng PostgreSQL
            sql = "SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY " + column + ") FROM student_scores";
        } else if ("COUNT".equals(function)) {
            sql = "SELECT COUNT(" + column + ") FROM student_scores WHERE " + column + " IS NOT NULL";
        } else {
            sql = "SELECT " + function + "(" + column + ") FROM student_scores";
        }

        Query query = entityManager.createNativeQuery(sql);
        Object result = query.getSingleResult();

        if ("COUNT".equals(function)) {
            return result != null ? ((Number) result).doubleValue() : 0.0;
        }

        return result != null ? ((Number) result).doubleValue() : 0.0;
    }

    /**
     * Tính trung vị (median) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị trung vị của môn học được chỉ định.
     */
    @Override
    public Double findMedianBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return executeStatisticQuery(enumSubject, "PERCENTILE_CONT");
    }

    /**
     * Tính trung bình (average) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị trung bình của môn học được chỉ định.
     */
    @Override
    public Double findAverageBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return executeStatisticQuery(enumSubject, "AVG");
    }

    /**
     * Tính điểm tối đa (max) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị điểm tối đa của môn học được chỉ định.
     */
    @Override
    public Double findMaxBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return executeStatisticQuery(enumSubject, "MAX");
    }

    /**
     * Tính điểm tối thiểu (min) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị điểm tối thiểu của môn học được chỉ định.
     */
    @Override
    public Double findMinBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return executeStatisticQuery(enumSubject, "MIN");
    }

    /**
     * Tính độ lệch chuẩn (standard deviation) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị độ lệch chuẩn của môn học được chỉ định.
     */
    @Override
    public Double findStandardDeviationBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        return executeStatisticQuery(enumSubject, "STDDEV");
    }

    /**
     * Đếm tổng số sinh viên có điểm của một môn học cụ thể (không bao gồm null).
     *
     * @param subject Tên môn học.
     * @return Tổng số sinh viên có điểm trong môn học được chỉ định.
     */
    @Override
    public Integer countStudentsWithScoreBySubject(String subject) {
        Subject enumSubject = Subject.fromString(subject);
        Double count = executeStatisticQuery(enumSubject, "COUNT");
        return (count != null) ? count.intValue() : 0;
    }

    // thêm phương thức countStudentsWithScoreBelowOneBySubject để đếm số lượng học sinh có điểm dưới 1 theo môn
    @Override
    public Integer countStudentsWithScoreBelowXBySubject(String subject, Double score) {
        Subject enumSubject = Subject.fromString(subject);
        String column = enumSubject.getColumnName();

        String sql = "SELECT COUNT(" + column + ") FROM student_scores WHERE " + column + " < " + score;
        Query query = entityManager.createNativeQuery(sql);
        Object result = query.getSingleResult();

        return result != null ? ((Number) result).intValue() : 0;
    }

    /**
     * Lấy phổ điểm của một môn học cụ thể với bước chia là step.
     *
     * @param subject Tên môn học.
     * @param step    Độ chia của khoảng điểm (ví dụ: 0.25).
     * @return Phổ điểm của môn học được chỉ định dưới dạng Map.
     */
    @Override
    public Map<String, Integer> getScoreDistributionBySubjectWithStep(String subject, Double step) {
        Subject enumSubject = Subject.fromString(subject);
        String column = enumSubject.getColumnName();

        // Giả sử điểm số nằm trong khoảng từ 0 đến 10
        Double minScore = 0.0;
        Double maxScore = 10.0;

        Map<String, Integer> distribution = new HashMap<>();

        // Tính toán số lượng khoảng chia
        int numberOfBins = (int) Math.ceil((maxScore - minScore) / step);

        for (int i = 0; i < numberOfBins; i++) {
            Double rangeStart = minScore + i * step;
            Double rangeEnd = rangeStart + step;

            // Định dạng key với 2 chữ số thập phân
            String key = String.format("%.2f", rangeStart);

            String sql = "SELECT COUNT(" + column + ") FROM student_scores WHERE " + column +
                         " >= " + rangeStart + " AND " + column + " < " + rangeEnd;

            Query query = entityManager.createNativeQuery(sql);
            Object result = query.getSingleResult();

            Integer count = (result != null) ? ((Number) result).intValue() : 0;
            distribution.put(key, count);
        }

        return distribution;
    }
}