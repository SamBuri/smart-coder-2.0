/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcorder.base;

import com.saburi.smartcorder.base.exceptions.KnownException;
import com.saburi.smartcorder.field.Field;
import com.saburi.smartcorder.project.Project;
import com.saburi.smartcorder.utils.Enums;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author CLINICMASTER13
 */
public class CodeGenerator {

    public CodeGenerator() {
       
    }
    
    public List<Field> getDefailtFields() throws Exception{
      return Arrays.asList(Field.builder().fieldName("UserID").caption("User ID").dataType("String").build(),
              Field.builder().fieldName("UserID").caption("User ID").dataType("String").build(),
              Field.builder().fieldName("UserFullName").caption("User Full Name").dataType("String").build(),
              Field.builder().fieldName("ClientMachine").caption("Client Machine").dataType("String").build(),
              Field.builder().fieldName("RecordDateTime").caption("Record Date Time").dataType("LocalDateTime").build(),
              Field.builder().fieldName("LastUpdateDateTime").caption("Last Update").dataType("LocalDateTime").build());

    }

    public static boolean  validate(List<Field> fields, Project project) throws KnownException {




            if (fields.isEmpty()) {
                throw new KnownException("Must include at least one item to continue");
            }

            List<Field> primaryKeyList = fields.stream().filter(f->f.isPrimaryKey()||f.isPrimaryKeyAuto()).toList();

            if (primaryKeyList.isEmpty() && project.getProjectType().equals(Enums.ProjectTypes.Desktop)) {
                throw new KnownException("You must enter a primary key");
            }
            if (primaryKeyList.size() > 1) {
                throw new KnownException("Composite primary key is not allowed. Please select a primary key");
            }

            for (Field pk : primaryKeyList) {
                if (pk.isPrimaryKeyAuto() && !(pk.getDataType().toLowerCase().contains("int"))) {
                    throw new KnownException("Un supported data type" + pk.getDataType() + " auto generated Id. Please use int instead");
                }
            }

            if (fields.stream().filter(f->f.isDisplayKey()).count()<0) {
                throw new KnownException("You must enter at least one display key");
            }

//            FilteredList<FieldDAO> helperList = new FilteredList<>(FXCollections.observableList(fields), e -> true);
//            helperList.setPredicate(FieldPredicates.isHelper());
            List<Field> helperList = fields.stream().filter(f->f.isHelper()).toList();
            if (!helperList.isEmpty()) {
                if (helperList.size() > 1) {
                    throw new KnownException("You cannot have more than 1 ID Helpers");
                } else {
                    Field idhelperField = helperList.get(0);
                    if (!(idhelperField.getDataType().equalsIgnoreCase("int") || idhelperField.getDataType().equalsIgnoreCase("integer"))) {
                        throw new KnownException("Un suppported data type" + idhelperField + " for ID Helper. Please use int instead");
                    }
                }
            }

            List<Field> generatorList =fields.stream().filter(f->f.isIDGenerator()).toList();
            if (!generatorList.isEmpty()) {
                if (generatorList.size() > 1) {
                    throw new KnownException("You cannot have more than 1 ID Generators");
                } else {

                    if (helperList.isEmpty() && project.getProjectType().equals(Enums.ProjectTypes.Desktop)) {
                        throw new KnownException("You cannot have ID Generator with ID Helper");
                    }
                }
            }
            return true;

    }

}
