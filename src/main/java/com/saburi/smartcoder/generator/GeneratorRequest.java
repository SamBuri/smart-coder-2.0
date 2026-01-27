package com.saburi.smartcoder.generator;

import com.saburi.smartcoder.field.Field;
import com.saburi.smartcoder.utils.Enums;
//import jakarta.validation.constraints.NotNull;
import com.saburi.smartcoder.utils.Utilities;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class GeneratorRequest {
    private int projectId;
//    @NotNull(message = "objectName cannot be bull")
    private String objectName;
    private String objectCaption;
    private String outputDirectory;
    private Enums.EntityTypes entityType;
    private Enums.ServiceTypes serviceType;
    private String moduleName;  //module name for frontend projects
    @Builder.Default
    private List<Field> fields = new ArrayList<>();
    @Builder.Default
    private List<String> files = new ArrayList<>();
    private boolean saveToProject;
    private boolean openFile;

    public String getObjectCaption() {
          return objectCaption != null ? objectCaption : Utilities.getCaption(this.objectName);
    }
}
