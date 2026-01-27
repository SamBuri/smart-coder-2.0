package com.saburi.smartcoder.generator;

import com.saburi.smartcoder.ProjectTypeRegistry;
import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.base.ProjectFile;
import com.saburi.smartcoder.base.ResponseObj;
import com.saburi.smartcoder.base.exceptions.KnownException;
import com.saburi.smartcoder.project.Project;
import com.saburi.smartcoder.project.ProjectRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneratorService {

private final ProjectRepo projectRepo;


    public ResponseObj<GeneratorResponse> generateCode(GeneratorRequest generatorRequest) throws Exception {


            Project project = projectRepo.findById(generatorRequest.getProjectId())
                    .orElseThrow(()->new KnownException("Project not found with id " + generatorRequest.getProjectId()));

            FileModel fileModel = FileModel.builder()
                    .projectRepo(projectRepo)
                    .objectName(generatorRequest.getObjectName())
                    .objectCaption(generatorRequest.getObjectCaption())
                    .entityType(generatorRequest.getEntityType())
                    .serviceType(generatorRequest.getServiceType())
                    .entityType(generatorRequest.getEntityType())
                    .fields(generatorRequest.getFields())
                    .moduleName(generatorRequest.getModuleName())
                    .projectType(project.getProjectType())
                    .project(project)
                    .saveToProject(generatorRequest.isSaveToProject())
                    .openFile(generatorRequest.isOpenFile())
                    .outputFolder(generatorRequest.getOutputDirectory())
                    .build();

            List<String> items = generatorRequest.getFiles();


            Map<String, ProjectFile> map  = ProjectTypeRegistry.register(fileModel);
//             Map<String, ProjectFile> map=  projectFilesMap.get(projectType);
            GeneratorResponse.GeneratorResponseBuilder generatorResponseBuilder = GeneratorResponse.builder();
            if(map==null) throw new KnownException("Project Type not registered in the registry");
            for (Object item : items) {

                ProjectFile pj = map.get(item.toString());
                if(pj!=null) generatorResponseBuilder.fileResponse(pj.generate());



            }

         return  ResponseObj.<GeneratorResponse>builder()
                 .data(generatorResponseBuilder.build())
                 .success(true)
                 .message("Operation successful")
                 .build();



    }

}
