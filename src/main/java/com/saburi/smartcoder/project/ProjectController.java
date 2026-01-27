package com.saburi.smartcoder.project;

import com.saburi.smartcoder.base.controller.BaseController;
import com.saburi.smartcoder.base.exceptions.KnownException;
import com.saburi.smartcoder.base.storage.db.DbStore;
import com.saburi.smartcoder.utils.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/projects")
@RequiredArgsConstructor
public class ProjectController extends BaseController<Project, Integer, Project> {
    private final ProjectRepo projectRepo;

    @Override
    protected DbStore<Project, Integer, Project> getDbStore() {
        return projectRepo;
    }

    @GetMapping("mini/projecttypes/{projectType}")
    public List<ProjectMini> getProjectsByProjectType(@PathVariable Enums.ProjectTypes projectType){
        return projectRepo.findByProjectType(projectType);

    }

    @Override
    protected void validate(Project model) {
        projectRepo.findByProjectName(model.getProjectName())
                .filter(pm -> !pm.getId().equals(model.getId()))
                .ifPresent(pm -> {
                    throw new KnownException(
                            String.format("Project with name %s already exists", model.getProjectName()));
                });
    }
}
