///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.saburi.smartcorder.project;
//
//import com.google.gson.JsonIOException;
//import com.google.gson.JsonSyntaxException;
//import com.google.gson.reflect.TypeToken;
//import com.saburi.smartcorder.base.DataAccess;
//import com.saburi.smartcorder.base.Model;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @author Hp
// */
//public class ProjectDAO extends Fil {
//
//    private Project project;
//    private transient final String currentFile = "Projects.json";
//
//    public ProjectDAO() {
//
//    }
//
//
//    public ProjectDAO(Project project) {
////        this.fileName = super.baseurl.concat(currentFile);
//        this.project = project;
//
//    }
//
//    @Override
//    protected String getFileName() {
//        return this.currentFile;
//    }
//
//
//    protected List<Project> readJson() throws Exception {
//        try {
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(createFile()));
//            return GSON.fromJson(bufferedReader,
//                    new TypeToken<List<Project>>() {
//                    }.getType());
//        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
//            throw e;
//        }
//    }
//
//    public List<Project> read() throws Exception {
//        List<Project> projects = this.readJson();
//        return projects != null ? projects : new ArrayList<>();
//
//    }
//
//    public void save() throws IOException, Exception {
//        List<? extends Model> projects = read();
//        this.saveJson((List<Model>) projects, project);
//    }
//
//    public Project getProject() {
//        return project;
//    }
//
//    @Override
//    public List<DataAccess> get() throws Exception {
//        try {
//            List<DataAccess> dataAccesses = new ArrayList<>();
//            this.read().forEach(proj -> dataAccesses.add(new ProjectDAO(proj)));
//            return dataAccesses;
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }
//
//    @Override
//    public void remove(Model model) throws Exception {
//        List<Project> projects = this.read();
//        projects.remove((Project) model);
//        this.saveJson(projects);
//    }
//
//    public Project get(String projectName) throws Exception {
//        try {
//
//            return this.read().stream()
//                    .filter((p) -> p.getProjectName().equalsIgnoreCase(projectName))
//                    .findFirst().orElse(null);
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
////    public Project get(int projectID) {
////        try {
////
////            return this.read().parallelStream()
////                    .filter((p) -> p.getProjectID() == projectID)
////                    .findFirst().orElse(null);
////        } catch (Exception e) {
////            return null;
////        }
////    }
//
//    @Override
//    public Model getModel() {
//        return this.project;
//    }
//}
