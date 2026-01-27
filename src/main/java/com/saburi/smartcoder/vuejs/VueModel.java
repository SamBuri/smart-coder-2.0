/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vuejs;


import com.saburi.smartcoder.field.Field;
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
public class VueModel {

    private final String objectName;
    private final List<Field> fields;
    private final String objectNameVariable;
    private final String modelName;
    private final Project project;

    public VueModel(String objectName, Project project, List<Field> fields) {
        this.objectName = objectName;
        this.project = project;
        this.objectNameVariable = Utilities.getVariableName(this.objectName);
        this.fields = fields;
        this.modelName = this.objectNameVariable.concat("Model");
    }

    private boolean forceReferences(Field field) {
        String proiectName = field.getProjectName();
        return field.isReference() && (proiectName.equalsIgnoreCase(this.project.getProjectName()) || isNullOrEmpty(proiectName));
    }

    private String makeLine(Field field, String separater, String begin, String endLiteral) {
        if (field.getDataType().equalsIgnoreCase("boolean") || field.getDataType().equalsIgnoreCase("bool")) {
            return begin + field.getVariableName().concat(separater).concat("false").concat(endLiteral);
        } else if (field.getDataType().equalsIgnoreCase("Image")) {
            return begin + field.getVariableName().concat(separater).concat("null").concat(endLiteral);
        } else if (field.isCollection()) {
            return begin + field.getVariableName().concat(separater).concat("[]").concat(endLiteral);
        } else if (this.forceReferences(field) && !field.isEnumerated()) {
            return begin + field.getVariableName().concat("Id").concat(separater).concat("\"\"").concat(endLiteral)
                    .concat("\n")
                    .concat(begin + field.getVariableName().concat(separater).concat("null").concat(endLiteral));
        }
        return begin + field.getVariableName().concat(separater).concat("\"\"").concat(endLiteral);
    }

    private String makeInitialLines() {
        String intials = "";
        String separater = ":";
        String begin = "";
        String endLiteral = ",";
        intials = fields.stream()
                .filter(p -> !p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(fieldDAO -> makeLine(fieldDAO, separater, begin, endLiteral)
                .concat("\n"))
                .reduce(intials, String::concat);
        return intials;
    }

    private String makeClearLines() {
        String intials = "";
        String separater = "=";
        String begin = "this.";
        String endLiteral = ";";
        intials = fields.stream()
                .filter(p -> !p.getSaburiKey()
                .equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(fieldDAO -> makeLine(fieldDAO, separater, begin, endLiteral)
                .concat("\n"))
                .reduce(intials, String::concat);
        return intials;
    }

    public String create() {
        return "const " + this.modelName + " = {\n "
                + objectNameVariable + ": {\n" + makeInitialLines().concat("\n")
                        .concat("clear(){\n").concat(makeClearLines()).concat("}\n")
                        .concat("},\n")
                        .concat("path:").concat("\"" + toPlural(objectName).toLowerCase() + "\"")
                        .concat("}\n")
                        .concat("\n")
                        .concat("export default ").concat(modelName).concat(";");

    }

}
