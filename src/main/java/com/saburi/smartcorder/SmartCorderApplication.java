package com.saburi.smartcorder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController("/")
public class SmartCorderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCorderApplication.class, args);
	}
        
        @GetMapping
        public String onStartup(){
           return "Application started on "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE d MMM yyyy 'at' HH:mm:ss", Locale.ENGLISH));
        }

}
