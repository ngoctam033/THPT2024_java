package com.example.demo.repository;

import com.example.demo.entity.StudentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentScoreRepository extends JpaRepository<StudentScore, Long> {
}