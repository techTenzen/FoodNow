package com.foodnow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodNowApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodNowApplication.class, args);
        System.out.println("🚀 FoodNow Backend is running...");
    }
}
// This is the main entry point for the FoodNow application.