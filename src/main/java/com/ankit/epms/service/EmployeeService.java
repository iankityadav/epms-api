package com.ankit.epms.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ankit.epms.exception.ResourceNotFoundException;
import com.ankit.epms.model.Employee;
import com.ankit.epms.model.PerformanceReview;
import com.ankit.epms.repo.EmployeeRepository;
import com.ankit.epms.repo.PerformanceReviewRepository;
import com.ankit.epms.specs.EmployeeSpecifications;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PerformanceReviewRepository reviewRepository;

    public List<Employee> getFilteredEmployees(Double score, LocalDate reviewDate, List<String> departments,
            List<String> projects) {
        Specification<Employee> spec = Specification.where(null);

        if (score != null && reviewDate != null) {
            spec = spec.and(EmployeeSpecifications.hasPerformanceScore(score, reviewDate));
        }
        if (departments != null && !departments.isEmpty()) {
            spec = spec.and(EmployeeSpecifications.inDepartments(departments));
        }
        if (projects != null && !projects.isEmpty()) {
            spec = spec.and(EmployeeSpecifications.inProjects(projects));
        }

        return employeeRepository.findAll(spec);
    }

    public Map<String, Object> getEmployeeDetails(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        List<PerformanceReview> last3Reviews = reviewRepository.findTop3ByEmployeeIdOrderByReviewDateDesc(id);

        Map<String, Object> details = new HashMap<>();
        details.put("employee", emp);
        details.put("department", emp.getDepartment());
        details.put("projects", emp.getProjects());
        details.put("last3Reviews", last3Reviews);

        return details;
    }
}
