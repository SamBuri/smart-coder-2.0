/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcorder.springboot;

import com.saburi.smartcorder.base.CodeGenerator;
import com.saburi.smartcorder.base.FileModel;
import com.saburi.smartcorder.base.exceptions.KnownException;
import com.saburi.smartcorder.field.Field;
import com.saburi.smartcorder.field.FieldHelper;
import com.saburi.smartcorder.java.JavaClass;
import com.saburi.smartcorder.project.Project;
import com.saburi.smartcorder.project.ProjectFileStorageService;
import com.saburi.smartcorder.utils.Enums;
import com.saburi.smartcorder.utils.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.saburi.smartcorder.utils.Utilities.*;


/**
 * @author CLINICMASTER13
 */
public class Entity extends SpringbootUtils {
    public Entity(FileModel fileModel, ProjectFileStorageService projectFileStorageService) {
        super(fileModel, projectFileStorageService);
        this.fields = this.fields.stream().filter(f -> !f.getFieldName().equalsIgnoreCase("Id")).collect(Collectors.toList());

    }

//    private ProjectDAO oProjectDAO = new ProjectDAO();
//    private ProjectFileStorageService projectFileStorageService;

//    public Entity(FileModel fileModel, ProjectFileStorageService projectFileStorageService) {
//        super(fileModel, projectFileStorageService);
//        this.fields = this.fields.stream().filter(f -> !f.getFieldName().equalsIgnoreCase("Id")).collect(Collectors.toList());
//
//    }

    @Override
    protected List<String> getImports(Field field) throws Exception {
        FieldHelper fieldHelper = new FieldHelper(field);
        return List.of(referecesImports(field),
                nullValidationImport(field),
                fieldHelper.getDataTypeImps(),
                sizeValidationImport(field),
                cascadeImport(field),
                joinColumImport(field),
                enumeratedImport(field),
                mappingImports(field),
                fieldHelper.getGenericDataTypeImps());
    }


