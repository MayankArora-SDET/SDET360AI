package com.sdet.sdet360.tenant.controller;

import com.sdet.sdet360.tenant.model.Project;
import com.sdet.sdet360.tenant.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> list() {
        log.info("Fetching all projects");
        List<Project> projects = projectService.getAllProjects();
        log.debug("Found {} projects", projects.size());
        return projects;
    }

    @GetMapping("/{id}")
    public Project get(@PathVariable UUID id) {
        log.info("Fetching project with id: {}", id);
        try {
            Project project = projectService.getProjectById(id);
            log.debug("Project found: {}", project);
            return project;
        } catch (EntityNotFoundException ex) {
            log.error("Project not found with id: {}", id, ex);
            throw ex;
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project create(@RequestBody Project project) {
        log.info("Creating new project: key={}, name={}", project.getProjectKey(), project.getProjectName());
        Project created = projectService.createProject(project);
        log.debug("Created project with id: {}", created.getId());
        return created;
    }

    @PutMapping("/{id}")
    public Project update(@PathVariable UUID id, @RequestBody Project project) {
        log.info("Updating project id={} with new values: key={}, name={}", id, project.getProjectKey(), project.getProjectName());
        try {
            Project updated = projectService.updateProject(id, project);
            log.debug("Updated project: {}", updated);
            return updated;
        } catch (EntityNotFoundException ex) {
            log.error("Failed to update - project not found id: {}", id, ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        log.info("Deleting (soft) project with id: {}", id);
        try {
            projectService.deleteProject(id);
            log.debug("Soft-deleted project id: {}", id);
        } catch (EntityNotFoundException ex) {
            log.error("Failed to delete - project not found id: {}", id, ex);
            throw ex;
        }
    }
}
