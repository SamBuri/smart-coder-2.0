package com.saburi.smartcoder.project;

import com.saburi.smartcoder.utils.Enums;

public interface ProjectMini {
    Integer getId();
    String getProjectName();
    Enums.ProjectTypes getProjectType();
    String getBasePackage();
    String getBaseFolder();
    Parent getParent();

    interface Parent{
        Integer getId();
        String getProjectName();
        Enums.ProjectTypes getProjectType();
        String getBasePackage();
    }
}
