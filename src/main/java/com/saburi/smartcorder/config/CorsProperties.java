package com.saburi.smartcorder.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {

    private String [] allowedOrigins;
    private String [] allowedMethods;
    private String [] allowedHeaders;
    private boolean allowCredentials = false;
    private long maxAge = 3600;

}