    public String makeEntityImports(Project project, Enums.EntityTypes entityTypes) throws Exception {
        String idImp = project.getProjectType().equals(Enums.ProjectTypes.JavaFX) ? """
                import jakarta.persistence.Id;
                 """ : "";
        String imp = """
                import java.util.Objects;
                import jakarta.persistence.Column;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Table;
                import org.hibernate.envers.Audited;
                import org.hibernate.envers.RelationTargetAuditMode;
                import lombok.Builder;
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.Setter;
                """ + idImp;
        List<Field> uniqueGroups = fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());
        if (!uniqueGroups.isEmpty()) {
            imp += """
                     import jakarta.persistence.UniqueConstraint;
                    """;
        }
        if (project.getProjectType().equals(Enums.ProjectTypes.Springboot)) {
            if (!commonProject.getProjectName().equalsIgnoreCase(project.getProjectName())) {
                imp += "import " + commonProject.getBasePackage() + ".entities." + getParentEntity(entityTypes) + ";\n";
                imp += "import " + commonProject.getBasePackage() + ".dtos.ResponseData;\n";
            }
        }
//        List<String> imports = new ArrayList();
//        for (FieldDAO t : this.fields) {
//            t.entityImports(project, forceReferences(t)).forEach(i -> addIfNotExists(imports, i));
//
//        };
//        for (String impo : imports) {
//            imp += impo + ";\n";
//        }
        return imp + getImports();
    }

    public String makeAnnotedFields() {
        String annotedFields = "";
        for (Field field : this.fields) {
            if (field.isReference() && field.isPrimaryKey()) {
                annotedFields += """
                        @Id
                        @Column(length = """ + field.getSize() + ", updatable = false)";
                annotedFields += "private String" + objectName.concat(this.forceReferences(field) ? "Id" : "");
            }

            annotedFields += this.entityAnnotation(field, objectName, primaryKeyVariableName, forceReferences(field));
            annotedFields += "private " + new FieldHelper(field).getDeclaration(forceReferences(field), true);

        }
        return annotedFields;
    }

    protected String entityAnnotation(Field fieldDAO, String objectName, String primaryKeyVariableName, boolean consideredReferences) {
        String fieldAnnotation = "";
        if (fieldDAO.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.ID_Generator.name())) {
            fieldDAO.setNullable(false);
        }
        String column = "";
        int size = fieldDAO.getSize();
        String references = fieldDAO.getReferences();
        String caption = fieldDAO.getCaption();
        String key = fieldDAO.getKey();
        String dataType = fieldDAO.getDataType();
        boolean isReferences = fieldDAO.isReference();
        boolean isCollection = fieldDAO.isCollection();
        boolean isEnumerated = fieldDAO.isEnumerated();
        boolean isNullable = fieldDAO.isNullable();
        String mapping = fieldDAO.getMapping();
        String saburiKey = fieldDAO.getSaburiKey();
        String fieldName = fieldDAO.getFieldName();
        String variableName = fieldDAO.getVariableName();

        if (fieldDAO.getKey().equalsIgnoreCase(Enums.keys.Primary.name())) {
            if (fieldDAO.isReference()) {
                fieldAnnotation += "@NotNull(message =  \"The field: " + caption + " cannot be null\")\n";
                if (fieldDAO.isEnumerated()) {
                    fieldAnnotation += "@Id\n";
                    fieldAnnotation += "@Size(max =  " + size + ", message =  \"The field: " + caption + " size cannot be greater than " + size + "\")\n";
                    fieldAnnotation += "@Enumerated(EnumType.STRING)\n";
                    fieldAnnotation += "@Column(length = " + size + ")";

                } else {
                    if (consideredReferences) {
                        fieldAnnotation += "@MapsId(\"" + references.concat("Id") + "\")\n"
                                + "    @OneToOne\n"
                                + "    @JoinColumn(name = \"" + objectName.concat("Id") + "\", referencedColumnName = \"" + references.concat("ID") + "\", insertable = true)";
                    }
                }
            } else {
                fieldAnnotation += "@Id\n";
                fieldAnnotation += "@NotNull(message =  \"The field: " + caption + " cannot be null\")\n";
                if (dataType.equalsIgnoreCase("String")) {
                    fieldAnnotation += "@Size(max =  " + size + ", message =  \"The field: " + caption + " size cannot be greater than " + size + "\")\n";
                    column += "@Column(length = " + size + ", updatable = false)";
                } else {
                    column += "@Column(updatable = false)";
                }
            }
        } else if (key.equalsIgnoreCase(Enums.keys.Primary_Auto.name())) {
            fieldAnnotation += "@Id\n"
                    + "    @GeneratedValue(strategy = GenerationType.AUTO)\n"
                    + "    @Column(updatable = false, nullable = false)\n";
        } else if (isCollection) {

            if (mapping.isBlank()) {
                mapping = "OneToMany";
            }
            fieldAnnotation += "@Builder.Default\n";
            fieldAnnotation += "@" + mapping + "(cascade = CascadeType.MERGE, mappedBy = \"" + Utilities.getVariableName(objectName) + "\")\n";
            if (isReferences) {
                fieldAnnotation += "@JsonIgnoreProperties(\"" + Utilities.getVariableName(objectName) + "\")";
//                fieldAnnotation += "@JoinTable(name = \"" + objectName + getFieldName() + "\",\n"
//                        + "            joinColumns = {\n"
//                        + "                @JoinColumn(name = \"" + primaryKeyVariableName + "\", nullable = false)},\n"
//                        + "            inverseJoinColumns = {\n"
//                        + "                @JoinColumn(name = \"" + Utilities.getVariableName(getReferences()) + "ID\", nullable = false)})";
            }

        } else if (isReferences
                && !isCollection) {
            if (!isNullable) {
                fieldAnnotation += "@NotNull(message =  \"The field: " + caption + " cannot be null\")\n";
            }
            if (isEnumerated) {
                fieldAnnotation += "@Enumerated(EnumType.STRING)\n";
                fieldAnnotation += "@Column(length = " + size + ")";

            } else {
                if (consideredReferences) {
                    if (mapping.isBlank()) {
                        mapping = "OneToOne";
                    }

                    if (key.isBlank()) {
                        key = "Foreign";

                    }

                    column = "@JoinColumn(name = \"" + variableName.concat("Id") + "\"";
                    column += ",foreignKey = @ForeignKey(name = \"fk" + fieldName.concat("Id") + objectName + "\")";

                    if (key.equalsIgnoreCase("Unique")) {
                        column += ",unique = true";
                    }
                    column += ")";
                    fieldAnnotation += "@" + mapping + "\n";
                }

            }
        } else if (saburiKey.equalsIgnoreCase(Enums.Saburikeys.ID_Helper.name())) {
            column = "@Column(updatable = false)";
        } else if (dataType.equalsIgnoreCase("String")) {
            fieldAnnotation += "@Size(max =  " + size + ", message =  \"The field: " + caption + " size cannot be greater than " + size + "\")\n";

            column += "@Column(";
            if (!isReferences) {
                column += "length =  " + size;
            }

            if (key.equalsIgnoreCase("Unique")) {
                column += ", unique = true";
            }
            column += ")\n";
            if (!isNullable) {
                fieldAnnotation += "@NotNull(message =  \"The field: " + caption + " cannot be null\")\n";
            }

        } else if (dataType.equalsIgnoreCase("LocalDate") || dataType.equalsIgnoreCase("LocalDateTime")) {

            if (!isNullable) {
                column += "@Column(name = \"" + variableName + "\",nullable = false)";
                fieldAnnotation += "@NotNull(message =\"The field: " + caption + " cannot be null\")\n";
            }
        } else if (dataType.equalsIgnoreCase("int") || dataType.equalsIgnoreCase("Integer")
                || dataType.equalsIgnoreCase("float") || dataType.equalsIgnoreCase("double")) {
            column += "@Column(name = \"" + variableName + "\"";
            if (!isNullable) {
                column += ",nullable = false";
                fieldAnnotation += "@NotNull(message =\"The field: " + caption + " cannot be null\")\n";
            }
            if (key.equalsIgnoreCase("Unique")) {
                column += ",unique = false";
                fieldAnnotation += "@NotNull(message =\"The field: " + caption + " cannot be null\")\n";
            }
            column += ")";
        } else if (dataType.equalsIgnoreCase("Image")) {
            fieldAnnotation += "@Lob\n";

            if (!isNullable) {
                fieldAnnotation += "@NotNull(message =  \"The field: " + caption + " cannot be null\")\n";
            }

        }

        fieldAnnotation += column;
        return fieldAnnotation;
    }

