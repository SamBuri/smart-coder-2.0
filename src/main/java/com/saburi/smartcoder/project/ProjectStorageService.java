//package com.saburi.smartcorder.project;
//
//import com.saburi.smartcorder.base.storage.FileStorageService;
//import com.saburi.smartcorder.base.storage.StorageRouterService;
//import com.saburi.smartcorder.base.storage.db.BaseDbStorageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ProjectStorageService extends StorageRouterService<Project, String> {
//
//    private final ProjectFileStorageService fileStorageService;
//    private final ProjectDbService projectDbService;
//
//    @Override
//    protected FileStorageService<Project, String> getFileStorageService() {
//        return this.fileStorageService;
//    }
//
//    @Override
//    protected BaseDbStorageService<Project, String> getDbStorageService() {
//        return this.projectDbService;
//    }
//
//
//
//}
