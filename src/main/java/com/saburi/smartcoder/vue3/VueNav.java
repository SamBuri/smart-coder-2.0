/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vue3;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Utilities;

/**
 * @author samburiima
 */
public class VueNav extends Vue3Utils {

    private final String searchObjectName;

    public VueNav(FileModel fileModel) {
        super(fileModel);
        this.searchObjectName = Utilities.toPlural(objectName);

    }

    private String imports() {
        return "import " + objectName + " from './" + objectName + ".vue'\n "
                + "import " + searchObjectName + " from './" + searchObjectName + ".vue'\n" +
                "import navUtils from \"@/nav/NavUtils\";\n";
    }

    private String objectHead() {
        int width = 700;
        int size = this.fields.size();
        if (size > 8) {
            width = 1000;

        }
        String editPath = this.objectName.toLowerCase();
        return "id: \"" + this.moduleName.toLowerCase() + "." + editPath + "\",\n"
                + "        title: \"" + Utilities.toPlural(this.fileModel.getObjectCaption()) + "\",\n"
                + "        component: " + this.objectName + ",\n"
                + "        path: \"" + Utilities.toPlural(editPath) + "\",\n"
                + "        requires: navUtils.allRoles(\""+editPath+"\"),\n"
                + "        width: \"" + width + "px\",\n";

    }

    private String headerValue(Field field) {
        return getFieldPath(field);
    }

    private String header(Field field) {
        String numeric = " ";
        if (field.isNumeric()) {
            numeric = " ,isNumeric: true";
        }

        String isDate = " ";
        if (field.getDataType().equalsIgnoreCase("Date") || field.getDataType().equalsIgnoreCase("LocalDate")) {
            isDate = ", isDate: true";
        }

        String isDateTime = " ";
        if (field.getDataType().equalsIgnoreCase("DateTime") || field.getDataType().equalsIgnoreCase("LocalDateTime")) {
            isDateTime = ", isDateTime: true";
        }
        return "{ title: \"" + field.getCaption() + "\", property: \"" + headerValue(field) + "\"" + numeric + " " + isDate + "" + isDateTime + "},\n";

    }

    private String headers() {
        String headers = "{\n"
                + "                title: \"Id\",\n"
                + "                align: \"start\",\n"
                + "                // sortable: false,\n"
                + "                property: \"id\",\n"
                + "            },\n";

        headers = fields.stream()
                .filter(f -> !f.isCollection())
                .map(fieldDAO -> this.header(fieldDAO))
                .reduce(headers, String::concat);
        headers = headers.concat(this.constantHeaders());
        headers = "headers: [" + headers + "],\n";

        return headers;

    }

    String constantHeaders() {
        return " { title: \"Branch\", property: \"branch\",},\n"
                + "{ title: \"Creation Date\", property: \"creationDate\",  label: \"Creation Date\", field: \"creationDate\", isDateTime: true },\n" +
                "                { title: \"Last Modified Date\", property: \"lastModifiedDate\", isDateTime: true},\n" +
                "                { title: \"Created By\", property: \"createdBy\", },\n" +
                "                { title: \"Modified By\", property: \"modifiedBy\", }";
    }

    private String editHeaders() {
        String headers = "";

        headers = fields.stream().filter(f -> !f.isCollection())
                .map(fieldDAO -> this.header(fieldDAO))
                .reduce(headers, String::concat);
        headers += "{title: \"Actions\", property: \"actions\"}";
        headers = "editHeaders: [" + headers + "],";

        return headers;

    }

    private String subMenus() {
        String lowername = objectName.toLowerCase();
        return String.format(" children: navUtils.allChildren(\n" +
                "      \"%s\",\n" +
                "      \"%s\",\n" +
                "      \"%s\",\n" +
                "      false\n" +
                "    ),", moduleName, objectName.toLowerCase(), searchObjectName.toLowerCase(), false);
    }

    private String createSubMenu(String name, String path, String params, String icon) {
        String to = " to:{ name: \"" + path + "\", " + params + "}";
        return "{ id: \"" + this.moduleName + "." + objectVariableName + "."
                + name.replaceAll(" ", "")
                .toLowerCase() + "\", title: \"" + name + "\", icon: \"mdi-" + icon + "\",  "
                + to.concat("}");
    }

    private String routes() {
        String editPath = objectName.toLowerCase();
        String searchPath = this.searchObjectName.toLowerCase();
        return String.format("routes: navUtils.allRoutes(\"%s\", %s, \"%s\", %s, true),\n", editPath, objectName, searchPath, searchObjectName);


    }


    @Override
    public String create() {
        String objectNav = Utilities.getVariableName(this.objectName).concat("Nav");
        return this.imports() + " const "
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

    @Override
    protected String getFileName() {
        return objectName.concat("Nav");
    }

    @Override
    protected String getFileExtension() {
        return "js";
    }

}
