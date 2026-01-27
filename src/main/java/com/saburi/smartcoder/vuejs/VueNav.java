/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vuejs;

import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;

/**
 *
 * @author samburiima
 */
public class VueNav {

    private final String objectName;
    private final String objectCaption;
    private final String moduleName;
    private final List<Field> fields;
    private final String objectVariablename;
    private final String searchObjectName;

    public VueNav(FileModel fileModel) {
        this.objectName = fileModel.getObjectName();
        this.moduleName = fileModel.getModuleName().toLowerCase();
        this.fields = fileModel.getFields();
        this.objectVariablename = Utilities.getVariableName(objectName);
        this.objectCaption = fileModel.getObjectCaption();
        this.searchObjectName = Utilities.toPlural(objectName);
       
    }
    
    private String imports(){
        return "import "+objectName+" from './"+objectName+".vue'\n "
                + "import "+searchObjectName+" from './"+searchObjectName+".vue'\n";
    }

    private String objectHead() {
          int width = 700;
        int size = this.fields.size();
        if (size > 8) {
            width = 1000;
         
        }
        return "id: \"" + this.moduleName.toLowerCase() + "." + this.objectName.toLowerCase() + "\",\n"
                + "        name: \"" + Utilities.toPlural(this.objectCaption) + "\",\n"
                + "        component: " + this.objectName + ",\n"
                + "        path: \"" + Utilities.toPlural(this.objectName.toLowerCase()) + "\",\n"
                + "        width: \""+width+"px\",\n";

    }

    private String headerValue(Field field) {
        return field.isReference() && !field.isEnumerated() ? field.getVariableName() : field.getVariableName();
    }

    private String header(Field field) {
        String numeric = " ";
        if(field.isNumeric()){
            numeric = " ,isNumeric: true";
        }
        
        String isDate = " ";
        if(field.getDataType().equalsIgnoreCase("Date")||field.getDataType().equalsIgnoreCase("LocalDate")){
            isDate = ", isDate: true";
        }
        
        String isDateTime = " ";
        if(field.getDataType().equalsIgnoreCase("DateTime")||field.getDataType().equalsIgnoreCase("LocalDateTime")){
            isDateTime = ", isDateTime: true";
        }
        return "{ text: \"" + field.getCaption() + "\", propertyValue: \""+ headerValue(field)+"\"" +numeric+" "+isDate+""+isDateTime+"},\n";

    }

    private String headers() {
        String headers = "{\n"
                + "                text: \"Id\",\n"
                + "                align: \"start\",\n"
                + "                // sortable: false,\n"
                + "                propertyValue: \"id\",\n"
                + "            },\n";

        headers = fields.stream().map(fieldDAO -> this.header(fieldDAO)).reduce(headers, String::concat);
        headers = headers.concat(this.constantHeaders());
        headers = "headers: [" + headers + "],\n";

        return headers;

    }
    
    String constantHeaders(){
     return " { text: \"Branch\", propertyValue: \"branch\", label: \"Branch\", field: \"branch\" },\n"
             + "{ text: \"Creation Date\", propertyValue: \"creationDate\",  label: \"Creation Date\", field: \"creationDate\", isDateTime: true },\n" +
"                { text: \"Last Modified Date\", propertyValue: \"lastModifiedDate\", isDateTime: true},\n" +
"                { text: \"Created By\", propertyValue: \"createdBy\",  label: \"Created By\", field: \"createdBy\"},\n" +
"                { text: \"Modified By\", propertyValue: \"modifiedBy\", label: \"Modified By\", field: \"modifiedBy\"}";
    }
    
    private String editHeaders() {
        String headers = "";

        headers = fields.stream().map(fieldDAO -> this.header(fieldDAO)).reduce(headers, String::concat);
        headers+="{text: \"Actions\", propertyValue: \"actions\"}";
        headers = "editHeaders: [" + headers + "],";

        return headers;

    }

    private String subMenus() {
        return "children: ["
                + createSubMenu("New", "0").concat(",\n")
                + createSubMenu("Edit", "1").concat(",\n")
                + createSubMenu("History", "2").concat(",\n")
                        .concat("]");
    }

    private String createSubMenu(String name, String mode) {
        return "{ id: \"" + this.moduleName + "." + objectVariablename + "." + name.replaceAll(" ", "").toLowerCase() + "\", name: \"" + name + "\", route: \"" + objectVariablename.toLowerCase() + "\", mode: " + mode + "}";
    }
    
    private String routes(){
        String editPath = objectName.toLowerCase();
        String searchPath = this.searchObjectName.toLowerCase();
        
        return " routes:[\n" +
"        {\n" +
"                path: '/" + editPath + "/:mode',\n" +
"                name: '" + editPath + "',\n" +
"                component: " + objectName + ",\n" +
"                meta: { auth: true },\n" +
"         },\n" +
"         {\n" +
"                path: '/"+searchPath+"',\n" +
"                name: '"+searchPath+"',\n" +
"                component: "+searchObjectName+",\n" +
"                meta: { auth: true },\n" +
"         },\n" +
"        \n" +
"        ],\n";
        
    }
    

    public String create() {
        String objectNav = Utilities.getVariableName(this.objectName).concat("Nav");
        return this.imports()+" const "
                .concat(objectNav)
                .concat("=")
                .concat("{")
                .concat(routes())
                .concat("menu:{")
                .concat(this.objectHead())
                .concat(this.editHeaders())
                .concat(this.headers())
                .concat(this.subMenus())
                .concat("}\n}\n")
                .concat("export default ".concat(objectNav).concat(";"));

    }

}
