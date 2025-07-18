package com.foodnow.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "Welcome to FoodNow API!";
    }
    
    @GetMapping("/api/test")
    public String test() {
        return "API is working!";
    }
}