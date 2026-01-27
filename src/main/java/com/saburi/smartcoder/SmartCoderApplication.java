package com.saburi.smartcoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SmartCoderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCoderApplication.class, args);
	}
        
        @GetMapping("/api")
        public String onStartup(){
           return "Application started on "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE d MMM yyyy 'at' HH:mm:ss", Locale.ENGLISH));
        }

    @PostConstruct
    public void init() {
        Path dbDir = Paths.get(System.getProperty("user.home"), ".smart-coder");
        try {
            Files.createDirectories(dbDir);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create config directory: " + dbDir, e);
        }
    }

}
