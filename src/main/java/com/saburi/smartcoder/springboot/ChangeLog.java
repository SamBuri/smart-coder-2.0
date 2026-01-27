/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcoder.springboot;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author samburiima
 */
public class ChangeLog extends ResourceFile {

    public ChangeLog(FileModel fileModel) {
        super(fileModel);
        this.fields = this.fields.stream().filter(f -> !f.getFieldName().equalsIgnoreCase("Id")).collect(Collectors.toList());
    }

    public List<Field> constantFields() throws Exception {

        return List.of(
                Field.builder().fieldName("ClientId").build(),
                Field.builder().fieldName("CreatedBy").build(),
                Field.builder().fieldName("ModifiedBy").build(),
                Field.builder().fieldName("CreationDate").dataType("long").build(),
                Field.builder().fieldName("LastModifiedDate").dataType("long").build(),
                Field.builder().fieldName("Branch").build(),
                Field.builder().fieldName("BranchId").build(),
                Field.builder().fieldName("OrganisationId").build(),
                Field.builder().fieldName("Organisation").build()

        );
    }

    public List<Field> indexColumns() throws Exception {
        return List.of(Field.builder().fieldName("BranchId").build(),
                Field.builder().fieldName("OrganisationId").build()
        );
    }

    public List<Field> auditFields() {

        return List.of(Field.builder().fieldName("Rev").dataType("int").references("RevInfo").build(),
                Field.builder().fieldName("Revtype").dataType("short").build()
        );
    }

    private String makeColumnName(Field field) {

        String name = "";

        for (char c : new FieldHelper(field).getDBColumnName(this.forceReferences(field)).toCharArray()) {
            String cs = String.valueOf(c);
            if (cs.toUpperCase().equals(cs)) {
                name += "_" + cs.toLowerCase();
            } else {
                name += c;
            }
        }

        return name;

    }

    private String makeColumn(Field fieldDo, String prefix) {
        String columnName = this.makeColumnName(fieldDo);

        String nameType = "name=\"" + columnName + "\" type=\"" + this.getDataType(fieldDo) + "\"";
        String autoIncrement = "";
        boolean isAutoIdByEntityType = (this.entityType == Enums.EntityTypes.Auto_ID_Int || this.entityType == Enums.EntityTypes.Auto_ID_Long);
        if (!isAudit(prefix) && (fieldDo.isPrimaryKeyAuto() || (fieldDo.isPrimaryKey() && isAutoIdByEntityType))) {
            autoIncrement = " autoIncrement=\"true\"";
        }

        String column = "<column  ".concat(nameType).concat(autoIncrement).concat(this.makeDefault(fieldDo)).concat(">\n").concat(this.makeColumnConstraints(fieldDo, prefix)).concat("</column>");

        return column;

    }

    private String createIndex(Field field, String tableName) {
        String columnName = this.makeColumnName(field);
        return String.format(" <createIndex tableName=\"%s\" indexName=\"idx_%s_%s\">\n" +
                "    <column name=\"%s\"/>\n" +
                "</createIndex>", tableName, tableName, columnName, columnName);
    }

    private String makePrimaryKeyField(String prefix) {
        if (this.primaryKeyFied != null) {
            return "";
        }

        Field field = Field.builder().fieldName("Id").dataType(idDataType).key(Enums.keys.Primary.name()).size(20).build();
        return this.makeColumn(field, prefix);


    }

    private String makeIdHelperField(String prefix) {

        boolean isIdGen = this.entityType.equals(Enums.EntityTypes.Auto_ID_Gen);

        if (!isIdGen) {
            return "";
        }
        Field field = Field.builder().fieldName("IdHelper").dataType("int").key(Enums.Saburikeys.ID_Helper.name()).build();
        return this.makeColumn(field, prefix);


    }

