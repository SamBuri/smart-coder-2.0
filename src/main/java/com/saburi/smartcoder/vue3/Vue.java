/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vue3;

import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

/**
 *
 * @author samburiima
 */
public class Vue extends Vue3Utils {


    public Vue(FileModel fileModel) {
        super(fileModel);

    }

  

    private Enums.UIControls getUIControl(Field field) {
        Enums.UIControls uIControl = field.getControlType();
        if (uIControl.equals(Enums.UIControls.TextArea) && field.isCollection()&!field.isSelect()) {
            return Enums.UIControls.MultiSelectCombo;
        }
        
        if (uIControl.equals(Enums.UIControls.TextArea) && field.isCollection()&field.isSelect()) {
            return Enums.UIControls.MultiSelectField;
        }
        else if (uIControl.equals(Enums.UIControls.TextField) && field.isNumeric()) {
            return Enums.UIControls.SNumberInput;
        } else if (uIControl.equals(Enums.UIControls.ComboBox) && field.isSelect()) {
//            System.out.println("Field Name: "+field.getColumnName());
            return Enums.UIControls.SelectField;
        }
        
        return uIControl;
    }

    private String makeControl(Field field) {
        Enums.UIControls uIControl = getUIControl(field);

        return switch (uIControl) {
            case ComboBox ->
                "<s-autocomplete".concat(comboProperties(field)).concat("></s-autocomplete>\n");
            case MultiSelectCombo ->
                "<v-autocomplete".concat(multiSelectComboProperties(field)).concat("></v-autocomplete>\n");
            case CheckBox ->
                "<v-checkbox".concat(noValidationProps(field)).concat("></v-checkbox>\n");
            case TextArea ->
                "<s-textarea\n" + textArea(field) + "></s-textarea>\n";
            case TableView ->
                this.crudTable(field);
            case DatePicker ->
                " <s-date-picker" + this.basicNoCounterProperties(field)+ "/>";
            case ImageView ->
                "<s-file-input\n" + fileInput(field) + "></s-file-input>\n";

            case SNumberInput ->
                "<s-number-input\n" + basicProperties(field) + "></s-number-input>\n";

            case SelectField ->
                "<s-select-field\n" + selectField(field) + "></s-select-field>\n";
                
                 case MultiSelectField ->
                "<s-multi-select-field\n" + selectField(field) + "></s-multi-select-field>\n";

            default ->
                "<s-text-field\n" + basicProperties(field) + "></s-text-field>\n";
        };

    }

    private String makeColumnScales(Field field) {
        return field.getControlType().equals(Enums.UIControls.TableView) ? "<v-col cols=\"12\">\n" : "<v-col :cols=\"cols\" :sm=\"sm\" :md=\"md\">\n";
    }

    private String controlColumn(Field field) {
        return makeColumnScales(field).concat(this.makeControl(field)).concat("</v-col>\n");
    }

    private String noValidationProps(Field field) {
        String variableName = this.getVariableName(field);
        return " id=\"" + variableName + "\" label=\"" + field.getCaption() + "\"\n"
                + "          v-model=\"" + "model." + variableName + "\"\n";

    }

    private String basicProperties(Field field) {

        String properties = noValidationProps(field)
                .concat(disableControl(field));
        properties += " :rules=\"rules." + this.getVariableName(field) + "\"\n";
        properties += ":counter=\"" + field.getSize() + "\"\n";
        return properties;

    }
    
    private String basicNoCounterProperties(Field field) {

        String properties = noValidationProps(field)
                .concat(disableControl(field));
        properties += " :rules=\"rules." + getVariableName(field) + "\"\n";
       return properties;

    }


    private String selectField(Field field) {
 return basicProperties(field)+
         "@ok=\"controller."+dialogOkMtdName(field)+"\"\n" +
"          :items=\"controller."+getStoreVariableName(field)+".mini\"\n" +
"          :headers=\"controller."+ Utilities.getVariableName(field.getReferences())+"Nav.menu.miniHeaders\"\n" +
"        ";
        
        

    }
    
   


    private String textValueProperties(Field field) {
        return field.isEnumerated() ? "" : "item-title=\"" + Utilities.getVariableName(field.getReferences()) + "Name\"\n"
                + "          item-propertyValue=\"id\"\n";
    }

