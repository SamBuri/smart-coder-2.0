/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vue3;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Enums;

import static com.saburi.smartcoder.utils.Utilities.toPlural;

/**
 *
 * @author samburiima
 */
public class VueModel extends Vue3Utils{

    private final String modelName;
    
    public VueModel(FileModel fileModel) {
       super(fileModel);
       
        this.modelName = this.objectVariableName.concat("Model");
    }

    private String makeLine(Field field, String separater, String begin, String endLiteral) {
        String variableName = getVariableName(field);
        if (field.getDataType().equalsIgnoreCase("boolean") || field.getDataType().equalsIgnoreCase("bool")) {
            return begin + variableName.concat(separater).concat("false").concat(endLiteral);
        } else if (field.getDataType().equalsIgnoreCase("Image")||field.isDate()) {
            return begin + variableName.concat(separater).concat("null").concat(endLiteral);
        } else if (field.isCollection()) {
            return begin + field.getVariableName().concat(separater).concat("[]").concat(endLiteral);
        } 
        return begin + variableName.concat(separater).concat("\"\"").concat(endLiteral);
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
    
   
      private String makeCoyLine(Field field){
         
        return String.format("this.%s = obj.%s;", getVariableName(field), getFieldPath(field));
    };
    
         private String getFieldPathDisplay(Field f){
      if(f.isReference() && forceReferences(f)) return f.getVariableName().concat(".displayKey");
      return f.getVariableName();
    }
    private String makeCopyMethod() {
        String copyLines = "this.id=obj.id;\n";
       
        copyLines = fields.stream()
                .filter(p -> !p.getSaburiKey()
                .equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(fieldDAO -> makeCoyLine(fieldDAO)
                .concat("\n"))
                .reduce(copyLines, String::concat);
        return String.format("copy(obj){\n%s\n},", copyLines);
    }
    
     private String makePrintLine(Field field) {

        return "data.push({ text: \"" + field.getCaption() + "\", propertyValue: this." + objectVariableName + "." + getFieldPathDisplay(field) + " });\n";
    }
    
     private String printOptions() {

        String printLines = "";
        printLines = this.fields.stream()
                .filter(f -> !f.getDataType().equalsIgnoreCase("boolean"))
                .map(f -> this.makePrintLine(f))
                .reduce(printLines, String::concat);
        String printData = "let data = [];\n" + printLines + "\n";
        String mtdBody = printData + " return {\n"
                + "        data: data,\n"
                + "        startXPos: 10,\n"
                + "        startYPos: 25,\n"
                + "        lineBreak: 4,\n"
                + "        hSpace: 50,\n"
                + "        vSpace: 10,\n"
                + "        title: \"" + fileModel.getObjectCaption() + "\"\n"
                + "\n"
                + "      };\n";

        return "printOptions(){" + mtdBody + "},\n";
    }
     
      public String makeFormDataLine(Field field, String variable) {
        return variable + ".append(\"" + getVariableName(field)+ "\", this." + getVariableName(field) + ");\n";

    }

    private String makeFormDataLines(String variable) {
        String lines = "";
        lines = this.fields.stream()
                .filter(p -> !p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(f -> makeFormDataLine(f, variable)).reduce(lines, String::concat);
        return lines;
    }

    public String getFormDataMtd() {
        return this.hasMuiltipart ? "getFormData() {\n"
                + "      var data = new FormData();\n" + makeFormDataLines("data") + "\n"
                + "      return data;\n"
                + "    }," : "";
    }
    

    
    @Override
    public String create() {
        return "const " + this.modelName + " = {\n "
                + "model: {\n" + makeInitialLines().concat("\n")
                        .concat("clear(){\n").concat(makeClearLines()).concat("},\n")
                        .concat(makeCopyMethod()).concat("\n")
                        .concat(printOptions()).concat("\n")
                        .concat(getFormDataMtd()).concat("\n")
                        .concat("},\n")
                        .concat("path:").concat("\"" + toPlural(objectName).toLowerCase() + "\"")
                        .concat(",\n")
                        .concat("rules: {\n" + makeRules() + "\n}\n")
                        .concat("}\n")
                        .concat("\n")
                        .concat("export default ").concat(modelName).concat(";");
        
    }
    
    private String getLengthRule(Field field) {
        return (field.getDataType().equalsIgnoreCase("String") && !field.isReference())
                ? "(v) => v.length < " + field.getSize() + " || \"" + field.getCaption() + " length must be "
                + "less or equal to " + field.getSize() + "\"," : "";
    }
    
    private String rules(Field field) {
        if (field.getControlType().equals(Enums.UIControls.CheckBox)) {
            return "";
        }
        String rules = getVariableName(field) + ":[(v) => !!v || \"" + field.getCaption() + " is required\",\n";
        if (field.getDataType().equalsIgnoreCase("String")) {
            rules += getLengthRule(field);
        }
        
        rules += " ],";
        
        return rules;
    }
    
    private String makeRules() {
        String rules = "";
        rules = fields.stream().
                filter(p -> !p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(fieldDAO -> rules(fieldDAO)).reduce(rules, String::concat);
        return rules.concat("\n");
    }

   

    @Override
    protected String getFileName() {
       return this.objectName.concat("Model");
    }

    @Override
    protected String getFileExtension() {
        return "js";
    }
    
}
