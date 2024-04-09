package com.dreamhome.repository;

import com.dreamhome.table.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {
}
