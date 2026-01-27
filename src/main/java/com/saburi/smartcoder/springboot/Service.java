/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.springboot;



import com.saburi.smartcoder.base.CodeGenerator;
import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.base.exceptions.KnownException;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;
import com.saburi.smartcoder.java.JavaClass;
import com.saburi.smartcoder.project.Project;
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;
import java.util.stream.Collectors;

import static com.saburi.smartcoder.utils.Utilities.makeMethod;


/**
 *
 * @author Hp
 */
public class Service extends SpringbootUtils{

    public Service(FileModel fileModel) {
        super(fileModel);
    }

    public String makeImports(Project project, Enums.ServiceTypes serviceTypes)  {
        
        String imp = "import " + commonProject.getBasePackage() + ".repositories.PagingAndSortingRepo;\n";
        imp += "import " + project.getBasePackage() + "." + objectName.concat(".dtos").toLowerCase() + "." + requestObject + ";\n"
                + "import org.springframework.beans.factory.annotation.Autowired;"
                + "import org.springframework.stereotype.Service;\n"
                + "import " + commonProject.getBasePackage() + ".services.".concat(Utilities.getParentService(serviceTypes)).concat(";\n"
                + "import " + commonProject.getBasePackage() + ".dtos.ResponseData;\n"
                + "import " + commonProject.getBasePackage() + ".utils.KnownException;\n"
                + "import java.util.List;");

        if (serviceTypes.equals(Enums.ServiceTypes.ID_Gen)) {
            if (idGenerator != null) {
                if (idGenerator.isReference()) {
                    imp += "import " + project.getBasePackage() + "." + idGenerator.getReferences().toLowerCase() + "." + idGenerator.getReferences() + ";\n";
                }
            }
        }

        if (fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique.name()) || p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).count() > 0) {
            imp += "import java.util.Objects;\n";
        }

        List<Field> collections = fields.stream().filter((p) -> p.isCollection())
                .distinct().collect(Collectors.toList());

        for (Field f : collections) {
            imp += "import " + project.getBasePackage() + "." + f.getReferences().toLowerCase() + "." + f.getReferences() + ";\n";
            imp += "import java.util.ArrayList;\n";
        }

      
           String ref ="";
           ref= fields.stream()
                .filter(f -> f.isForeignKey(forceReferences(f)))
                .map(f -> getPackageImport(f))
                .distinct()
                .reduce(ref, String::concat);
           imp+=ref;

        if (this.createModify()) {
            imp += "import " + commonProject.getBasePackage() + ".utils.SpringUtil;\n";
        }

        return imp;
    }

    private String getPackageImport(Field f) {
        if (forceReferences(f)) {
            String references = f.getReferences();

            String imp = "import " + project.getBasePackage() + "." + references.toLowerCase()
                    .concat(".")
                    .concat(references);
            return imp.concat(";\n").concat(imp.concat("Repo;\n"));
        }

        return "";

    }

    private boolean createModify() {
        return this.fields
                .parallelStream()
                .filter((p) -> referencesLookupExt(project, p) || referencesAccountExt(project, p))
                .count() > 0;
    }

    private String makeClassFields(Enums.ServiceTypes serviceTypes) {
        String resourceID = serviceTypes.equals(Enums.ServiceTypes.ID_Gen) ? "private static final int RESOURCE_ID = 1;\n" : "";

        String properties = resourceID
                + "    private static final String ENTITY_CAPTION = \"" + this.fileModel.getObjectCaption() + "\";\n"
                + "    @Autowired\n"
                + "    private " + objectName + "Repo " + objectVariableName + "Repo;\n";

        String prop = "";
        prop = this.fields.stream()
                .filter(f -> f.isForeignKey(forceReferences(f)))
                .map(f -> this.makeRepoProps(f, forceReferences(f)))
                .distinct()
                .reduce(prop, String::concat);
        properties += prop;

        if (this.createModify()) {
            properties += "    @Autowired\n"
                    + "    private SpringUtil springUtil;\n";
        }
        return properties;
    }

    private String getReferenceRepoVariableName(Field f) {
        return Utilities.getVariableName(f.getReferences()).concat("Repo");
    }

    private String makeRepoProps(Field f, boolean forceReference) {
        if (f.isForeignKey(forceReference)) {
            return "@Autowired\nprivate " + f.getReferences() + "Repo  " + getReferenceRepoVariableName(f) + ";\n";
        }
        return "";
    }

    private String refSetVariable(Field field) {
        if (!field.isForeignKey(forceReferences(field))) {
            return "";
        }
        String ref = field.getVariableName();
        String refReq = new FieldHelper(field).getReqFieldName(forceReferences(field));
        String refReqLine = "req.get" + refReq + "()";

        return field.getReferences() + " " + ref + " = " + getReferenceRepoVariableName(field) + "\n"
                + "                .findById(" + refReqLine + ")\n"
                + "                .orElseThrow(()->new KnownException(\"No " + field.getCaption() + " found with id: \"+" + refReqLine + "));";
    }

