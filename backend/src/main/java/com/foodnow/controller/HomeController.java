package com.foodnow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "✅ FoodNow Backend is running!";
    }
}
// This controller serves as a simple health check endpoint for the FoodNow application.