package com.saburi.smartcorder.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/enums/")
@Slf4j
public class EnumController {

    @GetMapping("projecttypes")
    public Enums.ProjectTypes [] getProjectTypes(){
        log.info("Request actually reaches here {}");
        return Enums.ProjectTypes.values();
    }

    @GetMapping("springbootfiles")
    public Enums.SpringBootFiles [] getSpringBootFiles(){
        return Enums.SpringBootFiles.values();
    }

    @GetMapping("mappings")
    public Enums.RelationMapping [] getRelationMappings(){
        return Enums.RelationMapping.values();
    }

    @GetMapping("entitytypes")
    public Enums.EntityTypes [] getEntityTypes(){
        return Enums.EntityTypes.values();
    }

    @GetMapping("servicetypes")
    public Enums.ServiceTypes [] getServiceTypes(){
        return Enums.ServiceTypes.values();
    }

    @GetMapping("keys")
    public Enums.keys [] getKeys(){
        return Enums.keys.values();
    }

    @GetMapping("saburikeys")
    public Enums.Saburikeys [] getSaburiKeys(){
        return Enums.Saburikeys.values();
    }

    @GetMapping("vuefiles")
    public Enums.VueFiles [] getVueFiles(){
        return Enums.VueFiles.values();
    }

    @GetMapping("javafxfiles")
    public Enums.JavaFxFiles[] getJavaFxFiles(){
        return Enums.JavaFxFiles.values();
    }


}
