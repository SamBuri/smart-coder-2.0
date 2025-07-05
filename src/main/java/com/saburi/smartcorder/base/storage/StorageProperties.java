package com.saburi.smartcorder.base.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

//@ConfigurationProperties("storage")
public class StorageProperties {
    private String location = "storage-files";

    // Getters and setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