//    public String makeProperties() {
//        String properties = "";
//        for (Field field : fields) {
//            properties += field.makeProperties(forceReferences(field));
//        }
//        return properties;
//    }

    //    public String makeSetters() {
//        String setters = "";
//        for (Field field : fields) {
//            setters += field.makeSetter();
//        }
//        return setters;
//    }
    public String overriddenID(Project project) {
        if (project.getProjectType().equals(Enums.ProjectTypes.Springboot)) {
            return "";
        }

        String primaryKey = "";
        for (Field field : fields) {
            if (field.getKey().equalsIgnoreCase(Enums.keys.Primary.name()) || field.getKey().equalsIgnoreCase(Enums.keys.Primary_Auto.name())) {
                primaryKey = field.getVariableName();
            }
        }

        if (primaryKey.equalsIgnoreCase("id")) {
            return "";
        }
        String method = " @Override\npublic Object getId(){\n";
        if (!primaryKey.isBlank()) {
            method += "return this." + primaryKey + ";\n";
        } else {
            method += " throw new UnsupportedOperationException(\"Not supported yet.\");\n";
        }
        method += "}\n";
        return method;
    }

    public String overriddenDisplayKey() {
        String displayKey = "";
        String displayVariableName = "";

        List<Field> displayKeys = fields.stream().filter((p) -> p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Display.name())).collect(Collectors.toList());

        for (Field field : displayKeys) {
            if (field.isForeignKey(forceReferences(field))) {
                displayVariableName = field.getVariableName().concat(".getDisplayKey()");
            } else if (field.isReference() && field.isEnumerated()) {
                displayVariableName = field.getVariableName().concat(".name()");
            } else {
                displayVariableName = field.getVariableName();
            }

            if (displayKeys.indexOf(field) == 0) {
                displayKey += displayVariableName;
            } else {
                displayKey += "+\" \"+this." + displayVariableName;
            }

        }

        if (displayKey.equalsIgnoreCase("displayKey")) {
            return "";
        }
        String method = " @Override\npublic String getDisplayKey(){\n";
        if (!displayKey.isBlank()) {
            method += "return this." + displayKey + ";\n";
        } else {
            method += " throw new UnsupportedOperationException(\"Not supported yet.\");\n";
        }
        method += "}\n";
        return method;
    }

    public String otherMethods(Project project, Enums.EntityTypes entityTypes) {

        boolean isAutoPK;
        if (project.getProjectType().equals(Enums.ProjectTypes.JavaFX)) {
            isAutoPK = primaryKeyFied.isPrimaryKeyAuto();
        } else {
            isAutoPK = entityTypes.equals(Enums.EntityTypes.Auto_ID_Int) || entityTypes.equals(Enums.EntityTypes.Auto_ID_Long);
        }

        List<Field> uniqueGroups = fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());

        String body = """
                if (this == o) {
                            return true;
                        }
                        if (o == null) {
                            return false;
                        }
                        if (getClass() != o.getClass()) {
                            return false;
                        }
                        final """ + " " + this.objectName + " " + objectVariableName + " = (" + this.objectName + ") o;";
        if (isAutoPK && !uniqueGroups.isEmpty()) {
            int last = uniqueGroups.size() - 1;
            List<Field> middleUniqueFileds = new ArrayList<>(uniqueGroups);
            middleUniqueFileds.remove(last);
            body = middleUniqueFileds.stream().map((fieldDAO) -> " if (!Objects.equals(this." + fieldDAO.getVariableName() + ", " + objectVariableName + "." + fieldDAO.getVariableName() + ")) {\n"
                    + "            return false;\n"
                    + "        }").reduce(body, String::concat);
            Field fieldDAO = uniqueGroups.get(last);
            body += "return Objects.equals(this." + fieldDAO.getVariableName() + ", " + objectVariableName + "." + fieldDAO.getVariableName() + ");";

        } else {
            body += " if (this.getId() == null || " + objectVariableName + ".getId() == null) {\n"
                    + "            return false;\n"
                    + "        }\nreturn this.getId().equals(" + objectVariableName + ".getId());\n";

        }
        String objectEquals = makeMethod("@Override\npublic", "boolean", "equals", "Object o", body);

        String hashBody = "";
        if (isAutoPK && !uniqueGroups.isEmpty()) {
            int last = uniqueGroups.size() - 1;
            List<Field> middleUniqueFileds = new ArrayList<>(uniqueGroups);
            middleUniqueFileds.remove(last);
            hashBody = middleUniqueFileds.stream().map((fieldDAO) -> " Objects.hashCode(this." + fieldDAO.getVariableName() + ")+")
                    .reduce(hashBody, String::concat);
            Field fieldDAO = uniqueGroups.get(last);
            hashBody += "Objects.hashCode(this." + fieldDAO.getVariableName() + ");";
            hashBody = "return " + hashBody;

        } else {
            hashBody = " return Objects.hashCode(this." + (project.getProjectType().equals(Enums.ProjectTypes.JavaFX) ? primaryKeyVariableName : "id") + ");\n";

        }

        String objectHashCode = makeMethod("@Override\npublic", "int", "hashCode", "", hashBody);
        String idValueMessage = "";
        if (project.getProjectType().equals(Enums.ProjectTypes.Springboot)) {
            if (entityTypes.equals(Enums.EntityTypes.Auto_ID_Gen)) {

                Field idgenerator = Utilities.getIDGenerator(fields);
                if (idgenerator != null) {
                    String generatorValue = idgenerator.getVariableName();

                    if (idgenerator.isReference()) {
                        if (idgenerator.isEnumerated()) {
                            generatorValue = idgenerator.getVariableName();
                        } else {
                            generatorValue = idgenerator.getVariableName() + ".getId()";
                        }
                    }

                    idValueMessage += "@Override\n"
                            + "    public String getIdGenerateValue() {\n"
                            + "       return  String.valueOf(this." + generatorValue + ");\n"
                            + "    }";
                }
            }
        }

        return objectEquals + objectHashCode + idValueMessage;

    }

    public String makeClass(Project project, Enums.EntityTypes entityTypes) throws Exception {

        List<Field> uniqueGroups = fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());
        String uniqueColumnGroupsConstraints = "";
        if (!uniqueGroups.isEmpty()) {
            Field fdao = uniqueGroups.get(0);
            FieldHelper fieldHelper = new FieldHelper(fdao);
            String columnName = fieldHelper.getDBColumnName(this.forceReferences(fdao));


            String columnNames = "\"" + columnName + "\"";
            String constraintName = "uq" + fdao.getFieldName();
            for (int i = 1; i < uniqueGroups.size(); i++) {
                Field fieldDAO = uniqueGroups.get(i);
                columnNames += "," + "\"" + columnName + "\"";
                constraintName += fieldDAO.getFieldName();
            }

            uniqueColumnGroupsConstraints = " ,uniqueConstraints = @UniqueConstraint(columnNames = {" + columnNames + "}, "
                    + "name = \"" + constraintName + "\")\n";
        }
        String constructor = new JavaClass(objectName).makeNoArgConstructor().concat("\n");
        String methods = otherMethods(project, entityTypes) + this.overriddenID(project) + this.overriddenDisplayKey();

        String entityPackage = project.getEntityPackage();
        String packageName = isNullOrEmpty(entityPackage)
                ? project.getBasePackage() + "." + objectName.toLowerCase() : entityPackage;
        String imp = project.getProjectType().equals(Enums.ProjectTypes.Springboot) ? "implements ResponseData" : "";

        JavaClass javaClass = new JavaClass(packageName, objectName, this.makeEntityImports(project, entityTypes),
                this.makeAnnotedFields(), constructor, "", methods);

        String entityAnnotation = "@Entity\n@Builder\n@AllArgsConstructor\n"
                + "@Getter\n"
                + "@Setter\n";
        String tableName = Utilities.toPlural(objectName).toLowerCase();
        String tableAnnotation = "@Table(name = \"" + tableName + "\"" + uniqueColumnGroupsConstraints + ")";
        String auditAnnotation = "@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)";

        String topAnnotations = entityAnnotation.concat("\n").concat("\n").concat(tableAnnotation).concat("\n").concat(auditAnnotation);
        return javaClass.makeClass(entityTypes, imp, topAnnotations);
    }

    @Override
    protected boolean isValid() throws KnownException {
        CodeGenerator.validate(fields, project);
        return super.isValid();
    }

    @Override
    protected String getFileName() {
        return objectName;
    }

    @Override
    protected String create() throws Exception {
        return makeClass(project, entityType);
    }

}