//     private String makeRepo(FieldDAO f, boolean forceReference) {
//        if (f.isForeignKey(forceReference)) {
//            return f.getFieldName() + "Repo = " + f.getVariableName() + "Repo;";
//        }
//        return "";
//    }


    public String getReferenceDisplayText(Field field, boolean forceReferences) {
        if (field.isReference() && forceReferences && !field.isReference()) {
            return ".getDisplayKey()";
        }
        return "";
    }
    private String validateMethod(String firstChar) {

        List<Field> uniqueGroups = fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());

        List<Field> unique = fields.stream().filter((p) -> p.getKey()
                .equalsIgnoreCase(Enums.keys.Unique.name())).collect(Collectors.toList());
        String validateBody = "";
        if (!uniqueGroups.isEmpty()) {
            String message = "\"The record with ";
            String uniqueGroupValidate = "";
            int last = uniqueGroups.size() - 1;
            String methodName = "findBy";
            String methodParams = "";
            for (int i = 0; i <= last; i++) {
                Field field = uniqueGroups.get(i);
                FieldHelper fdao =  new FieldHelper(field);
                methodParams += objectVariableName.concat(".") + fdao.getCall();
                methodName += "" + field.getFieldName();

                if (i != last) {
                    methodName += "And";
                    methodParams += ",";
                }

                message += "" + field.getCaption() + ": \"+" + objectVariableName + "." + fdao.getCall() +getReferenceDisplayText(field,this.forceReferences(field)) + "+";

                if (i != last) {
                    message += " \" and ";
                }
            }
            message += "\" already exists\"";
            uniqueGroupValidate += "if (" + repoVariableName + "." + methodName + "(" + methodParams + ")\n"
                    + "                .stream().filter(" + firstChar + " -> !Objects.equals(" + firstChar + ".getId(), " + objectVariableName + ".getId())).count() > 0) {\n"
                    + "            throw new KnownException(" + message + ");\n"
                    + "        }";

            validateBody += uniqueGroupValidate;
        }

        String uniqueValidate = "";

        for (Field field : unique) {
            FieldHelper fieldHelper =  new FieldHelper(field);
            String getCall = fieldHelper.getCall();
            uniqueValidate += "if (" + repoVariableName + ".findBy" + field.getFieldName() + "(" + objectVariableName + "." + getCall + ")\n"
                    + "                .stream().filter(" + firstChar + " -> !Objects.equals(" + firstChar + ".getId(), " + objectVariableName + ".getId())).count() > 0) {\n"
                    + "            throw new KnownException(\"A record with name: \" + " + objectVariableName + "." + getCall + "+ \" already exists\");\n"
                    + "        }";
        }

        validateBody += uniqueValidate;
        validateBody += "return super.isValid(" + objectVariableName + ");";
        String params = objectName.concat(" ").concat(objectVariableName);
        String validateMethod = Utilities.makeThrowsMethod("@Override\npublic", "boolean", "isValid", params, validateBody);
        return validateMethod;
    }

    private String predictModifyMtdName(Field fiedDAO) {
        String lookupName = fiedDAO.getFieldName();
        int length = lookupName.length();
        if (lookupName.substring(length - 2, length).equalsIgnoreCase("id")) {
            lookupName = lookupName.substring(0, length - 2);
        }
        return lookupName;
    }

    public boolean referencesLookup(Field field) {
        return field.getReferences().equalsIgnoreCase("LookupData");
    }

    public boolean referencesLookupExt(Project project, Field field) {
        return this.referencesLookup(field) && !field.getProjectName().equalsIgnoreCase(project.getProjectName());
    }

    public boolean referencesAccount(Field field) {
        return field.getReferences().equalsIgnoreCase("Account");
    }

    public boolean referencesAccountExt(Project project, Field field) {
        return this.referencesAccount(field) && !field.getProjectName().equalsIgnoreCase(project.getProjectName());
    }

    private String modifyLine(Field field, String ov) {
        String getCall = new FieldHelper(field).getCall();
        if (referencesLookupExt(this.project, field)) {
            return ov + ".set" + this.predictModifyMtdName(field) + "(springUtil.getLookupDataName(" + ov + "." + getCall + ", \"" + field.getCaption() + "\"));\n";
        } else if (referencesAccountExt(this.project, field)) {
            return ov + ".set" + this.predictModifyMtdName(field) + "(springUtil.getAccountName(" + ov + "." + getCall + "));\n";
        }
        return "";
    }

    private String makeModifyMethod() {
        String modify = "";
        String ov = objectVariableName.substring(0, 1);
        modify = fields.stream()
                .filter((p) -> referencesLookupExt(this.project, p)
                || referencesAccountExt(this.project, p))
                .map(fieldDAO -> modifyLine(fieldDAO, ov)).reduce(modify, String::concat);

        if (!modify.isBlank()) {
            return "@Override\n"
                    + "    protected " + objectName + " modify(" + objectName + " " + ov + ") throws Exception {\n"
                    + modify
                    + "return " + ov + ";\n"
                    + "}\n";

        }

        return modify;
    }

    private String setLine(Field field) {
        String getCall = new FieldHelper(field).getCall();
        if (field.isCollection()) {

            return "  List<" + field.getReferences() + "> " + field.getVariableName() + " = new ArrayList<>();\n"
                    + "        req." + getCall+ ".forEach(t->{\n"
                    + "        t.set" + objectName + "(e);\n"
                    + "        " + field.getVariableName() + ".add(t);\n"
                    + "        });\n"
                    + "        e.set" + field.getFieldName() + "(" + field.getVariableName() + ");";
        }

        String body = this.refSetVariable(field);
        String getbytes = field.getDataType().equalsIgnoreCase("Image") ? ".getBytes()" : "";
        String call = field.isForeignKey(forceReferences(field))
                ? field.getVariableName() : "req." + getCall + getbytes;
        body += "e.set" + field.getFieldName() + "(" + call + ");\n";
        return body;
    }

    

    private String createExpose(Field field) {
        boolean referencesIn = forceReferences(field);
        String id = referencesIn ? "Id" : "";

        String param = new FieldHelper(field).getUsableDataType(false).concat(" ")
                .concat(referencesIn ? "id" : field.getVariableName());

        return "public List<? extends ResponseData> getBy"
                .concat(field.getFieldName()).concat(id).concat("(")
                .concat(param)
                .concat("){\n")
                .concat("return " + repoVariableName + ".findBy" + field.getFieldName().concat(id) + "(" + (referencesIn ? "id" : field.getVariableName()) + ");")
                .concat("}\n");
    }

    private String methods(Enums.ServiceTypes serviceTypes) {

        String params = "";
        String firstChar = this.objectName.substring(0, 1).toLowerCase();

        String mtds = makeMethod("@Override\npublic", "String", "getEntityCaption", "", "return ENTITY_CAPTION;");
        mtds += makeMethod("@Override\npublic", "PagingAndSortingRepo<" + this.objectName + ", " + idDataType + ">", "getRepository", "", "return " + objectVariableName.concat("Repo") + ";");

        mtds += makeMethod("@Override\npublic", "List<? extends ResponseData>", 
                "getMiniData", "", "return " + repoVariableName + ".findAllBy();");
        if (serviceTypes.equals(Enums.ServiceTypes.ID_Gen)) {
            mtds += makeMethod("@Override\npublic", "int", "getResourceID", "", "return RESOURCE_ID;");
            String cast = "";

            if (idGenerator != null) {
                params = new FieldHelper(idGenerator).getDeclaration(true, false);

                cast = idGenerator.isReference() ? idGenerator.getReferences() : idGenerator.getDataType();
                cast = "(" + cast + ")object";
            }

            mtds += """
                    @Override
                        protected int getNextIdHelper(Object object) {
                            return """.concat(" ") + (repoVariableName) + ".getMaxIdHelper(" + cast + ").orElse(0) + 1;\n"
                    + "    }\n";

            mtds += " @Override\n"
                    + "    protected " + objectName + " modifyToSave(" + objectName + " " + objectVariableName + ") throws  Exception {\n"
                    + "       \n"
                    + "        if (" + objectVariableName + ".getId() == null) {\n"
                    + "            " + objectVariableName + ".setIdHelper(this.getNextIdHelper(" + objectVariableName + "));\n"
                    + "            " + objectVariableName + ".setId(this.getNextID(" + objectVariableName + "));\n"
                    + "        }\n"
                    + "        return " + objectVariableName + ";\n"
                    + "    }";
        }
        mtds += makeModifyMethod();

        String exm = "";

        exm = fields.stream().filter(p -> p.isExpose()).map(fieldDAO -> createExpose(fieldDAO)).reduce(exm, String::concat);
        mtds += exm;

        mtds += validateMethod(firstChar);

        String requestVaiableName = Utilities.getVariableName(requestObject);

        String createMethodBody = "return new " + objectName + "();";

        mtds += makeMethod("@Override\npublic", objectName, "instantiate", "", createMethodBody);

        String setEntityBody = "";
//        setEntityBody += this.fields.stream()
//                .filter(f -> f.isForeignKey(forceReferences(f)))
//                .map(field -> this.refSetVariable(field))
//                .reduce(setEntityBody, String::concat);

        setEntityBody += this.fields.stream().map(field -> this.setLine(field))
                .reduce(setEntityBody, String::concat);
        setEntityBody += "return e;\n";
        mtds += "@Override\n"
                + "    public " + objectName + " setEntity(" + objectName + " e, " + requestObject + " req) throws Exception{\n"
                + "" + setEntityBody + "\n}\n";

        return mtds;

    }

    public String makeClass(Project project, Enums.ServiceTypes serviceTypes) throws Exception  {
        CodeGenerator.validate(fields, project);
        String className = objectName + "" + Enums.SpringBootFiles.Service.name();

        String methods = methods(serviceTypes);

        String packageName = project.getBasePackage() + "." + objectName.toLowerCase();
        JavaClass javaClass = new JavaClass(packageName, className, this.makeImports(project, serviceTypes), this.makeClassFields(serviceTypes) + methods);
        return javaClass.makeClass(Utilities.getParentService(serviceTypes) + "<" + objectName + "," + requestObject + ", " + idDataType + ">", "@Service");
    }

    @Override
    protected boolean isValid() {
        CodeGenerator.validate(fields, project);
        if(Utilities.isNullOrEmpty(entityType.name())) throw  new KnownException("Entity Type is required!");
        if(Utilities.isNullOrEmpty(fileModel.getServiceType().name())) throw  new KnownException("Service Type is required!");
        return super.isValid(); 
    }
    
    

    @Override
    protected String getFileName() {
        return this.service;
    }


    @Override
    protected String create() throws Exception{
       return this.makeClass(this.project, this.fileModel.getServiceType());
    }

}
