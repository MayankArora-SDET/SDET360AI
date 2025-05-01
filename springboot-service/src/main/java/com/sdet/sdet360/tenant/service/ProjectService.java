package com.sdet.sdet360.tenant.service;

import com.sdet.sdet360.tenant.model.Project;
import com.sdet.sdet360.tenant.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findByDeletedAtIsNull();
    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findByIdAndDeletedAtIsNull(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
    }

    @Transactional
    public Project createProject(Project project) {
        // createdAt/updatedAt will be set automatically
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(UUID projectId, Project details) {
        Project project = getProjectById(projectId);

        project.setProjectKey(details.getProjectKey());
        project.setProjectName(details.getProjectName());
        project.setVertical(details.getVertical());
        // updatedAt will be bumped automatically on save (via @PreUpdate)

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(UUID projectId) {
        Project project = getProjectById(projectId);
        project.setDeletedAt(LocalDateTime.now());
        projectRepository.save(project);
    }
}
