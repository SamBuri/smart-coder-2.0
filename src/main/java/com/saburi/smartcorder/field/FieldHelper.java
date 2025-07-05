package com.saburi.smartcorder.field;

import lombok.Getter;

@Getter
public class FieldHelper {
    private Field field;

    public FieldHelper(Field field) {
        this.field = field;
    }

    public  String getDataTypeImps() {
        String dType = this.field.getDataType();
        if (dType.equalsIgnoreCase("LocalDate")) {
            return "import java.time.LocalDate\n";
        }
        if (dType.equalsIgnoreCase("LocalDateTime")) {
            return "import java.time.LocalDateTime";
        }
        if (dType.equalsIgnoreCase("Date")) {
            return "import java.util.Date";
        }
        if (dType.equalsIgnoreCase("DateTime")) {
            return "import java.util.Date";
        }

        if (dType.equalsIgnoreCase("BigDecimal")) {
            return "import java.math.BigDecimal";
        }

        if (dType.equalsIgnoreCase("List")) {
            return "import java.util.ArrayList";
        }

        if (dType.equalsIgnoreCase("Set")) {
            return "import java.util.HashSet";
        }
        return "";
    }

    public  String getGenericDataTypeImps() {
        String dType = this.field.getDataType();
        if (dType.equalsIgnoreCase("List")) {
            return "import java.util.List";
        }

        if (dType.equalsIgnoreCase("Set")) {
            return "import java.util. Set";
        }
        return "";
    }


    public String getUsableDataType(boolean forceReferences) {
        String references = this.field.getReferences();
        String dataType = field.getDataType();
        if (this.field.isCollection()) {
            return this.field.getDataType() + "<" + references + ">";
        } else if (forceReferences && field.isReference()) {
            return references;
        } else if (field.isEnumerated()) {
            return references;
        } else if (dataType.equalsIgnoreCase("bool")
                || dataType.equalsIgnoreCase("boolean")) {
            return "boolean";
        } else if (dataType.equalsIgnoreCase("Image")) {
            return "byte[]";
        } else {
            return dataType;
        }
    }

    public String getDeclaration(boolean forceReferences, boolean newLine) {
        boolean isCollection = this.field.isCollection();
        String dataType = field.getDataType();
        String variableName = this.field.getVariableName();
        String references = field.getReferences();
        if (newLine) {

            if (isCollection) {
                if (dataType.equalsIgnoreCase("List")) {
                    return "List<" + references + "> " + references + " = new ArrayList<>();\n";
                } else if (dataType.equalsIgnoreCase("Set")) {
                    return "Set<" + references + "> " + references + " = new HashSet<>();\n";
                } else {
                    return "";
                }
            } else {
                return this.getUsableDataType(forceReferences).concat(" ").concat(variableName).concat(";\n");
            }
        }
        return this.getUsableDataType(forceReferences).concat(" ").concat(variableName);

    }

    public String addIdPrefix(String variableName){
        int length = variableName.length();
        if(length<2)return variableName;
        return variableName.substring(length-2, length);
    }

    public boolean isForeignKey(boolean forceReference) {
        return this.field.isReference() && forceReference && !this.field.isEnumerated();
    }


    public String getDBColumnName(boolean forceReferences) {
        String variableName = this.field.getVariableName();

        if (isForeignKey(forceReferences)
                && !this.field.getReferences().equalsIgnoreCase("RevInfo")) {

            return addIdPrefix(variableName).equalsIgnoreCase("id") ? variableName : variableName.concat(forceReferences ? "Id" : "");
        }
        return variableName;
    }

}
