/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.vuejs;


import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;
import java.util.stream.Collectors;

import static com.saburi.smartcoder.utils.Utilities.isNullOrEmpty;
import static com.saburi.smartcoder.utils.Utilities.toPlural;

/**
 *
 * @author samburiima
 */
public class Vue {

    private final String objectName;
    private final List<Field> fields;
    private final String objectNameVariable;
    private final String modelName;
    private final String objectCaption;
    private final String moduleName;
    private final boolean hasMuiltipart;

    public Vue(String objectName, String moduleName, String objectCaption, List<Field> fields) {
        this.objectName = objectName;
        this.moduleName = moduleName;
        this.objectCaption = objectCaption;
        this.objectNameVariable = Utilities.getVariableName(this.objectName);
        this.fields = fields;
        this.modelName = this.objectNameVariable.concat("Model");
        this.hasMuiltipart = Utilities.hasMultipart(fields);
    }

    private boolean forceReferences(Field field) {
        String mName = field.getModuleName();
        return field.isReference() && (mName.equalsIgnoreCase(this.moduleName) || isNullOrEmpty(mName));
    }

    private Enums.UIControls getUIControl(Field field) {
        Enums.UIControls uIControl = field.getControlType();
        if (uIControl.equals(Enums.UIControls.TextArea) && field.isCollection()) {
            return Enums.UIControls.MultiSelectCombo;
        } else if (uIControl.equals(Enums.UIControls.TextField) && field.isNumeric()) {
            return Enums.UIControls.SNumberInput;
        } else if (uIControl.equals(Enums.UIControls.ComboBox) && field.isSelect()) {
            return Enums.UIControls.SelectField;
        }
        return uIControl;
    }

    private String makeControl(Field fieldDAO) {
        Enums.UIControls uIControl = getUIControl(fieldDAO);

        return switch (uIControl) {
            case ComboBox ->
                "<v-select".concat(comboProperties(fieldDAO)).concat("></v-select>\n");
            case MultiSelectCombo ->
                "<v-select".concat(multiSelectComboProperties(fieldDAO)).concat("></v-select>\n");
            case CheckBox ->
                "<v-checkbox".concat(noValidationProps(fieldDAO)).concat("></v-checkbox>\n");
            case TextArea ->
                "<v-textarea\n" + textArea(fieldDAO) + "></v-textarea>\n";
            case TableView ->
                this.crudTable(fieldDAO);
            case DatePicker ->
                " <s-date-picker" + this.noValidationProps(fieldDAO)
                .concat(" :rules=\"" + fieldDAO.getVariableName() + "Rules\"\n") + "/>";
            case ImageView ->
                "<s-file-input\n" + fileInput(fieldDAO) + "></s-file-input>\n";

            case SNumberInput ->
                "<s-number-input\n" + basicProperties(fieldDAO) + "></s-number-input>\n";

            case SelectField ->
                "<v-text-field\n" + selectField(fieldDAO) + "></v-text-field>\n";

            default ->
                "<v-text-field\n" + basicProperties(fieldDAO) + "></v-text-field>\n";
        };

    }

    private String makeColumnScales(Field field) {
        return field.getControlType().equals(Enums.UIControls.TableView) ? "<v-col cols=\"12\">\n" : "<v-col :cols=\"cols\" :sm=\"sm\" :md=\"md\">\n";
    }

    private String controlColumn(Field field) {
        return makeColumnScales(field).concat(this.makeControl(field)).concat("</v-col>\n");
    }

    private String noValidationProps(Field fieldDAO) {
        String variableName = fieldDAO.getVariableName(); 
        return " id=\""+variableName+"\" label=\"" + fieldDAO.getCaption() + "\"\n"
                + "          v-model=\"" + this.objectNameVariable + "." + variableName + "\"\n";

    }

    private String basicProperties(Field fieldDAO) {

        String properties = noValidationProps(fieldDAO)
                .concat(disableControl(fieldDAO));
        properties += " :rules=\"" + fieldDAO.getVariableName() + "Rules\"\n";
        properties += ":counter=\"" + fieldDAO.getSize() + "\"\n";
        return properties;

    }

    private String selectField(Field fieldDAO) {

        return this.basicProperties(fieldDAO)
                .concat("\n")
                .concat("append-icon=\"mdi-view-list\" @click:append=\"show" + fieldDAO.getFieldName() + "Dialog\"");

    }

