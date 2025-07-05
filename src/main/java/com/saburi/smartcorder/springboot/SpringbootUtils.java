/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcorder.springboot;

import com.saburi.smartcorder.base.FileModel;
import com.saburi.smartcorder.base.ProjectFile;
import com.saburi.smartcorder.field.Field;
import com.saburi.smartcorder.field.FieldHelper;
import com.saburi.smartcorder.project.Project;
import com.saburi.smartcorder.project.ProjectFileStorageService;
import com.saburi.smartcorder.utils.Enums;
import com.saburi.smartcorder.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.saburi.smartcorder.utils.Utilities.getVariableName;
import static com.saburi.smartcorder.utils.Utilities.isNullOrEmpty;

/**
 *
 * @author samburiima
 */
public abstract class SpringbootUtils extends ProjectFile {

    protected String objectName;
    protected String moduleName;
    protected List<Field> fields;
    protected String objectVariableName;
    protected String controller;
    protected String controllerVariableName;
    protected String service;
    protected String serviceVaribaleName;
    protected String repo;
    protected String repoVariableName;
    protected String mini;
    protected Project project;
    protected Field primaryKeyFied;
    protected String primaryKeyVariableName;
    protected Field idGenerator;
    protected boolean hasGenerator;
    protected String requestObject;
    protected String idDataType;
    protected Enums.EntityTypes entityType;
    private ProjectFileStorageService projectFileStorageService;
//    private final ProjectDAO oProjectDAO = new ProjectDAO();
    protected Project commonProject;

    public SpringbootUtils(FileModel fileModel, ProjectFileStorageService projectFileStorageService) {
        super(fileModel);

        this.projectFileStorageService = projectFileStorageService;
        try {
            this.project = fileModel.getProject();
            this.objectName = fileModel.getObjectName();
            this.moduleName = fileModel.getModuleName();
            this.fields = fileModel.getFields().stream().filter(f -> !f.getSaburiKey()
                    .equalsIgnoreCase(Enums.Saburikeys.UI_Only.name())).toList();
            this.repo = this.objectName.concat("Repo");
            this.repoVariableName = getVariableName(repo);
            this.service = objectName.concat("Service");
            this.serviceVaribaleName = getVariableName(service);
            this.mini = this.objectName.concat("Mini");
            this.objectVariableName = getVariableName(this.objectName);
            this.controller = this.objectName.concat("Controller");
            this.controllerVariableName = getVariableName(this.controller);

            primaryKeyFied = Utilities.getPrimaryKey(this.fields);
            if (primaryKeyFied != null) {
                this.primaryKeyVariableName = primaryKeyFied.getVariableName();
            }

            this.idGenerator = Utilities.getIDGenerator(fields);
            this.hasGenerator = idGenerator != null;
            requestObject = objectName.concat(Enums.SpringBootFiles.Request.name());

            this.entityType = fileModel.getEntityType();
            this.idDataType = primaryKeyFied == null ? Utilities.getIdWrapperDataType(entityType) : primaryKeyFied.getDataType();
            this.commonProject = projectFileStorageService.findById(project.getCommonProjectName()).orElse(this.project);
        } catch (Exception ex) {
            Logger.getLogger(SpringbootUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String referecesImports(Field field) throws Exception {
        if (!field.isReference()) {
            return "";
        }

        String references = field.getReferences();

        if (field.isEnumerated()) {
            String enumPackage = (this.forceReferences(field)) ? project.getBasePackage() : commonProject.getBasePackage();
            return "import " + enumPackage + ".enums." + references;

        }
        if (!forceReferences(field)) {
            return "";
        }

        return "import " + project.getBasePackage() + "." + references.toLowerCase().concat(".").concat(references);

    }

    public String nullValidationImport(Field field) throws Exception {

        if (field.isPrimaryKey() || !field.isNullable()) {
            return "import jakarta.validation.constraints.NotNull";
        }

        return "";
    }

    public String sizeValidationImport(Field field) throws Exception {
        FieldHelper fieldHelper =  new FieldHelper(field);

        if (field.isCollection()) {
            return "";
        }
        if (fieldHelper.getUsableDataType(forceReferences(field)).equalsIgnoreCase("String")) {
            return "import jakarta.validation.constraints.Size";
        }

        return "";
    }

    public String enumeratedImport(Field fieldDAO) {
        if (!fieldDAO.isEnumerated()) {
            return "";
        }
        return "import jakarta.persistence.Enumerated;\nimport jakarta.persistence.EnumType";
    }
    
      public String joinColumImport(Field fieldDAO) {
        if (!fieldDAO.isForeignKey(forceReferences(fieldDAO))) {
            return "";
        }
        return "import jakarta.persistence.JoinColumn;\nimport jakarta.persistence.ForeignKey";
    }

    private String fixMapping(Field fieldDAO) {
        String mapping = fieldDAO.getMapping();
        if (!fieldDAO.isForeignKey(forceReferences(fieldDAO))) {
            return "";
        }
        if (!isNullOrEmpty(mapping)) {
            return mapping;
        }

        if (fieldDAO.isCollection()) {
            return "OneToMany";
        }
        return "ManyToOne";

    }

    public String mappingImports(Field fieldDAO) throws Exception {

        String mapping = fixMapping(fieldDAO);
        if (isNullOrEmpty(mapping)) {
            return "";
        }
        return "import jakarta.persistence." + mapping;

    }

    public String cascadeImport(Field fieldDAO) {
        if (fieldDAO.isCollection() && fieldDAO.isForeignKey(forceReferences(fieldDAO))) {
            return "import jakarta.persistence.CascadeType";
        }
        return "";
    }

    public String jacksonImports(Field field) {

        if (!field.isCollection()) {
            return "";
        }
        if (field.isForeignKey(forceReferences(field))) {
            return "import com.fasterxml.jackson.annotation.JsonIgnoreProperties";
        }
        return "";

    }
    
      protected List<String> getImports(Field field) throws Exception {
        FieldHelper fieldHelper =  new FieldHelper(field);
        return List.of(referecesImports(field),
                nullValidationImport(field),
                fieldHelper.getDataTypeImps(),
                fieldHelper.getGenericDataTypeImps());
    }

 

    protected String getImports() throws Exception {

        List<String> imports = new ArrayList<>();

        for (Field fieldDAO : fields) {
            imports.addAll(getImports(fieldDAO));
        }

        return imports.stream().distinct().sorted().filter(f->!isNullOrEmpty(f))
                .map(f -> f + ";\n").collect(Collectors.joining());

    }
    
    
    

    protected boolean forceReferences(Field fieldDAO) {

        String projectName = fieldDAO.getProjectName();

        return projectName.equalsIgnoreCase(this.project.getProjectName()) || isNullOrEmpty(projectName);
    }

    @Override
    protected String getBaseFolder() {
        return this.project.getBaseFolder();

    }

    @Override
    protected String getFolderName() {
        return this.objectName.toLowerCase();

    }

    @Override
    protected String getFileExtension() {
        return "java";
    }

}
