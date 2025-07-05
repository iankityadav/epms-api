package com.ankit.epms.specs;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.ankit.epms.model.Employee;
import com.ankit.epms.model.PerformanceReview;
import com.ankit.epms.model.Project;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class EmployeeSpecifications {
    private EmployeeSpecifications() {
    }

    public static Specification<Employee> hasPerformanceScore(Double score, LocalDate date) {
        return (root, query, cb) -> {
            Join<Employee, PerformanceReview> join = root.join("performanceReviews", JoinType.INNER);
            return cb.and(cb.equal(join.get("score"), score), cb.equal(join.get("reviewDate"), date));
        };
    }

    public static Specification<Employee> inDepartments(List<String> departmentNames) {
        return (root, query, cb) -> root.get("department").get("name").in(departmentNames);
    }

    public static Specification<Employee> inProjects(List<String> projectNames) {
        return (root, query, cb) -> {
            Join<Employee, Project> join = root.join("projects", JoinType.INNER);
            return join.get("name").in(projectNames);
        };
    }
}