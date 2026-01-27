/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.springboot;


import com.saburi.smartcoder.base.CodeGenerator;
import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;
import com.saburi.smartcoder.java.JavaClass;
import com.saburi.smartcoder.project.Project;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

/**
 *
 * @author Hp
 */
public class WebController extends SpringbootUtils {

    private final String controllerType;
    private Enums.ServiceTypes serviceTypes;
    private final String baseService;
    private final boolean hasMultipart;

    public WebController(FileModel fileModel){
        super(fileModel);
        this.serviceTypes = fileModel.getServiceType();
        this.hasMultipart = Utilities.hasMultipart(fields);
        this.controllerType = Utilities.getControllerType(serviceTypes, hasMultipart);
        this.baseService = Utilities.getParentService(serviceTypes);
    }

    public String makeImports(Project project) {

        String imp = "import " + project.getBasePackage() + "." + objectName.toLowerCase() + ".dtos." + requestObject + ";\n"
                + "import " + commonProject.getBasePackage() + ".controllers." + controllerType + ";\n"
                + "import " + commonProject.getBasePackage() + ".services." + baseService + ";\n"
                + "import org.springframework.beans.factory.annotation.Autowired;\n"
                + "import org.springframework.web.bind.annotation.RequestMapping;\n"
                + "import org.springframework.web.bind.annotation.RestController;\n";

        if (fields.parallelStream().filter(p -> p.isExpose()).count() > 0) {
            imp += "import org.springframework.web.bind.annotation.GetMapping;\n"
                    + "import org.springframework.web.bind.annotation.PathVariable;\n"
                    + "import " + commonProject.getBasePackage() + ".dtos.ResponseData;\n"
                    + "import java.util.List;\n";

        }

        return imp;
    }

    private String makeAnnotedFields() {

        return """
               @Autowired
                   private """.concat(" ") + service + " " + serviceVaribaleName + ";";

    }

    public String makeProperties(Enums.EntityTypes entityTypes) {
        String dataType = primaryKeyFied == null ? Utilities.getIdWrapperDataType(entityTypes) : primaryKeyFied.getDataType();
        return "@Override\n"
                + "    protected " + baseService + "<" + objectName + ", " + requestObject + ", " + dataType + "> getBaseService() {\n"
                + "        return " + serviceVaribaleName + ";\n"
                + "    }";
    }

    private String createExpose(Field field) {
        boolean referencesIn = forceReferences(field);
        String id = referencesIn ? "Id" : "";
        String param = new FieldHelper(field).getUsableDataType(false).concat(" ")
                .concat(referencesIn ? "id" : field.getVariableName());
        return "@GetMapping(\"/" + field.getFieldName().toLowerCase() + "/{" + (referencesIn ? "id" : field.getVariableName()) + "}\")\npublic List<? extends ResponseData> getBy"
                .concat(field.getFieldName()).concat(id).concat("(")
                .concat("@PathVariable ")
                .concat(param)
                .concat("){\n")
                .concat("return " + serviceVaribaleName + ".getBy" + field.getFieldName().concat(id) + "(" + (referencesIn ? "id" : field.getVariableName()) + ");")
                .concat("}\n");
    }

    public String makeClass(Project project, Enums.EntityTypes entityTypes) {

        String className = objectName + "" + Enums.SpringBootFiles.Controller.name();

        String dataType = primaryKeyFied == null ? Utilities.getIdWrapperDataType(entityTypes) : primaryKeyFied.getDataType();
        String annotation = "@RestController\n"
                + "@RequestMapping(\"" + Utilities.toPlural(objectName).toLowerCase() + "\")";

        String exm = "";

        exm = fields.stream().filter(p -> p.isExpose()).map(fieldDAO -> createExpose(fieldDAO)).reduce(exm, String::concat);

        String packageName = project.getBasePackage() + "." + objectName.toLowerCase();
        JavaClass javaClass = new JavaClass(packageName, className, this.makeImports(project), makeAnnotedFields() + makeProperties(entityTypes) + exm);
        return javaClass.makeClass(controllerType + "<" + objectName + ", " + requestObject + ", " + dataType + ">", annotation);
    }

    @Override
    protected boolean isValid() {
        CodeGenerator.validate(fields, project);
        return super.isValid();
    }

    @Override
    protected String getFileName() {
        return controller;
    }

    @Override
    protected String create() throws Exception {
        return makeClass(project, entityType);
    }
}
