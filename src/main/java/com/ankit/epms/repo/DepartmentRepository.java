package com.ankit.epms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ankit.epms.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
