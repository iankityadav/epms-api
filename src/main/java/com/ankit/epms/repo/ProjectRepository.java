package com.ankit.epms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ankit.epms.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
