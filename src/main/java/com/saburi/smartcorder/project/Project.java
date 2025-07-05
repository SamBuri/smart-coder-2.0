/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcorder.project;


import com.saburi.smartcorder.base.Model;
import com.saburi.smartcorder.utils.Enums.ProjectTypes;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

/**
 *
 * @author Hp
 */
@SuperBuilder
@Data
public class Project extends Model<String> {

    private ProjectTypes projectType;
    private String projectName;
    private String basePackage;
    private String baseFolder;
    private String testFolder;

    private String commonProjectName;
    private String entityPackage;
    private String dBAccessPackage;
    private String contollerPackage;
    private String utilPackage;
    private String enumClass;
    private String objectNameClass;
    private String navigationClass;
    private String entityFolder;
    private String dBAcessFolder;
    private String controllerFolder;
    private String resourceFolder;
    private String menuControllerFile;
    private String searchTreeFile;
    private String menuUIFile;
    private String sQLFile;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }

        Project project = (Project) o;

        return this.projectName.equalsIgnoreCase(project.projectName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.projectName);

    }




    @Override
    public String getDisplay() {
        return this.projectName;
    }
}
