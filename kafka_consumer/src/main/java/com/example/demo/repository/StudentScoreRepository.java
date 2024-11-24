package com.example.demo.repository;

import com.example.demo.entity.StudentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentScoreRepository extends JpaRepository<StudentScore, Long> {
    
    // Tìm StudentScore dựa trên studentId
    Optional<StudentScore> findByStudentId(String studentId);

    // Tìm tất cả StudentScore có điểm Toán lớn hơn giá trị nhất định
    List<StudentScore> findByToanGreaterThan(Float toanScore);

    // Tìm Top 10 StudentScore có điểm Hóa học cao nhất
    List<StudentScore> findTop10ByOrderByHoaDesc();

    // Kiểm tra sự tồn tại của StudentScore với studentId nhất định
    boolean existsByStudentId(String studentId);

    // Xóa StudentScore dựa trên studentId
    void deleteByStudentId(String studentId);

    // Tìm tất cả StudentScore có điểm Văn học giữa hai giá trị
    List<StudentScore> findByVanBetween(Float minScore, Float maxScore);

    // Tìm các StudentScore có điểm Sinh học trên hoặc bằng giá trị nhất định sử dụng @Query
    @Query("SELECT s FROM StudentScore s WHERE s.sinh >= :minScore")
    List<StudentScore> findStudentsWithSinhScoreAbove(@Param("minScore") Float minScore);
}