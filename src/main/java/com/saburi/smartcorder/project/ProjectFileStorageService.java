package com.saburi.smartcorder.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saburi.smartcorder.base.storage.FileStorageService;
import com.saburi.smartcorder.base.storage.StorageProperties;

public class ProjectFileStorageService extends FileStorageService<Project, String> {
    public ProjectFileStorageService(StorageProperties properties, ObjectMapper objectMapper) {
        super(properties, objectMapper);
    }
}
