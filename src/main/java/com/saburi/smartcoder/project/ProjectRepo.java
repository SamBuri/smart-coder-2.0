package com.saburi.smartcoder.project;

import com.saburi.smartcoder.base.storage.db.DbStore;
import com.saburi.smartcoder.utils.Enums;

import java.util.List;
import java.util.Optional;


public interface ProjectRepo extends DbStore<Project, Integer, Project> {

    List<ProjectMini> findByProjectType(Enums.ProjectTypes projectType);

    Optional<ProjectMini> findByProjectName(String projectName);
}
