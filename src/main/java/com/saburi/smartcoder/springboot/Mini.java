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
import com.saburi.smartcoder.utils.Enums;
import com.saburi.smartcoder.utils.Utilities;

import java.util.List;

/**
 *
 * @author Hp
 */
public class Mini extends DtoClass {

    private final boolean isPKNull;

    public Mini(FileModel fileModel) {
        super(fileModel);

        isPKNull = this.primaryKeyFied == null;

    }

    @Override
    protected List<String> getImports(Field field) throws Exception {
        FieldHelper fieldHelper = new FieldHelper(field);
        return List.of(referecesImports(field),
                fieldHelper.getDataTypeImps(),
                fieldHelper.getGenericDataTypeImps());
    }

    private String makeImports() throws Exception {

        String imp = "";
//        imp += Utilities.makeResponseImport(project);

//        List<String> imports = new ArrayList();
//        for (FieldDAO t : this.fields) {
//            t.miniImports(project).forEach(i -> addIfNotExists(imports, i));
//
//        }
//        for (String impo : imports) {
//            imp += impo + ";\n";
//        }
        imp += super.getImports();
        return imp;
    }

    private String makeAFields() {
        StringBuilder annotedFields = new StringBuilder(isPKNull ? idDataType.concat(" ").concat("get").concat("Id();").concat("\n") : "");

        for (Field field : this.fields) {
            FieldHelper fieldHelper = new FieldHelper(field);
            annotedFields.append(fieldHelper.getUsableDataType(this.forceReferences(field))).append(fieldHelper.getCall().concat(";").concat("\n"));

        }
        return annotedFields.toString();
    }

    public String makeClass() throws Exception {

        String className = objectName + "" + Enums.SpringBootFiles.Mini.name();

        String packageName = project.getBasePackage() + "." + objectName.toLowerCase().concat(".dtos");
        JavaClass javaClass = new JavaClass(packageName, className, this.makeImports(), this.makeAFields());
        return javaClass.makeInterfaceExt(Utilities.RESPONSE_INERFACE);
    }

    @Override
    protected boolean isValid() {
        CodeGenerator.validate(fields, project);
        return super.isValid();
    }

    @Override
    protected String getFileName() {
        return mini;
    }

    @Override
    protected String create() throws Exception {
        return this.makeClass();
    }

}
