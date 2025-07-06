package com.ankit.epms;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ankit.epms.model.Department;
import com.ankit.epms.model.Employee;
import com.ankit.epms.model.PerformanceReview;
import com.ankit.epms.model.Project;
import com.ankit.epms.repo.DepartmentRepository;
import com.ankit.epms.repo.EmployeeRepository;
import com.ankit.epms.repo.PerformanceReviewRepository;
import com.ankit.epms.repo.ProjectRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PerformanceReviewRepository reviewRepository;

    @BeforeEach
    void setupData() {
        reviewRepository.deleteAll();
        employeeRepository.deleteAll();
        projectRepository.deleteAll();
        departmentRepository.deleteAll();

        Department dept = new Department();
        dept.setName("HR");
        dept.setBudget(100000.0);
        departmentRepository.save(dept);

        Project project = new Project();
        project.setName("CRM");
        project.setDepartment(dept);
        project.setStartDate(LocalDate.of(2024, 1, 1));
        project.setEndDate(LocalDate.of(2024, 12, 31));
        projectRepository.save(project);

        Employee emp = new Employee();
        emp.setName("John Doe");
        emp.setEmail("john@example.com");
        emp.setDateOfJoining(LocalDate.of(2023, 1, 10));
        emp.setSalary(70000.0);
        emp.setDepartment(dept);
        emp.setProjects(List.of(project));
        employeeRepository.save(emp);

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(emp);
        review.setScore(4.0);
        review.setReviewDate(LocalDate.of(2025, 1, 1));
        review.setReviewComments("Excellent work");
        reviewRepository.save(review);
    }

    // 1. Get list of employees with performance score + review date + department
    // contains + project contains
    @Test
    @Order(1)
    void testGetEmployees_FilterByScoreDateDepartmentsProjects() throws Exception {
        mockMvc.perform(get("/api/employees")
                .param("score", "4.0")
                .param("reviewDate", "2025-01-01")
                .param("departments", "HR", "Engineering")
                .param("projects", "CRM", "Website Redesign")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    // 2. Get list of employees filtered by department contains
    @Test
    @Order(2)
    void testGetEmployees_FilterByDepartments() throws Exception {
        mockMvc.perform(get("/api/employees")
                .param("departments", "Finance", "Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    // 3. Get list of employees filtered by projects contains
    @Test
    @Order(3)
    void testGetEmployees_FilterByProjects() throws Exception {
        mockMvc.perform(get("/api/employees")
                .param("projects", "Intranet Revamp", "HRMS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    // 4. Fetch employee detail by ID - should include department, projects and last
    // 3 reviews
    @Test
    @Order(4)
    void testGetEmployeeDetailsById() throws Exception {
        mockMvc.perform(get("/api/employees/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee.id").value(4))
                .andExpect(jsonPath("$.department.name").exists())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.last3Reviews").isArray())
                .andExpect(jsonPath("$.last3Reviews", hasSize(lessThanOrEqualTo(3))));
    }

    // 5. Fetch detail of non-existent employee
    @Test
    @Order(5)
    void testGetEmployeeDetailsById_NotFound() throws Exception {
        mockMvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    // 6. Get list with no filters - should return all
    @Test
    @Order(6)
    void testGetEmployees_NoFilters() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))));
    }

    // 7. Bad request on invalid filter format
    @Test
    @Order(7)
    void testGetEmployees_InvalidDateOrScore() throws Exception {
        mockMvc.perform(get("/api/employees")
                .param("score", "NaN")
                .param("reviewDate", "invalid-date"))
                .andExpect(status().isBadRequest());
    }
}
