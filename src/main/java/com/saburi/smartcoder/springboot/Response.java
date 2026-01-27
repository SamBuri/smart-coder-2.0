/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saburi.smartcoder.springboot;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.field.FieldHelper;

/**
 *
 * @author samburiima
 */
public class Response extends DtoClass{

    public Response(FileModel fileModel) {
        super(fileModel);
    }

   

    private String makeImport(Field field) {
        String enumPackage = (forceReferences(field)) ? project.getBasePackage() : commonProject.getBasePackage();
        return "import " + enumPackage + ".enums." + field.getReferences().concat(";\n");
    }

    private String makeImports() {
        String imp= "import "+this.commonProject.getBasePackage()+".dtos.ResponseData;\n";
        imp = this.fields.stream()
                .filter(Field::isEnumerated)
                .map(this::makeImport)
                .distinct()
                .reduce(imp, String::concat);
        return imp;
    }

  

    private String makeField(Field field) {
        if (forceReferences(field) && !field.isEnumerated()) {
            return "String " + field.getVariableName().concat("Id, ".concat("String ").concat(field.getVariableName()));
        }
        return new FieldHelper(field).getDeclaration(false, false);
    }
    
    

    private String makeRecord() {

        String construtorLine = "";
        for (int i = 0; i < fields.size(); i++) {
            Field field = this.fields.get(i);

            if (i == 0) {
                construtorLine += makeField(field);
            } else {
                construtorLine += "," + makeField(field);
            }

        }

        return "public record " + this.objectName + "Response (\n" + construtorLine + ") implements ResponseData{\n"
                + "\n"
                + "}";
    }

    @Override
    public String create() {
        return "package ".concat(project.getBasePackage() + "." + objectName.toLowerCase().concat(".dtos;\n"))
                .concat(this.makeImports())
                .concat(this.makeRecord());
    }

    @Override
    protected String getFileName() {
       return objectName.concat("Response");
    }

}
