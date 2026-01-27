/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcoder.vue3;


import com.saburi.smartcoder.base.FileModel;
import com.saburi.smartcoder.base.ProjectFile;
import com.saburi.smartcoder.utils.Enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author samburiima
 */
public class Vue3 {

    public Map<String, ProjectFile> register(FileModel fileModel) {
        Map<String, ProjectFile> map = new HashMap<>();
        map.put(Enums.VueFiles.Model.name(), new VueModel(fileModel));
        map.put(Enums.VueFiles.View.name(), new Vue(fileModel));
        map.put(Enums.VueFiles.Store.name(), new Store(fileModel));
        map.put(Enums.VueFiles.Nav.name(), new VueNav(fileModel));
        map.put(Enums.VueFiles.Search.name(), new Search(fileModel));
        map.put(Enums.VueFiles.Controller.name(), new Controller(fileModel));
        return map;
    }

}
