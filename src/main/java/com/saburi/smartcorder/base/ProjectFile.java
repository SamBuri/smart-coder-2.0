/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.saburi.smartcorder.base;

import com.saburi.smartcorder.base.exceptions.KnownException;

import java.io.File;

/**
 *
 * @author samburiima
 */
public abstract class ProjectFile {

    protected FileModel fileModel;
    protected final String FILE_SEPARATOR="//";

    public ProjectFile(FileModel fileModel) {
        this.fileModel = fileModel;
    }
    
    
    
    protected abstract String getBaseFolder();

    protected abstract String getFolderName();

    protected abstract String getFileName();

    protected abstract String getFileExtension();

    protected String getFullFileName() {
        String baseFolderName = fileModel.isSaveToProject()?getBaseFolder():fileModel.getOutputFolder();
        String folder= String.format("%s%s%s", baseFolderName, FILE_SEPARATOR,getFolderName());
//        Utilities.makeDirectory(folder);
        return String.format("%s%s%s.%s", folder, FILE_SEPARATOR, getFileName(), getFileExtension());
    }

    protected boolean isValid() throws KnownException {
        return true;
    }

    protected abstract String create() throws Exception;

    public FileResponse generate() throws Exception {
        if (!this.isValid()) {

        }
        String fileName = getFullFileName();
        File file = new File(fileName);
       return FileResponse.builder()
               .fullFileName(getFullFileName())
               .shortName(getFileName())
               .content(create())
               .success(true)
               .message("Operation Successful")
               .build();

       
    }

}
