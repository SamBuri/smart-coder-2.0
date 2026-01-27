/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.project;


import com.saburi.smartcoder.base.entities.AutoIncEntity;
import com.saburi.smartcoder.utils.Enums.ProjectTypes;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Hp
 */
//@SuperBuilder
@Getter
@Setter
@jakarta.persistence.Entity
@Table(name = "projects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_projects_project_name", columnNames = {"project_name"})
})
public class Project extends AutoIncEntity {
    @Column(name = "project_name")
    private String projectName;
    private String language;
    @Enumerated(EnumType.STRING)
    private ProjectTypes projectType;
    /**
     * All java projects shall have a common project where everything else springs from
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Project parent;

    /**
     * This should be the src folder for most project type
     */
    private String basePackage;
    private String baseFolder;
    private String testFolder;
    private String resourceFolder;

    /**
     * Version of your company conventions this project follows (e.g. "v2.4.1")
     */
    private String conventionVersion = "latest";

    /**
     * When was this project last scanned / validated
     */
    private LocalDateTime lastScannedAt;


    /**
     * Optional Git remote URL – useful for future "open in GitHub" actions
     */
    private String gitRemoteUrl;

    /**
     * Optional notes (e.g. "Uses Kotlin instead of Java", "Vue 2 legacy")
     */
    private String notes;

    /**
     * Safety flag – if true, generator will NEVER write files without --force
     */
//    @Builder.Default
    private boolean dryRunByDefault = true;

    /**
     * If true, generator will ask for confirmation even in scripts
     */
//    @Builder.Default
    private boolean requireConfirmation = true;

    /**
     * Timestamp when this config was created
     */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when config was last modified
     */
    private LocalDateTime updatedAt = LocalDateTime.now();
    private String entityPackage;
    private String dBAccessPackage;
    private String controllerPackage;
    private String utilPackage;
    private String enumClass;
    private String objectNameClass;
    private String navigationClass;
    private String entityFolder;
    private String dBAcessFolder;
    private String controllerFolder;
    private String menuControllerFile;
    private String searchTreeFile;
    private String menuUIFile;
    private String sQLFile;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project project)) {
            return false;
        }

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