    public String dialogModelVariableName(Field field) {
        return field.getVariableName() + "Dialog";
    }

    public String dialogModelDataVariable(Field field) {
        return dialogModelVariableName(field) + ": false,\n";
    }

    public String dialogDataObjectDataName(Field field) {
        return field.getVariableName() + "Data";
    }

    public String dialogDataObject(Field field) {
        return dialogDataObjectDataName(field) + ": null,\n";
    }

    public String showDialogDataVariables() {
        return fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.dialogModelDataVariable(f))
                .collect(Collectors.joining());
    }

    public String dialogDataObjects() {
        String dObject = fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.dialogDataObject(f))
                .collect(Collectors.joining());

        return dObject.equals("") ? "" : dObject.concat("mtdsProvided: true,\n");
    }

    public String dialogDataWatcher(Field field) {
        String dialogDataObjectName = dialogDataObjectDataName(field);
        String body = "if(this." + dialogDataObjectName + "){\n"
                + "        this." + objectNameVariable + "." + field.getVariableName() + " = this." + dialogDataObjectName + ".id;\n"
                + "      }\n";
        return Utilities.makeMethodJs("", "", dialogDataObjectName,
                "", body);
    }

    public String dialogDataWatchers() {
        return fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.dialogDataWatcher(f))
                .collect(Collectors.joining());
    }

    private String dialog(Field field) {
        String variable = field.getVariableName();
        String referenceVariableName = Utilities.getVariableName(field.getReferences());
        return "<v-dialog v-model=\"" + dialogModelVariableName(field) + "\" persistent :max-with=\"600\">\n"
                + "        <search-mini :mtdsProvided=\"mtdsProvided\" @ok=\"" + variable + "Ok\" @close=\"" + variable + "Close\" :items=\"$store.state." + getStateVariable(field) + "\"\n"
                + "          :headers=\"" + referenceVariableName + "Nav.menu.miniHeaders\" />\n"
                + "      </v-dialog>\n";
    }

    public String dialogs() {
        return fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.dialog(f))
                .collect(Collectors.joining());
    }

    private String dialogMethods(Field field) {

        String dialogModelVariableName = dialogModelVariableName(field);
        String variableName = field.getVariableName();
        return variableName + "Ok(data){\n"
                + "      this." + this.dialogDataObjectDataName(field) + " = data\n"
                + "    },\n"
                + variableName + "Close(){\n"
                + "      this." + dialogModelVariableName + " = false;\n"
                + "    },\n"
                + "\n"
                + "    show" + field.getFieldName() + "Dialog(){\n"
                + "      this." + dialogModelVariableName + " = true;\n"
                + "    },\n";
    }

    private String dialogMethods() {
        return fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.dialogMethods(f))
                .collect(Collectors.joining());
    }

    private String referenceComputed(Field f) {

        if (!forceReferences(f)) {
            return "";
        }
        return f.getVariableName()+"(){\n"
                + "      return this."+this.objectNameVariable + "." + f.getVariableName()+";\n"
                + "    },\n";
    }
    
        private String referenceWatcher(Field f) {

        if (!forceReferences(f)) {
            return "";
        }
        String variableName = f.getVariableName();
        return variableName+"(){\n"
                +"if(this."+variableName+"){"
                + "       this."+this.objectNameVariable + "." + variableName+"Id =this." + variableName+".id;\n"
                + "    }\n"
                + "},\n";
    }

    private boolean isSameModule(Field field) {
        return this.moduleName.equalsIgnoreCase(this.getModuleName(field));
    }

    private String getModuleName(Field field) {
        String module = field.getModuleName();
        return isNullOrEmpty(module) ? this.moduleName : module;

    }

    private String textValueProperties(Field field) {
        return field.isEnumerated() ? "" : "item-text=\"" + Utilities.getVariableName(field.getReferences()) + "Name\"\n"
                + "          item-propertyValue=\"id\"\n";
    }

    private String comboProperties(Field field) {
        return this.basicProperties(field)
                .concat(":items=\"$store.state." + getStateVariable(field) + "\"\n"
                        .concat(":loading=\"$store.state." + getStateVariable(field).concat("Loading") + "\"\n")
                        + textValueProperties(field)
                        + "          ").concat(comboReturnObject(field));

    }

    private String comboReturnObject(Field field) {
        return this.forceReferences(field) ? "return-object\n" : "";
    }

    private String multiSelectComboProperties(Field field) {
        return this.comboProperties(field).concat("multiple").concat("\n");
    }

    private String disableControl(Field field) {
        return field.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Read_Only.name()) ? "disabled\n" : "";

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
                + "            v-model=\"" + objectNameVariable + "." + field.getVariableName() + "\"\n"
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
                + "       :headers =\"" + referenceVariableName + "Nav.menu.editHeaders\"\n"
                + "       :items=\"" + objectNameVariable + "." + field.getVariableName() + "\"\n"
                + "       :component=\"" + referenceVariableName + "Nav.menu.component\"\n"
                + "       maxWidth=\"700px\"\n"
                + "      />\n";
    }

    private String fileInput(Field field) {
        return this.noValidationProps(field);
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
        String rules = field.getVariableName() + "Rules: [(v) => !!v || \"" + field.getCaption() + " is required\",\n";
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

    private String makeControlColumns() {
        String controls = "";
        controls = fields.stream()
                .filter(p -> !p.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.Query_Only.name()))
                .map(field -> controlColumn(field)).reduce(controls, String::concat);
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

    private String data() {
        int sm = 6;
        int md = 4;
        int width = 700;
        int size = this.fields.size();
        if (size > 8) {
            width = 1000;
            md = 3;
        }

        return """
               data: () => ({
                   cols: 12,
                   sm:""" + sm + ",\n" + ""
                + "       md: " + md + ",\n" + ""
                + "       maxWidth:" + width + ",\n"
                + "path: " + this.modelName + ".path,\n"
                + "    " + this.objectNameVariable + ": " + this.modelName + "." + this.objectNameVariable + ",\n"
                + dataNavLines()
                + makeRules()
                + urlLines()
                + showDialogDataVariables()
                + dialogDataObjects()
                + "showPrintPrompt: true,"
                + "  }),\n";
    }

    private String predictLookupObjectName(Field field) {
        String references = field.getReferences();
        String fieldName = field.getFieldName();
        if (field.isEnumerated()) {
            return references;
        }
        String lookupName = references.equalsIgnoreCase("LookupData") ? fieldName : references;

        int length = lookupName.length();
        if (lookupName.substring(length - 2, length).equalsIgnoreCase("id")) {
            lookupName = lookupName.substring(0, length - 2);
        }
        return lookupName;
    }

    private String getReferencingName(Field field) {
        return field.getReferences().equalsIgnoreCase("LookupData") ? toPlural(predictLookupObjectName(field))
                : field.isEnumerated() ? predictLookupObjectName(field) : "Mini";
    }

    private String getStateVariable(Field field) {
        return storePath(field, ".") + Utilities.getVariableName(this.getReferencingName(field));

    }

    private String storePath(Field fieldDAO, String literal) {
        String mName = this.getModuleName(fieldDAO).toLowerCase();
        String rV = fieldDAO.isEnumerated() ? "" : fieldDAO.getReferences().toLowerCase().concat(literal);
        return mName.concat(literal) + rV;
    }

    private String makeImports() {

        String imports = "import " + this.modelName + " from \"./" + this.objectName + "Model\";\n";
        imports += fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.TableView))
                .count() > 0 ? "import CrudTable from \"../../components/CrudTable.vue\";\n" : "";

        imports += fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.ImageView))
                .count() > 0 ? "import funcs from '../../utils/funcs'\n" : "";

        imports = fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.TableView)
                || p.isSelect())
                .distinct().map(f -> importLine(f)).reduce(imports, String::concat);
        return imports;
    }

    private String importLine(Field field) {
        String mName = isSameModule(field) ? "" : getModuleName(field).concat("/");
        String references = field.getReferences();
        String referencesNav = references.concat("Nav");
        return "import " + Utilities.getVariableName(referencesNav) + " from '../" + mName + "" + references.toLowerCase() + "/" + referencesNav + ".js';\n";

    }

    private String makeComponents() {
        String crudTable = fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.TableView))
                .count() > 0 ? "CrudTable" : "";
        return crudTable;
    }

    private String dataNavLine(Field field) {
        String referenceVariableName = Utilities.getVariableName(field.getReferences()).concat("Nav");
        return referenceVariableName.concat(":").concat(referenceVariableName).concat(",\n");

    }

    private String dataNavLines() {
        String lines = "";
        lines = this.fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.TableView)
                || p.isSelect())
                .map(f -> dataNavLine(f)).reduce(lines, String::concat);
        return lines;
    }

    private String created() {

        String body = "";

        body = fields.stream()
                .filter((p) -> p.isReference())
                .filter((p) -> !p.isCollection())
                .map(fieldDAO -> "this.$store.dispatch(\"".concat(this.storePath(fieldDAO, "/")) + "get" + this.getReferencingName(fieldDAO) + "\");\n")
                .reduce(body, String::concat);
        return " created() {" + body + "},\n";

    }

    public String makeFormDataLine(Field field, String variable) {
        return variable + ".append(\"" + field.getVariableName() + "\", this." + objectNameVariable + "." + field.getVariableName() + ");\n";

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
    private String setImageFileName(Field field) {
        return " async set" + field.getFieldName() + "File(obj) {\n"
                + "      let " + field.getVariableName() + " = obj." + field.getVariableName() + ";\n"
                + "      if (" + field.getVariableName() + ") {\n"
                + "        this." + field.getVariableName() + "Url = \"data:image/png;base64,\" + " + field.getVariableName() + ";\n"
                + "        const getUrlExtension = (url) => {\n"
                + "        return url.split(/[#?]/)[0].split(\".\").pop().trim();\n"
                + "      };\n"
                + "      var imgExt = getUrlExtension(this." + field.getVariableName() + "Url);\n"
                + "\n"
                + "        const response = await fetch(this." + field.getVariableName() + "Url);\n"
                + "        const blob = await response.blob();\n"
                + "        console.log(\"Blob: \", blob);\n"
                + "        const file = new File([blob], \"logo.\" + imgExt, {\n"
                + "          type: blob.type,\n"
                + "        });\n"
                + "      this." + objectNameVariable + "." + field.getVariableName() + " = file;\n"
                + "\n"
                + "      } else {\n"
                + "        this." + field.getVariableName() + "Url = null;\n"
                + "      }\n"
                + "    },\n";
    }

    String callSetFile(Field fieldDAO) {
        return " funcs.createFileFromBytes(obj." + fieldDAO.getVariableName() + ").then(e=>{\n"
                + "        this." + objectNameVariable + "." + fieldDAO.getVariableName() + " =e;\n"
                + "\n"
                + "      }).catch(error=> console.log(error));\n";
    }

    private String makePrintLine(Field field) {

        return "data.push({ text: \"" + field.getCaption() + "\", propertyValue: this." + objectNameVariable + "." + field.getVariableName() + " });\n";
    }

    private String print() {

        String printLines = "";
        printLines = this.fields.stream()
                .filter(f -> !f.getDataType().equalsIgnoreCase("boolean"))
                .map(f -> this.makePrintLine(f))
                .reduce(printLines, String::concat);
        String printData = "let data = [];\n" + printLines + "\n";
        String mtdBody = printData + " let options = {\n"
                + "        data: data,\n"
                + "        startXPos: 10,\n"
                + "        startYPos: 25,\n"
                + "        lineBreak: 4,\n"
                + "        hSpace: 50,\n"
                + "        vSpace: 10,\n"
                + "        title: \"" + objectCaption + "\"\n"
                + "\n"
                + "      }\n"
                + "\n"
                + "      this.makePDFDocument(options);";

        return "print(){" + mtdBody + "},\n";
    }

    private String watch() {
        
        String referenceWatcher = "";
        referenceWatcher = this.fields
                .stream()
                .filter(f->this.forceReferences(f))
                .map(f->this.referenceWatcher(f))
                .distinct()
                .reduce(referenceWatcher, String::concat)
                .concat("\n");

        return "watch: {\n"
                + dialogDataWatchers()
                +referenceWatcher
                + "},\n";

    }
    
     private String computed() {
        
        String referenceComputed = "";
        referenceComputed = this.fields
                .stream()
                .filter(f->this.forceReferences(f))
                .map(f->this.referenceComputed(f))
                .distinct()
                .reduce(referenceComputed, String::concat)
                .concat("\n");

        return "computed: {\n"
                 +referenceComputed
                + "},\n";

    }

    private String methods() {
        String theBody = hasMuiltipart ? "getFormData()" : objectNameVariable;

        String onFileChangedMtds = "";
//        onFileChangedMtds = this.fields
//                .stream().filter(p -> p.getDataTypeImps().equalsIgnoreCase("Image"))
//                .map(f -> onFileChanged(f))
//                .reduce(onFileChangedMtds, String::concat);

        String setImageFileNameMtds = "";
        setImageFileNameMtds = this.fields
                .stream().filter(p -> p.getDataType().equalsIgnoreCase("Image"))
                .map(f -> setImageFileName(f))
                .reduce(setImageFileNameMtds, String::concat);

        String callSetFiles = "";

        callSetFiles = this.fields
                .stream().filter(p -> p.getDataType().equalsIgnoreCase("Image"))
                .map(f -> callSetFile(f))
                .reduce(callSetFiles, String::concat);

        String save = """
                      save() {
                            this.$store.dispatch("post", { path: this.path, body: this.""" + theBody + "});\n"
                + "    },\n";

        String update = """
                        update() {
                              this.$store.dispatch("put", { path: `${this.path}/${this.""" + objectNameVariable + ".id}`,body: this." + theBody + "});\n"
                + "    },\n";

        String updateDialg = "updateDialog() {\n"
                + "      var obj = this.$store.state.search.selectedData[0].propertyValue;\n"
                + "      this.setDialog(obj);\n"
                + "    },\n";

        String search = "async search() {\n"
                + "      var obj = this.$store.state.obj;\n"
                + "      this." + objectNameVariable + " = Object.assign({}, obj);\n"
                + "      this.setObjects(obj);\n"
                + "    },\n";

        String reset = "reset() {\n"
                + "      this." + objectNameVariable + ".clear();\n"
                + "    },\n";

        String setObject = "setObjects(obj){\nconsole.log(obj);\n" + callSetFiles + "},";

        String setDialog = "setDialog(obj) {\n"
                + "this." + this.objectNameVariable + " = Object.assign({}, obj);\n"
                + "this.setObjects(obj);\n"
                + "},\n";

        String done = "done() {\n"
                + "      this.$store.commit(\n"
                + "        \"crudtable/data\",\n"
                + "        Object.assign({}, this." + this.objectNameVariable + ")\n"
                + "      );\n"
                + "    },\n";

        String updateCrudTableDialog = "updateCrudTableDialog() {\n"
                + "      this.setDialog(this.$store.state.crudtable.data);\n"
                + "    },\n";

        String resetCrudTableDialog = "resetCrudTableDialog() {\n"
                + "      this.reset();\n"
                + "    },\n";

        return "methods: {" + save.concat(update).concat(updateDialg).concat(search).concat(reset).
                concat(setObject)
                .concat(setDialog)
                .concat(done)
                .concat(updateCrudTableDialog)
                .concat(resetCrudTableDialog)
                .concat(getFormDataMtd())
                .concat(onFileChangedMtds)
                .concat(setImageFileNameMtds)
                .concat(this.print())
                .concat(dialogMethods())
                .concat("}");

    }

    private String templete() {
        return "<template>\n"
                + "  <crud-form\n"
                + "    @save=\"save\"\n"
                + "    @update=\"update\"\n"
                + "    @search=\"search\"\n"
                + "    @updateDialog=\"updateDialog\"\n"
                + "    @reset=\"reset\"\n"
                + "    @done=\"done\"\n"
                + "    @updateCrudTableDialog=\"updateCrudTableDialog\"\n"
                + "    @resetCrudTableDialog=\"resetCrudTableDialog\"\n"
                + "    :path=\"path\"\n"
                + "    :maxWidth=\"maxWidth\"\n"
                + "    :showPrintPrompt=\"showPrintPrompt\""
                + "    @print=\"print\""
                + "  >\n"
                + "    <template slot=\"heading\">" + this.objectCaption + "</template>\n"
                + "\n"
                + "    <template slot=\"form-data\">\n"
                + this.makeControlColumns()
                + this.dialogs()
                + "    </template>\n"
                + "  </crud-form>\n"
                + "</template>\n";
    }

    private String script() {
        return "<script>\n"
                + makeImports()
                + "export default {\n"
                + "  components: { " + makeComponents() + " },\n"
                + "  name: \"" + objectName + "\",\n"
               
                + data()
                + created()
                + computed()
               
                + "\n"
                + watch()
                + methods()
                + "};\n"
                + "</script>";
    }

    public String create() {
        return this.templete().concat(this.script());
    }

}
