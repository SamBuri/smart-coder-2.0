/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcoder.springboot;



import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.base.ProjectFile;
import com.saburi.smartcoder.utils.Enums;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author samburiima
 */
@Component
public class SpringBoot {
  ;
    public Map<String, ProjectFile> register(FileModel fileModel) {
        Map<String, ProjectFile> map = new HashMap<>();
        map.put(Enums.SpringBootFiles.Entity.name(), new Entity(fileModel));
        map.put(Enums.SpringBootFiles.Request.name(), new Request(fileModel));
        map.put(Enums.SpringBootFiles.Mini.name(), new Mini(fileModel));
        map.put(Enums.SpringBootFiles.Repo.name(), new Repository(fileModel));
        map.put(Enums.SpringBootFiles.Service.name(), new Service(fileModel));
        map.put(Enums.SpringBootFiles.Controller.name(), new WebController(fileModel));
        map.put(Enums.SpringBootFiles.ControllerTest.name(), new ControllerTest(fileModel));
        map.put(Enums.SpringBootFiles.ServiceTest.name(), new ServiceTest(fileModel));
        map.put(Enums.SpringBootFiles.Change_Log.name(), new ChangeLog(fileModel));
        return map;
    }

}
