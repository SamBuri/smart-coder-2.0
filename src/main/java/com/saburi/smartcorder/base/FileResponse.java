package com.saburi.smartcorder.base;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResponse {
    private String fullFileName;
    private String shortName;
    private String content;
    private boolean success;
    private String message;
}
