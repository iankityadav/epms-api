package com.ankit.epms.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ankit.epms.model.Employee;
import com.ankit.epms.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> getFilteredEmployees(
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reviewDate,
            @RequestParam(required = false) List<String> departments,
            @RequestParam(required = false) List<String> projects) {

        return ResponseEntity.ok(employeeService.getFilteredEmployees(score, reviewDate, departments, projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeDetails(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeDetails(id));
    }
}
