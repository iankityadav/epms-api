package com.ankit.epms.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ankit.epms.model.PerformanceReview;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findTop3ByEmployeeIdOrderByReviewDateDesc(Long employeeId);
}
