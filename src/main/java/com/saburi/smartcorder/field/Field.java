/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcorder.field;

import com.saburi.smartcorder.utils.Enums;
import com.saburi.smartcorder.utils.Utilities;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 *
 * @author Sam Buriima
 */
@Data
@Builder
public class Field {

    private String fieldName = "";
    private String caption = "";
    private String dataType = "";
    private String references = "";
    private String mapping = "";
    private String key = "";
    private String saburiKey = "";
    @Builder.Default
    private int size = 100;
    @Builder.Default
    private boolean nullable = false;
    @Builder.Default
    private boolean enumerated = false;
    @Builder.Default
    private String subFields = "";
    private String projectName = "";
    @Builder.Default
    private boolean expose = false;
    private String moduleName ="";
    @Builder.Default
    private boolean select = false;

//    private String variableName;
//    private String referencesID;
//    private String display;
//    private String referencesVariableID;
//    private String displayVariableName;
//    private final String displayDataType = "String";


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
}