    private String makeConstantColums(String prefix) {
        String columns = "";
        try {
            columns = this.constantFields().stream().map(f -> makeColumn(f, prefix)).reduce(columns, String::concat);
        } catch (Exception ex) {
            Logger.getLogger(ChangeLog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return columns;
    }

    private String tableName(String prefix) {
        return Utilities.toPlural(objectName).concat(prefix).toLowerCase();
    }

    private boolean isAudit(String prefix) {
        return prefix.equalsIgnoreCase("_aud") || prefix.equalsIgnoreCase("_rev");
    }

    private String makeColumnConstraints(Field field, String prefix) {
        boolean isPrimary = field.isPrimaryKey() || field.isPrimaryKeyAuto();
        boolean isForeign = field.isForeignKey(this.forceReferences(field));
        boolean isUnique = field.getKey().equalsIgnoreCase(Enums.keys.Unique.name());
        boolean isNullable = field.isNullable();

        if (isNullable && !isForeign && !isUnique && !isPrimary) {
            return "";
        }

        String columnName = makeColumnName(field);
        String tableName = tableName(prefix);
        String constraintName = tableName + "_" + columnName;

        String pk = "";
        if (isPrimary) {
            pk = "primaryKey=\"true\" primaryKeyName=\"pk_" + constraintName + "\" ";
        }

        String fk = "";
        if (isForeign) {
            String referenceTableName = Utilities.toPlural(field.getReferences()).toLowerCase();
            String referencedColumnName = referenceTableName.equalsIgnoreCase("revinfo") ? "rev" : "id";

            fk = "foreignKeyName=\"fk_" + constraintName + "\"  referencedTableName=\"" + referenceTableName + "\" " + "referencedColumnNames=\"" + referencedColumnName + "\" ";
        }

        String uk = "";
        boolean isRevision = isAudit(prefix);
        if (isUnique && !isRevision) {
            uk = " unique=\"true\" uniqueConstraintName=\"uk_" + constraintName + "\" ";
        }

        String nullable = isRevision ? "" : isNullable ? "" : " nullable=\"false\"";
        return "<constraints " + pk + fk + uk + nullable + "/>\n";
    }

    private String getDataType(Field field) {
        String dataType = field.getDataType();
        if (dataType.equalsIgnoreCase("String")) {

            return "VARCHAR(" + field.getSize() + ")";
        } else if (dataType.equalsIgnoreCase("Image") || dataType.equalsIgnoreCase("File")) {
            return "LONGBLOB";
        } else if (dataType.equalsIgnoreCase("int") || dataType.equalsIgnoreCase("Integer")) {
            return "INT";
        } else if (dataType.equalsIgnoreCase("long") || dataType.equalsIgnoreCase("Long")) {
            return "BIGINT";
        } else if (dataType.equalsIgnoreCase("short")) {
            return "TINYINT(3)";
        } else if (dataType.equalsIgnoreCase("LocalDate") || dataType.equalsIgnoreCase("Date")) {
            return "DATE";
        } else if (dataType.equalsIgnoreCase("BigDecimal")) {
            return "DECIMAL(15,2)";
        } else {
            return dataType.toUpperCase();
        }
    }

    private String makeDefault(Field field) {
        String dataType = field.getDataType();
        if (dataType.equalsIgnoreCase("BigDecimal")) {
            return " defaultValue=\"0\"";
        }
        return "";
    }

    private String createIndexColumns(String tableName) {
        String indeces = "";
        try {
            indeces = this.indexColumns().stream().map(f -> this.createIndex(f, tableName)).reduce(indeces, String::concat);
            return indeces;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String getUniqueGroupsConstraints(String tableName) {
        List<Field> uniqueGroups = fields.stream().filter((p) -> p.getKey().equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());

        String uniqueColumnGroupsConstraints = "";
        if (!uniqueGroups.isEmpty()) {
            Field fdao = uniqueGroups.get(0);
            String fColName = makeColumnName(fdao);
            String columnNames = makeColumnName(fdao);
            String constraintName = "uk_" + tableName + "_" + fColName;
            for (int i = 1; i < uniqueGroups.size(); i++) {

                String colName = makeColumnName(uniqueGroups.get(i));
                columnNames += "," + colName;
                constraintName += "_" + colName;
            }

            uniqueColumnGroupsConstraints = " <changeSet id=\"add_" + constraintName + "\" author=\"Sam Buriima\">\n" + "        <addUniqueConstraint tableName=\"" + tableName + "\" columnNames=\"" + columnNames + "\" constraintName=\"" + constraintName + "\"/>\n" + "    </changeSet>\n";
        }
        return uniqueColumnGroupsConstraints;
    }

    public String makeChangeSet() {
        String tableName = Utilities.toPlural(objectName).toLowerCase();
        String author = "Sam Buriima";
        String id = project.getProjectName() + "_create_" + tableName;
        String prefix = "";
        String columns = this.makePrimaryKeyField(prefix);
        columns += makeIdHelperField(prefix);
        columns = fields.stream().filter(f -> !f.isCollection()).map(f -> makeColumn(f, prefix)).reduce(columns, String::concat);

        columns += makeConstantColums(prefix);


        String uniqueColumnGroupsConstraints = getUniqueGroupsConstraints(tableName);

        return "<changeSet author=\"" + author + "\" id=\"" + id + "\">\n" + "    <createTable tableName=\"" + tableName + "\">\n" + columns + "    </createTable>\n" + createIndexColumns(tableName) + "\n " +
                " <rollback>\n" + "            <dropTable tableName=\"" + tableName + "\"/>\n" + "        </rollback>" + "</changeSet>\n" + uniqueColumnGroupsConstraints + "\n";
    }

    public String makeAuditChangeSet() {
        String prefix = "_aud";
        String tableName = tableName(prefix);
        String author = "Sam Buriima";
        String id = project.getProjectName() + "_create_" + tableName;

        String columns = this.makePrimaryKeyField(prefix);
        String revColums = "";

        revColums = auditFields().stream().map(f -> makeColumn(f, prefix)).reduce(revColums, String::concat);
        columns += revColums;
        columns += makeIdHelperField(prefix);
        columns = fields.stream().filter(f -> !f.isCollection()).map(f -> makeColumn(f, prefix)).reduce(columns, String::concat);

        columns += makeConstantColums(prefix);

//        List<FieldDAO> uniqueGroups = fields.stream().filter((p) -> p.getProperty()
//                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());
//        String uniqueColumnGroupsConstraints = "";
//        if (!uniqueGroups.isEmpty()) {
//            FieldDAO fdao = uniqueGroups.get(0);
//            String fColName = makeColumnName(fdao);
//            String columnNames = "";
//            String constraintName = "uk_" + tableName + "_" + fColName;
//            for (int i = 1; i < uniqueGroups.size(); i++) {
//
//                FieldDAO fieldDAO = uniqueGroups.get(i);
//                String colName = makeColumnName(fieldDAO);
//                columnNames += "," + colName;
//                constraintName += "_" + colName;
//            }
//
//            uniqueColumnGroupsConstraints = " <changeSet id=\"add_" + constraintName + "\" author=\"Sam Buriima\">\n"
//                    + "        <addUniqueConstraint tableName=\"" + tableName + "\" columnNames=\"" + columnNames + "\" constraintName=\"" + constraintName + "\"/>\n"
//                    + "    </changeSet>\n";
//        }
        return "<changeSet author=\"" + author + "\" id=\"" + id + "\">\n" + "    <createTable tableName=\"" + tableName + "\">\n" + columns + "   " +
                " </createTable>\n" +

                "" + "  <rollback>\n" + "            <dropTable tableName=\"" + tableName + "\"/>\n" + "        </rollback>" + "" +
                "</changeSet>\n";
    }

    public String makeDocument() {

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<databaseChangeLog\n" + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + "        xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\n" + "        xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\n" + "        xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd\n" + "    http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd\">\n"
                .concat(makeChangeSet()).concat("\n")
                .concat(makeAuditChangeSet()).concat("\n")
                .concat("</databaseChangeLog>");

    }

    @Override
    protected String getFolderName() {
        return "changelogs".concat(FILE_SEPARATOR).concat(objectName).toLowerCase();
    }

    @Override
    protected String getFileName() {
        return "create_" + Utilities.toPlural(objectName).toLowerCase();
    }

    @Override
    protected String getFileExtension() {
        return "xml";
    }

    @Override
    protected String create() throws Exception {
        return makeDocument();
    }

}
