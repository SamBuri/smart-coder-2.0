/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcoder.vue3;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.base.ProjectFile;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;
import com.saburi.smartcoder.project.Project;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;

import static com.saburi.smartcoder.utils.Utilities.isNullOrEmpty;
import static com.saburi.smartcoder.utils.Utilities.toPlural;

/**
 *
 * @author samburiima
 */
public abstract class Vue3Utils extends ProjectFile {

    protected String objectName;
    protected String moduleName;
    protected List<Field> fields;
    protected String objectVariableName;
    protected String controller;
    protected String controllerVariableName;
    protected String store = "Store";
    protected Project project;
    protected final boolean hasMuiltipart;

    public Vue3Utils(FileModel fileModel) {
        super(fileModel);
        this.project = fileModel.getProject();
        this.objectName = fileModel.getObjectName();
        this.moduleName = fileModel.getModuleName();
        this.fields = fileModel.getFields().stream().filter(f -> !f.getSaburiKey()
                .equalsIgnoreCase(Enums.Saburikeys.Query_Only.name())).toList();
        this.objectVariableName = Utilities.getVariableName(this.objectName);
        this.controller = this.objectName.concat("Controller");
        this.controllerVariableName = Utilities.getVariableName(this.controller);
        this.hasMuiltipart = Utilities.hasMultipart(fields);
    }

    public boolean forceReferences(Field fieldDAO) {
        String proiectName = fieldDAO.getProjectName();
        return fieldDAO.isReference() && (proiectName.equalsIgnoreCase(this.project.getProjectName()) || isNullOrEmpty(proiectName));
    }

    public String getModuleName(Field fieldDAO) {
        String module = fieldDAO.getModuleName();
        return isNullOrEmpty(module) ? this.moduleName : module;

    }

    public boolean isSameModule(Field fieldDAO) {
        return this.moduleName.equalsIgnoreCase(this.getModuleName(fieldDAO));
    }

    public String getVariableName(Field field) {
        if(field.isCollection()) return field.getVariableName();
        return new FieldHelper(field).getDBColumnName(true);
    }
    
    public String dialogOkMtdName(Field field){
    return getVariableName(field)+ "Ok";}

    public String getReferenceObjectName(Field field) {
        if (!field.isReference()) {
            return "";
        }

        String prefix = field.isEnumerated() ? getModuleName(field) : field.getReferences();
        return prefix.substring(0, 1).toUpperCase() + prefix.substring(1) + store;

    }

    public String getStoreName(Field field) {
        if (!field.isReference()) {
            return "";
        }

        String prefix = field.isEnumerated() ? getModuleName(field) : field.getReferences();
        return prefix.substring(0, 1).toUpperCase() + prefix.substring(1) + store;

    }

    public String getStoreVariableName(Field field) {
        if (!field.isReference()) {
            return "";
        }
        return Utilities.getVariableName(getStoreName(field));
    }

    public String defineStoreFunction(Field field) {
        if (!field.isReference()) {
            return "";
        }
        return "define" + getStoreName(field);
    }

    private String importPath(Field field) {
        if (!field.isReference()) {
            return "";
        }
        String references = field.getReferences();
        String objectFolder = field.isEnumerated() ? "" : "/" + references;
        String path = String.format("@/%s%s", getModuleName(field), objectFolder);
        System.out.println("Path " + path);
        return path.toLowerCase();

    }

    public String importPath(Field field, String filename) {
        if (!field.isReference()) {
            return "";
        }
        return this.importPath(field) + "/" + filename;

    }

    protected String getFieldPath(Field f) {

        if (f.isForeignKey(forceReferences(f)) && !f.isCollection()) {
            return f.getVariableName().concat(".id");
        }
        return f.getVariableName();
    }

    public String predictLookupObjectName(Field field) {
        String references = field.getReferences();
        String fieldName = field.getFieldName();
        if (field.isEnumerated()) {
            return references;
        }
        String lookupName = references.equalsIgnoreCase("LookupData") ? fieldName : references;

        int length = lookupName.length();
        if (lookupName.substring(length - 2, length).equalsIgnoreCase("id")) {
            lookupName = lookupName.substring(0, length - 2);
        }
        return toPlural(lookupName);
    }

    public String getReferencingName(Field field) {
        if (!field.isReference()) {
            return "";
        }
        return field.getReferences().equalsIgnoreCase("LookupData") ? predictLookupObjectName(field)
                : field.isEnumerated() ? predictLookupObjectName(field) : "Mini";
    }

    public String getStoreAction(Field field) {
        return String.format("get%s();\n", getReferencingName(field));
    }

    public String callStoreAction(Field field) {
        return String.format("%s.%s\n", getStoreVariableName(field), getStoreAction(field));

    }

    public String callStoreDataVaribale(Field field) {
        if (!field.isReference()) {
            return toPlural(field.getVariableName());
        }
        return String.format("controller.%s.%s", getStoreVariableName(field),
                Utilities.getVariableName(getReferencingName(field)));
    }

    public String callStoreDataVaribaleLoading(Field field) {
        if (!field.isReference()) {
            return toPlural(field.getVariableName())+"Loading";
        }
        return String.format("controller.%s.%sLoading", getStoreVariableName(field),
                Utilities.getVariableName(getReferencingName(field)));
    }

    @Override
    protected String getBaseFolder() {
        return this.project.getBaseFolder();

    }

    @Override
    protected String getFolderName() {
        return this.moduleName.concat(FILE_SEPARATOR).concat(objectName).toLowerCase();

    }

}
