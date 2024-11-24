// FILE: StudentScoreRepositoryImpl.java
package com.example.demo.repository;

import com.example.demo.enums.Subject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class StudentScoreRepositoryImpl implements StudentScoreRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Thực hiện truy vấn thống kê cho một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @param function Hàm thống kê (AVG, MIN, MAX, PERCENTILE_CONT).
     * @return Kết quả của hàm thống kê.
     */
    private Double executeStatisticQuery(Subject subject, String function) {
        String column = subject.getColumnName();

        String sql;
        if ("PERCENTILE_CONT".equals(function)) {
            // Giả sử bạn đang sử dụng PostgreSQL
            sql = "SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY " + column + ") FROM student_scores";
        } else {
            sql = "SELECT " + function + "(" + column + ") FROM student_scores";
        }

        Query query = entityManager.createNativeQuery(sql);
        Object result = query.getSingleResult();

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
}