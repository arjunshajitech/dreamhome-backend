package com.dreamhome.repository;

import com.dreamhome.table.Project;
import com.dreamhome.table.enumeration.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    boolean existsByName(String name);

    boolean existsByNameAndClientId(String name, UUID id);

    Project findByIdAndClientId(UUID projectId, UUID id);

    List<Project> findAllByClientId(UUID id);

    Project findOneById(UUID projectId);

    List<Project> findAllByEngineerId(UUID id);

    Project findByIdAndEngineerId(UUID id, UUID id1);

    List<Project> findAllByStatus(ProjectStatus projectStatus);
}
