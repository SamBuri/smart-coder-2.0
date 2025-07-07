package com.saburi.smartcorder.base;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseObj<T> {
    private boolean success;
    private String message;
    private String details;
    private T data;
    
    // Constructor, getters, setters
}
