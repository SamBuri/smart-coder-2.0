/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.springboot;


import com.saburi.smartcoder.base.CodeGenerator;
import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;
import com.saburi.smartcoder.java.JavaClass;
import com.saburi.smartcoder.project.Project;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import static com.saburi.smartcoder.utils.Utilities.addIfNotExists;
import static com.saburi.smartcoder.utils.Utilities.isNullOrEmpty;


/**
 *
 * @author Hp
 */
public class Request extends DtoClass {

    public Request(FileModel fileModel) {
        super(fileModel);
       
    }

    @Override
    public String referecesImports(Field field) throws Exception {
        return field.isCollection()||field.isEnumerated()?super.referecesImports(field):"";
    }
    
    
    
      @Override
    protected List<String> getImports(Field field) throws Exception {
          FieldHelper fieldHelper =  new FieldHelper(field);
        return List.of(referecesImports(field),
                nullValidationImport(field),
                sizeValidationImport(field),
                fieldHelper.getDataTypeImps(),
                fieldHelper.getGenericDataTypeImps());
    }

    

    public List requestImports(Project currentProject, boolean considerReferences, Field field) throws Exception {
        List list = new ArrayList();
//        Project lineProject = fieldDAO.getFieldLineProject(currentProject);

        String references = field.getReferences();
        String dataType = field.getDataType();
        boolean isNull = field.isNullable();
        boolean forceReference = this.forceReferences(field);
        if (field.isPrimaryKey()) {
            addIfNotExists(list, "import jakarta.validation.constraints.NotNull");

        }
        if (field.isReference()) {
            if (!isNull) {
                addIfNotExists(list, "import jakarta.validation.constraints.NotNull");
            }

            if (field.isCollection()) {
                addIfNotExists(list, "import java.util." + field.getDataType());
                if (field.getDataType().equalsIgnoreCase("List")) {
//                    addIfNotExists(list, "import java.util.ArrayList");
                } else if (field.getDataType().equalsIgnoreCase("Set")) {
//                    addIfNotExists(list, "import java.util.HashSet");
                }

            }
            if (field.isEnumerated()) {
                String enumPackage = (forceReference) ? project.getBasePackage() : commonProject.getBasePackage();
                addIfNotExists(list, "import " + enumPackage + ".enums." + field.getReferences());

            } else {

                if (considerReferences && field.isCollection()) {

                    if (project.getProjectName().equalsIgnoreCase(currentProject.getProjectName())) {
                        addIfNotExists(list, "import " + project.getBasePackage() + "." + references.toLowerCase().concat(".").concat(references));
                    }

                }
            }
        } else if (dataType.equalsIgnoreCase("String")) {
            addIfNotExists(list, "import jakarta.validation.constraints.Size");

            if (!isNull) {
                addIfNotExists(list, "import jakarta.validation.constraints.NotNull");
            }

        } else {
            if (!isNull) {
                addIfNotExists(list, "import jakarta.validation.constraints.NotNull");
            }
            if (dataType.equalsIgnoreCase("LocalDate")) {
                addIfNotExists(list, "import java.time.LocalDate");
            } else if (dataType.equalsIgnoreCase("LocalDateTime")) {
                addIfNotExists(list, "import java.time.LocalDateTime");
            } else if (dataType.equalsIgnoreCase("Image")) {
                addIfNotExists(list, "import org.springframework.web.multipart.MultipartFile");
            }

        }
        return list;
    }

    private String makeImports() throws Exception {

        String imp = "import lombok.Builder;\nimport lombok.Data;\n";

        return imp+=getImports();
    }

    private String makeAnnotedFields() {
        StringBuilder annotedFields = new StringBuilder();
        for (Field field : this.fields) {

            annotedFields.append(new FieldHelper(field).requestAnnotation());
            annotedFields.append("private ").append(getDeclaration(field, true));

        }
        return annotedFields.toString();
    }

    public String getDeclaration(Field f,  boolean newLine) {
        String newline = newLine ? ";\n" : "";

        if (f.getDataType().equalsIgnoreCase("Image")) {
            return "MultipartFile ".concat(f.getVariableName())
                    .concat(newline);
        }
        if (f.isReference() && !f.isCollection()) {
            return f.getDataType() + " " + this.getVariableName(f).concat(newline);
        }
        return new FieldHelper(f).getDeclaration(forceReferences(f), newLine);

    }

    public String getVariableName(Field f) {

        return Utilities.getVariableName(getFieldName(f));
    }
    
     public String getFieldName(Field f) {

        return new FieldHelper(f).getReqFieldName(forceReferences(f));
    }

    public String getUsableDataType(Field field, boolean forceReferences) {

        if (field.getDataType().equalsIgnoreCase("Image")) {
            return "MultipartFile";
        } else {
            return new FieldHelper(field).getUsableDataType(false);
        }
    }

    private String makeConstructor() {

        String construtorLine = "";
        String construtorInitials = "";
        for (int i = 0; i < fields.size(); i++) {
            Field field = this.fields.get(i);

            if (!field.isCollection()) {

                if (i == 0) {
                    construtorLine += this.getDeclaration(field,  false);
                } else {
                    construtorLine += "," + this.getDeclaration(field, false);
                }
                construtorInitials += "this." + this.getVariableName(field) + " = " + this.getVariableName(field) + ";\n";
            }
        }

        return Utilities.makeMethod("public", "", objectName.concat(Enums.SpringBootFiles.Request.name()), construtorLine, construtorInitials);
    }

    public String makeGetter(Field f) {

        boolean forceReferences  = this.forceReferences(f);
        FieldHelper helper = new FieldHelper(f);
        String type = f.getDataType();

            return Utilities.makeMethod("public", getUsableDataType(f, false), helper.makeGetPrefix() + getFieldName(f), "", "return " + getVariableName(f) + ";");

    }

    public String makeSetter(Field f) {
        boolean forceReferences  = this.forceReferences(f);
        
        return "public ".concat("void").concat(" ").
                concat("set").concat(new FieldHelper(f).getReqFieldName(forceReferences))
                .concat("(" + getDeclaration(f,  false) + "){\n").concat("this.").
                concat(this.getVariableName(f)).concat(" = ").concat(this.getVariableName(f)).concat(";\n}");
    }

    public String makeProperties(Field f) {

        return this.makeGetter(f) + this.makeSetter(f);
    }

    private String makeProperties() {
        String properties = "";
        for (Field field : fields) {
            properties += makeProperties(field);
        }
        return properties;
    }

    public String makeClass(Project project) throws Exception {
      
        String className = objectName + "" + Enums.SpringBootFiles.Request.name();
        String constructor = new JavaClass(className).makeNoArgConstructor().concat("\n") + this.makeConstructor();
        String methods = "";

        String entityPackage = project.getEntityPackage();
        String packageName = isNullOrEmpty(entityPackage)
                ? project.getBasePackage() + "." + objectName.toLowerCase().concat(".dtos") : entityPackage;
        JavaClass javaClass = new JavaClass(packageName, className, this.makeImports(),
                this.makeAnnotedFields(), "", "", methods);
        
        return javaClass.makeClass("", "@Builder\n@Data\n");
    }

    @Override
    protected boolean isValid() {
        CodeGenerator.validate(fields, project);
        return super.isValid(); 
    }
    
    

    @Override
    protected String getFileName() {
        return requestObject;
    }

    

    @Override
    protected String create() throws Exception {
       return makeClass(project);
    }

}
