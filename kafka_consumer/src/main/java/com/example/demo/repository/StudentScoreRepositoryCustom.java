// FILE: StudentScoreRepositoryCustom.java
package com.example.demo.repository;

public interface StudentScoreRepositoryCustom {
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
}