    private String comboProperties(Field field) {
        return this.basicNoCounterProperties(field)
                .concat(":items=\"" + callStoreDataVaribale(field) + "\"\n"
                        .concat(":loading=\"" + callStoreDataVaribaleLoading(field) + "\"\n")
                        + textValueProperties(field)   + "          ");

    }

   

    private String multiSelectComboProperties(Field field) {
        return this.comboProperties(field).concat("multiple").concat("\n");
    }

    private String disableControl(Field field) {
        return field.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Read_Only.name())||field.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.UI_Only.name()) ? "disabled\n" : "";

    }

    private String textArea(Field field) {
        return basicProperties(field).concat("rows=\"1\"\n"
                + "           auto-grow");

    }

    private String datePicker(Field field) {
        return "<v-menu\n"
                + "          ref=\"menu\"\n"
                + "          v-model=\"menu\"\n"
                + "          :close-on-content-click=\"true\"\n"
                + "          transition=\"scale-transition\"\n"
                + "          offset-y\n"
                + "          min-width=\"auto\"\n"
                + "        >\n"
                + "          <template v-slot:activator=\"{ on, attrs }\">\n"
                + "            <v-text-field\n"
                + noValidationProps(field)
                + "              prepend-icon=\"mdi-calendar\"\n"
                + "              readonly\n"
                + "              v-bind=\"attrs\"\n"
                + "              v-on=\"on\"\n"
                + "            ></v-text-field>\n"
                + "          </template>\n"
                + "          <v-date-picker\n"
                + "            v-model=\"" + objectVariableName + "." + field.getVariableName() + "\"\n"
                + "            :max=\"\n"
                + "              new Date(Date.now() - new Date().getTimezoneOffset() * 60000)\n"
                + "                .toISOString()\n"
                + "                .substr(0, 10)\n"
                + "            \"\n"
                + "            min=\"1950-01-01\"\n"
                + "          ></v-date-picker>\n"
                + "        </v-menu>";
    }

    String crudTable(Field field) {
        String referenceVariableName = Utilities.getVariableName(field.getReferences());
        return "<crud-table \n"
                + "      title=\"" + field.getCaption() + "\"\n"
                + "       :headers =\"controller." + referenceVariableName + "Nav.menu.editHeaders\"\n"
                + "       :items=\"model." + field.getVariableName() + "\"\n"
                + "       :component=\"controller." + referenceVariableName + "Nav.menu.component\"\n"
                + "       maxWidth=\"700px\"\n"
                + "      />\n";
    }

    private String fileInput(Field field) {
        return this.noValidationProps(field);
    }





    private String makeControlColumns() {
        String controls = "";
        controls = fields.stream()
                .filter(p -> !p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(fieldDAO -> controlColumn(fieldDAO)).reduce(controls, String::concat);
        return controls;
    }

    private String urlLine(Field field) {
        return field.getVariableName() + "Url: null,\n";
    }

    private String urlLines() {
        String urlLines = "";
        urlLines = this.fields
                .stream().filter(p -> p.getDataType().equalsIgnoreCase("Image"))
                .map(f -> urlLine(f))
                .reduce(urlLines, String::concat);
        return urlLines.concat("\n");
    }

 

    private String makeImports() {

        String imports = "import " + this.controllerVariableName + " from \"./" + this.controller + "\";\n";
//        imports += fields.stream()
//                .filter(p -> p.getControlType().equals(UIControls.TableView))
//                .count() > 0 ? "import CrudTable from \"../../components/CrudTable.vue\";\n" : "";

        imports += fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.ImageView))
                .count() > 0 ? "import funcs from '../../utils/funcs'\n" : "";
        
        if(changeFormSize())imports+="import rootOptions from '@/root/RootOptions';\n";

        imports = fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.TableView)
                || p.isSelect())
                .map(f -> importLine(f))
                .distinct()
                .reduce(imports, String::concat);
        return imports;
    }

    private String importLine(Field fd) {
        if (!fd.isReference()) {
            return "";
        }


        String imp = "";
       

        return imp;
    }

    private String dataNavLine(Field fieldDAO) {
        String referenceVariableName = Utilities.getVariableName(fieldDAO.getReferences()).concat("Nav");
        return referenceVariableName.concat(":").concat(referenceVariableName).concat(",\n");

    }

