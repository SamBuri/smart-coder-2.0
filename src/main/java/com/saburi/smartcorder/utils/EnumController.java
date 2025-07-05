package com.saburi.smartcorder.utils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/enums/")
public class EnumController {

    @GetMapping("projecttypes")
    public Enums.ProjectTypes [] getProjectTypes(){
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

    @GetMapping("keys")
    public Enums.keys [] getKeys(){
        return Enums.keys.values();
    }

    @GetMapping("saburikeys")
    public Enums.Saburikeys [] getSaburiKeys(){
        return Enums.Saburikeys.values();
    }

}
