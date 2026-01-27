/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.field;

import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 *
 * @author Sam Buriima
 */
@Data
@Builder
public class Field {
    @Builder.Default
    private String fieldName = "";
    private String caption;
    @Builder.Default
    private String dataType = "String";
    @Builder.Default
    private String references = "";
    @Builder.Default
    private String mapping = "";
    @Builder.Default
    private String key = "";
    @Builder.Default
    private String saburiKey = "";
    @Builder.Default
    private int size = 100;
    @Builder.Default
    private boolean nullable = false;
    @Builder.Default
    private boolean enumerated = false;
    @Builder.Default
    private String subFields = "";
    @Builder.Default
    private String projectName = "";
    @Builder.Default
    private boolean expose = false;
    @Builder.Default
    private String moduleName ="";
    @Builder.Default
    private boolean select = false;

//    private String variableName;
//    private String referencesID;
//    private String display;
//    private String referencesVariableID;
//    private String displayVariableName;
//    private final String displayDataType = "String";


    private String defaultCaption(){
        return ""; //Utilities.getCaption(fieldName);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }

        Field field = (Field) o;

        return this.fieldName.equalsIgnoreCase(field.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.fieldName);

    }

    public String getCaption() {
        return caption != null ? caption : Utilities.getCaption(this.fieldName);
    }

    public boolean isCollection() {
        return this.getDataType().equalsIgnoreCase("List") || this.getDataType().equalsIgnoreCase("Set");
    }

    public String getVariableName() {
        return Utilities.getVariableName(this.fieldName);
    }

    public boolean isReference() {
        return !(this.getReferences().isBlank() || this.getReferences().equalsIgnoreCase("None"));
    }


    public boolean isForeignKey(boolean forceReference) {
        return this.isReference() && forceReference && !this.isEnumerated();
    }

    public boolean isPrimaryKey() {
        return this.getKey().equalsIgnoreCase(Enums.keys.Primary.name());
    }

    public boolean isPrimaryKeyAuto() {
        return this.key.equalsIgnoreCase(Enums.keys.Primary_Auto.name());
    }

    public boolean isHelper() {
        return this.saburiKey.equalsIgnoreCase(Enums.Saburikeys.ID_Helper.name());
    }

    public boolean isIDGenerator() {
        return this.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.ID_Generator.name());
    }
    public boolean isDisplayKey() {
        return this.saburiKey.equalsIgnoreCase(Enums.Saburikeys.Display.name());
    }

    public boolean isPrimitiveNumber() {
        String type = this.dataType;
        return type.equalsIgnoreCase("int") || type.equalsIgnoreCase("Integer")
                || type.equalsIgnoreCase("float")
                || type.equalsIgnoreCase("double");
    }

    public boolean isNumeric() {

        return isPrimitiveNumber()
                || this.dataType.equalsIgnoreCase("BigDecimal");
    }

    public boolean isDate() {
        return this.getDataType().equalsIgnoreCase("Date") || this.getDataType().equalsIgnoreCase("LocalDate");
    }

    public boolean isBoolean() {
        return this.getDataType().equalsIgnoreCase("bool") || this.getDataType().equalsIgnoreCase("boolean");
    }

    public boolean isBigDecimal(){
        return this.dataType.equalsIgnoreCase("BigDecimal");
    }

    public boolean isDateTime() {
        return this.getDataType().equalsIgnoreCase("DateTime") || this.getDataType().equalsIgnoreCase("LocalDateTime");
    }

    public boolean isDateOrDateTime() {return this.isDate() || this.isDateTime();}

     public Enums.UIControls getControlType() {
        return FieldUtils.getControlType(this.dataType, this.isReference(), this.isCollection(), this.isDateOrDateTime(), this.isBoolean(), this.size, this.makeTable());
     }

    public boolean makeTable() {
        return !StringUtils.isBlank(this.subFields);
    }
}
