/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
public class Controller extends Vue3Utils {

    public Controller(FileModel fileModel) {
        super(fileModel);

    }

    private String imports() {
        return "import rootController from \"@/root/RootController\";\n"
                + "import " + this.objectVariableName + "Model from \"./" + this.objectName + "Model\";\n"
                + makeImports();
    }

    private boolean includeNavVariables(Field fd) {
        return fd.makeTable() || fd.isSelect();
    }

    private String importLine(Field fd) {
        if (!fd.isReference()) {
            return "";
        }

        String references = fd.getReferences();

        String referencesNav = references.concat("Nav");
        String referencesController = references.concat("Controller");
        String imp = "";
        if (includeNavVariables(fd)) {
            imp += String.format("import %s from \"%s\"\n", Utilities.getVariableName(referencesNav),
                    this.importPath(fd, referencesNav.concat(".js")));
        }

        if (fd.makeTable()) {
            imp += String.format("import %s from \"%s\"\n", Utilities.getVariableName(referencesController),
                    this.importPath(fd, referencesController.concat(".js")));
        }
        imp += String.format("import {%s} from \"%s\"\n", defineStoreFunction(fd),
                this.importPath(fd, this.getStoreName(fd).concat(".js")));
        return imp;
    }

    private String makeImports() {

        String imports = "";
//        imports += fields.stream()
//                .filter(p -> p.getControlType().equals(UIControls.TableView))
//                .count() > 0 ? "import CrudTable from \"../../components/CrudTable.vue\";\n" : "";

        imports += fields.stream()
                .filter(p -> p.isReference())
                .count() > 0 ? "import { onMounted } from \"vue\";\n" : "";

        imports += fields.stream()
                .filter(p -> p.getControlType().equals(Enums.UIControls.ImageView))
                .count() > 0 ? "import funcs from '../../utils/funcs'\n" : "";

        imports = fields.stream()
                .filter(p -> p.isReference())
                .map(f -> importLine(f))
                .distinct()
                .reduce(imports, String::concat);
        return imports;
    }

    private String makeStoreLine(Field field) {
        if (!field.isReference()) {
            return "";
        }

        String storeVariableName = getStoreVariableName(field);

        String assignStore = String.format("controller.%s = %s;", storeVariableName, storeVariableName);

        return String.format("const %s = %s();\n%s\n", storeVariableName, super.defineStoreFunction(field), assignStore);
    }

    private String assignControllerVariableName(Field fd) {
        if (!fd.makeTable()) {
            return "";
        }
        String reference = fd.getReferences();

        String referenceController = reference.concat("Controller");
        String setDataVariableName = String.format("set%sData", reference);
        String setData = String.format("const {setData: %s}=%s();\n", setDataVariableName, Utilities.getVariableName(referenceController));
        String assignSetData = String.format("controller.%s=%s;\n", setDataVariableName, setDataVariableName);
        return setData.concat(assignSetData);
    }

    private String assignControllerVariableNames() {
        String lines = "";
        lines = this.fields.stream()
                .filter(f -> f.makeTable())
                .map(f -> this.assignControllerVariableName(f))
                .distinct()
                .reduce(lines, String::concat);
        return lines;
    }

    private String assignVariableName(Field fd) {
        if (!includeNavVariables(fd)) {
            return "";
        }
        String reference = fd.getReferences();
        String navVariableName = Utilities.getVariableName(reference).concat("Nav");
        return String.format("controller.%s=%s;\n", navVariableName, navVariableName);

    }

    public String assignVariableNames() {
        String lines = "";
        lines = this.fields.stream()
                .filter(f -> includeNavVariables(f))
                .map(f -> this.assignVariableName(f))
                .distinct()
                .reduce(lines, String::concat);
        return lines;
    }

    public String makeDefineLines() {
        String lines = "";
        lines = this.fields.stream()
                .filter(f -> f.isReference())
                .map(f -> this.makeStoreLine(f))
                .distinct()
                .reduce(lines, String::concat);
        return lines;
    }

    private String makeDialogOk(Field field) {
        if (!field.isSelect()) {
            return "";
        }
        String variableName= this.getVariableName(field);
        String okMethodName =dialogOkMtdName(field);
        String valuePath ="controller.model.propertyValue."+ variableName ;
        String dataDotId =  " data.id";
        String setValue = field.isCollection()?String.format("%s.push(%s)", valuePath, dataDotId)
                :String.format("%s=%s", valuePath, dataDotId);
        return "const " + okMethodName + " = (data) => {\n"
                + "    if (data) {\n"
                + "     \n"
                + "      " + setValue + ";\n"
                + "\n"
                + "    }\n"
                + "  };\n"
                + "controller." + okMethodName + " = " + okMethodName + ";\n";
    }

    public String makeDiaogOks() {
        String dialogs = "";
        dialogs = this.fields.stream()
                .filter(f -> f.isSelect())
                .map(f -> this.makeDialogOk(f))
                .distinct()
                .reduce(dialogs, String::concat);
        return dialogs;
    }

    private String onMunted() {
        String lines = "";
        lines = this.fields.stream()
                .filter(f -> f.isReference())
                .map(f -> this.callStoreAction(f))
                .distinct()
                .reduce(lines, String::concat);
        return lines.equals("") ? "" : String.format("onMounted(()=>{\n%s\n})\n", lines);
    }

    private String makeFunction() {
        return "export default function " + objectVariableName + "Controller(){\n"
                + "\n"
                + "  const controller = rootController(" + objectVariableName + "Model);\n"
                + makeDiaogOks()
                + makeDefineLines()
                + assignControllerVariableNames()
                + assignVariableNames()
                + onMunted()
                + "  return controller;\n"
                + "\n"
                + "}\n";
    }

    @Override
    public String create() {
        return this.imports().concat(this.makeFunction());
    }

    @Override
    protected String getFileName() {
        return this.objectName.concat("Controller");
    }

    @Override
    protected String getFileExtension() {
        return "js";
    }

}