//    private String dataNavLines() {
//        String lines = "";
//        lines = this.fields.stream()
//                .filter(p -> p.getControlType().equals(UIControls.TableView)
//                || p.isSelect())
//                .map(f -> dataNavLine(f)).reduce(lines, String::concat);
//        return lines;
//    }


    public String makeFormDataLine(Field fieldDAO, String variable) {
        return variable + ".append(\"" + fieldDAO.getVariableName() + "\", this." + objectVariableName + "." + fieldDAO.getVariableName() + ");\n";

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

//    private String onFileChanged(FieldDAO fieldDAO) {
//        return "on" + fieldDAO.getFieldName() + "FileChange(e) {\n"
//                + "      if (e) {\n"
//                + "        this." + fieldDAO.getVariableName() + "Url = URL.createObjectURL(e);\n"
//                + "      } else {\n"
//                + "        this." + fieldDAO.getVariableName() + "Url = null;\n"
//                + "      }\n"
//                + "    },\n";
//    }
    private String setImageFileName(Field fieldDAO) {
        return " async set" + fieldDAO.getFieldName() + "File(obj) {\n"
                + "      let " + fieldDAO.getVariableName() + " = obj." + fieldDAO.getVariableName() + ";\n"
                + "      if (" + fieldDAO.getVariableName() + ") {\n"
                + "        this." + fieldDAO.getVariableName() + "Url = \"data:image/png;base64,\" + " + fieldDAO.getVariableName() + ";\n"
                + "        const getUrlExtension = (url) => {\n"
                + "        return url.split(/[#?]/)[0].split(\".\").pop().trim();\n"
                + "      };\n"
                + "      var imgExt = getUrlExtension(this." + fieldDAO.getVariableName() + "Url);\n"
                + "\n"
                + "        const response = await fetch(this." + fieldDAO.getVariableName() + "Url);\n"
                + "        const blob = await response.blob();\n"
                + "        console.log(\"Blob: \", blob);\n"
                + "        const file = new File([blob], \"logo.\" + imgExt, {\n"
                + "          type: blob.type,\n"
                + "        });\n"
                + "      this." + objectVariableName + "." + fieldDAO.getVariableName() + " = file;\n"
                + "\n"
                + "      } else {\n"
                + "        this." + fieldDAO.getVariableName() + "Url = null;\n"
                + "      }\n"
                + "    },\n";
    }

    String callSetFile(Field fieldDAO) {
        return " funcs.createFileFromBytes(obj." + fieldDAO.getVariableName() + ").then(e=>{\n"
                + "        this." + objectVariableName + "." + fieldDAO.getVariableName() + " =e;\n"
                + "\n"
                + "      }).catch(error=> console.log(error));\n";
    }

  

 
    private boolean changeFormSize(){
      return this.fields.stream().filter(f->f.makeTable()).count()>0 ||this.fields.size()>11;
    }
    private String templete() {
        return "<template>\n"
                + "  <crud-form\n"
                + "    :controller=\"controller\"\n>\n"
                + "    <template #heading>" + this.fileModel.getObjectCaption() + "</template>\n"
                + "\n"
                + "    <template #form-data>\n"
                + this.makeControlColumns()
                + "    </template>\n"
                + "  </crud-form>\n"
                + "</template>\n";
    }

    private  String breakPoints (){
    if(!changeFormSize())
     return "const cols = 12;\nconst sm = 6;\n const md = 6;\n";
    
    return "const cols = 12;\nconst sm = 4;\n const md = 4;\n".concat("rootOptions.maxWidth=1000;\n");
    }



    private String initialiseController() {
        return "const controller= " + controllerVariableName + "();\n"
                + "\n"
                + "const model =  controller.model;\n"
                + "const rules= controller.rules;\n";
    }

    private String script() {
        return "<script setup>\n"
                + makeImports()
                + breakPoints()
                + initialiseController()
                + "</script>";
    }

    @Override
    public String create() {
        return this.script().concat(this.templete());
    }

    @Override
    protected String getFileName() {
       return objectName;
    }

    @Override
    protected String getFileExtension() {
       return "vue";
    }

}
