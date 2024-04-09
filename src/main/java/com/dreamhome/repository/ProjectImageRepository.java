package com.dreamhome.repository;

import com.dreamhome.table.ProjectImage;
import com.dreamhome.table.enumeration.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {
    List<ProjectImage> findAllByProjectIdAndClientIdAndType(UUID projectId, UUID id, ImageType imageType);

    ProjectImage findByIdAndClientId(UUID id, UUID id1);

    ProjectImage findByIdAndClientIdAndType(UUID id, UUID id1, ImageType imageType);

    List<ProjectImage> findAllByProjectIdAndEngineerIdAndType(UUID projectId, UUID id, ImageType imageType);
}
