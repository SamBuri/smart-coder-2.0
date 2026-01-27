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

import java.util.List;
import java.util.stream.Collectors;

import static com.saburi.smartcoder.utils.Utilities.makeAbstractMethod;
import static com.saburi.smartcoder.utils.Utilities.makeAnnotedAbstractMethod;


/**
 *
 * @author Hp
 */
public class Repository extends SpringbootUtils {


    public Repository(FileModel fileModel) {
        super(fileModel);
    }

   

    public String makeImports(Project project, Enums.EntityTypes entityTypes) throws Exception {
       
        String imp = "import " + commonProject.getBasePackage() + ".repositories.PagingAndSortingRepo;\n";
        imp += "import " + project.getBasePackage() + ".".concat((objectName).toLowerCase()).concat(".dtos.") + objectName.concat(Enums.SpringBootFiles.Mini.name()).concat(";\n");
        imp += "import java.util.List;\n";

        if (entityTypes.equals(Enums.EntityTypes.Auto_ID_Gen)) {
            imp += "import java.util.Optional;".concat("\n")
                    .concat("import org.springframework.data.jpa.repository.Query;").concat("\n");
        }
//        List<String> references 
        imp = fields.stream().filter(f -> (this.forceReferences(f)
                && (f.getKey().equalsIgnoreCase(Enums.keys.Unique_Group.name())
                || f.getKey().equalsIgnoreCase(Enums.keys.Unique.name())
                || f.getSaburiKey().equalsIgnoreCase(Enums.Saburikeys.ID_Generator.name())))
        )
                .map(fd -> fd.getReferences())
                .distinct()
                .map(r -> "import " + project.getBasePackage()
                + "." + r.toLowerCase() + "." + r + ";\n").reduce(imp, String::concat);
//                .collect(Collectors.toList());
//
//        imp = references.stream().map(reference -> "import " + project.getBasePackage()
//                + "." + reference.toLowerCase() + "." + reference + ";\n").reduce(imp, String::concat);
        return imp;
    }

//    public String makeProperties() {
//        String properties = "";
//        properties = fields.stream().map(field -> field.makeProperties(forceReferences(field))).reduce(properties, String::concat);
//        return properties;
//    }

    public String methods(Enums.EntityTypes entityTypes) {

        String mtds = "";
        String dotChar = "e";

        if (entityTypes.equals(Enums.EntityTypes.Auto_ID_Gen)) {
            String querry = "SELECT MAX(idHelper) from " + objectName + " " + dotChar + " ";
            String params = "";
            Field idGenerator = Utilities.getIDGenerator(fields);
            if (idGenerator != null) {
                params = new FieldHelper(idGenerator).getDeclaration(forceReferences(idGenerator), false);
                querry += "where " + dotChar + "." + idGenerator.getVariableName() + "=?1";
            }
            mtds += makeAnnotedAbstractMethod("\n @Query(\"" + querry + "\")", "public", "Optional<Integer>", "getMaxIdHelper", params);

        }
        mtds = fields.stream().filter((f -> f.getKey().equalsIgnoreCase(Enums.keys.Unique.name())))
                .map(field ->  makeAbstractMethod("\npublic", "List<" + objectName.concat(Enums.SpringBootFiles.Mini.name()) + ">", "findBy" + field.getFieldName(), new FieldHelper(field).getDeclaration(true, false)))
                .reduce(mtds, String::concat);

        List<Field> uniqueGroups = fields.stream().filter(f -> f.getKey()
                .equalsIgnoreCase(Enums.keys.Unique_Group.name())).collect(Collectors.toList());

        if (!uniqueGroups.isEmpty()) {
            String methodName = "findBy";
            String methodParams = "";
            int last = uniqueGroups.size() - 1;
            for (int i = 0; i <= last; i++) {
                Field fdao = uniqueGroups.get(i);
                methodParams += new FieldHelper(fdao).getDeclaration(forceReferences(fdao), false);
                methodName += "" + fdao.getFieldName();

                if (i != last) {
                    methodName += "And";
                    methodParams += ",";
                }
            }
            mtds += makeAbstractMethod("public", "List<" + objectName.concat(Enums.SpringBootFiles.Mini.name()) + ">", methodName, methodParams);
        }

        String exm = "";

        exm = fields.stream().filter(p -> p.isExpose()).map(fieldDAO -> createExpose(fieldDAO)).reduce(exm, String::concat);
        mtds += exm;
        return mtds;

    }

    private String createExpose(Field field) {
        FieldHelper fieldHelper =  new FieldHelper(field);
        boolean referencesIn = this.forceReferences(field); //fieldHelper.referencesIN(project);
        String id = referencesIn ? "Id" : "";
        String param= fieldHelper.getUsableDataType(this.forceReferences(field)).concat(" ")
                        .concat(referencesIn ? "id" : field.getVariableName());
        
        return "public List<" + objectName.concat(Enums.SpringBootFiles.Mini.name()) + "> findBy"
                .concat(field.getFieldName()).concat(id).concat("(")
                .concat(param).concat(");\n");
    }

    @Override
    protected boolean isValid() {
          CodeGenerator.validate(fields, project);
        return super.isValid(); 
    }
    
    

    public String makeClass(Project project, Enums.EntityTypes entityTypes) throws Exception {
      
        String className = objectName + "" + Enums.SpringBootFiles.Repo.name();

        String methods = methods(entityTypes).concat("\npublic List<" + objectName.concat(Enums.SpringBootFiles.Mini.name()) + "> findAllBy();\n");

        String dataType = primaryKeyFied == null ? Utilities.getIdWrapperDataType(entityTypes) : primaryKeyFied.getDataType();

        String packageName = project.getBasePackage() + "." + objectName.toLowerCase();
        JavaClass javaClass = new JavaClass(packageName, className, this.makeImports(project, entityTypes), methods);
        return javaClass.makeInterfaceExt("PagingAndSortingRepo<" + objectName + ", " + dataType + ">");
    }

    @Override
    protected String getFileName() {
         return repo;
    }

    @Override
    protected String create() throws Exception {
        return makeClass(project, entityType);
    }

}
