package com.saburi.smartcoder.field;

import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;
import lombok.Getter;

import java.util.Set;

@Getter
public class FieldHelper {
    private final Field field;

    public FieldHelper(Field field) {
        this.field = field;
    }

    public String getDataTypeImps() {
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

    public String getGenericDataTypeImps() {
        String dType = this.field.getDataType();
        if (dType.equalsIgnoreCase("List")) {
            return "import java.util.List";
        }

        if (dType.equalsIgnoreCase("Set")) {
            return "import java.util. Set";
        }
        return "";
    }

    public String lombokAnnotation(){
        if(this.field.isCollection()){return "@Builder.Default\n";}
        return "";
    }



    public String requestAnnotation() {
        String saburiKey = field.getSaburiKey();
        String key = field.getKey();
        String dataType = field.getDataType();
        String caption = field.getCaption();
        int size = field.getSize();

        boolean isPrimary = key.equalsIgnoreCase(Enums.keys.Primary.name());
        boolean isIdGenerator = saburiKey.equalsIgnoreCase(Enums.Saburikeys.ID_Generator.name());
        boolean isReference = field.isReference();
        boolean isCollection = field.isCollection();
        boolean nullable = field.isNullable();

        // ID_Generator overrides nullable
        if (isIdGenerator) {
            field.setNullable(false);
            nullable = false;
        }

        // Special case: ID_Helper → no annotations


        StringBuilder ann = new StringBuilder();
        boolean needsNotNull = !nullable;

        // Primary property: always @NotNull, and @Size if String and not reference
        if (isPrimary) {
            ann.append(notNull(caption));
            if (!isReference && "String".equalsIgnoreCase(dataType) && size > 0) {
                ann.append(sizeAnnotation(caption, size));
            }
            return ann.toString();
        }

        // Non-collection reference: @NotNull if required
        if (isReference && !isCollection) {
            if (needsNotNull) ann.append(notNull(caption));
            return ann.toString();
        }

        // String fields (non-primary): @Size + @NotNull
        if ("String".equalsIgnoreCase(dataType)) {
            if (size > 0) ann.append(sizeAnnotation(caption, size));
            if (needsNotNull) ann.append(notNull(caption));
            return ann.toString();
        }

        // All other types that only need @NotNull when not nullable
        // Includes: LocalDate, LocalDateTime, int/Integer/float/double, Image
        Set<String> notNullOnlyTypes = Set.of(
                "LocalDate", "LocalDateTime",
                "int", "Integer", "float", "double",
                "Image"
        );

        if (notNullOnlyTypes.contains(dataType) && needsNotNull) {
            ann.append(notNull(caption));
        }

        return ann.toString();
    }

    // Helper methods for clarity and reuse
    private String notNull(String caption) {
        return "@NotNull(message = \"The field: " + caption + " cannot be null\")\n";
    }

    private String sizeAnnotation(String caption, int size) {
        return "@Size(max = " + size + ", message = \"The field: " + caption +
                " size cannot be greater than " + size + "\")\n";
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

    public String addIdPrefix(String variableName) {
        int length = variableName.length();
        if (length < 2) return variableName;
        return variableName.substring(length - 2, length);
    }

    public boolean isForeignKey(boolean forceReference) {
        return this.field.isReference() && forceReference && !this.field.isEnumerated();
    }



    public String getReqFieldName(boolean forceReferences) {
        String fieldName = this.field.getFieldName();

        if (isForeignKey(forceReferences)
                && !this.field.getReferences().equalsIgnoreCase("RevInfo")) {

            return addIdPrefix(fieldName).equalsIgnoreCase("id") ? fieldName : fieldName.concat(forceReferences ? "Id" : "");
        }
        return fieldName;
    }

    public String getReqVariableName(boolean forceReferences) {
       return Utilities.getVariableName(getReqFieldName(forceReferences));
    }


    public String getDBColumnName(boolean forceReferences) {
        String variableName = this.field.getVariableName();

        if (isForeignKey(forceReferences)
                && !this.field.getReferences().equalsIgnoreCase("RevInfo")) {

            return addIdPrefix(variableName).equalsIgnoreCase("id") ? variableName : variableName.concat(forceReferences ? "Id" : "");
        }
        return variableName;
    }

    public String makeGetPrefix() {
        String type = this.getField().getDataType();
        if (type.equalsIgnoreCase("bool") || type.equalsIgnoreCase("boolean")) {
            return "is";
        } else {
            return "get";

        }
    }

    public String makeGetter(boolean forceReferences) {
        return Utilities.makeMethod("public", getUsableDataType(false), this.makeGetPrefix() + this.field.getFieldName(), "", "return " + this.field.getVariableName() + ";");

    }

//    public boolean hasCrudTable() {
//        return this.getControlType().equals(Enums.UIControls.TableView);
//    }

    public String getCall() {
       return String.format("%s%s()", this.makeGetPrefix(), this.field.getFieldName());

    }

//    public String getReqFieldName(boolean forceReferences) {
//        if (isForeignKey(forceReferences) && !
//                getReferences().equalsIgnoreCase("RevInfo")
//                &&!getReferences().endsWith("Id")) {
//            return this.getFieldName().concat(forceReferences ? "Id" : "");
//        }
//        return this.getFieldName();
//    }
}
