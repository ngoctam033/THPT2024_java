// FILE: StudentScoreRepositoryCustom.java
package com.example.demo.repository;

import java.util.Map;
import java.util.HashMap;

public interface StudentScoreRepositoryCustom {

    // tạo phương thức để lấy tên môn học 
    String getSubjectName(String subject);

    /**
     * Tính trung vị (median) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị trung vị của môn học được chỉ định.
     */
    Double findMedianBySubject(String subject);
    
    /**
     * Tính trung bình (average) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị trung bình của môn học được chỉ định.
     */
    Double findAverageBySubject(String subject);
    /**
     * Tính điểm tối đa (max) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị điểm tối đa của môn học được chỉ định.
     */
    Double findMaxBySubject(String subject);
    
    /**
     * Tính điểm tối thiểu (min) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị điểm tối đa của môn học được chỉ định.
     */
    Double findMinBySubject(String subject);

    /**
     * Tính độ lệch chuẩn (standard deviation) của một môn học cụ thể.
     *
     * @param subject Tên môn học.
     * @return Giá trị độ lệch chuẩn của môn học được chỉ định.
     */
    Double findStandardDeviationBySubject(String subject);

    /**
     * Đếm tổng số sinh viên có điểm của một môn học cụ thể (không bao gồm null).
     *
     * @param subject Tên môn học.
     * @return Tổng số sinh viên có điểm trong môn học được chỉ định.
     */
    Integer countStudentsWithScoreBySubject(String subject);

    // thêm phương thức countStudentsWithScoreBelowOneBySubject để đếm số lượng học sinh có điểm dưới 1 theo môn
    Integer countStudentsWithScoreBelowXBySubject(String subject, Double score);

    // định nghĩa một hàm để tính tổng số sinh viên đạt khoảng điểm từ minScore đến maxScore với độ chia là o.25
    Map<String, Integer> getScoreDistributionBySubjectWithStep(String subject, Double step);
}