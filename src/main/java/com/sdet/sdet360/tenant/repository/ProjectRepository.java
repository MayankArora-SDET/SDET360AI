package com.sdet.sdet360.tenant.repository;

import com.sdet.sdet360.tenant.model.Project;
import com.sdet.sdet360.tenant.model.Vertical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByDeletedAtIsNull();

    List<Project> findByVertical(Vertical vertical);

    Project findByVerticalAndProjectKey(Vertical vertical, String projectKey);

    Optional<Project> findByIdAndDeletedAtIsNull(UUID id);

    List<Project> findByVertical_IdAndDeletedAtIsNull(UUID verticalId);
}
