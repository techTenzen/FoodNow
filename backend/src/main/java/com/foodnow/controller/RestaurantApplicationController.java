package com.foodnow.controller;

import com.foodnow.model.RestaurantApplication;
import com.foodnow.service.RestaurantApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.foodnow.dto.RestaurantApplicationRequest; // <-- Import the new DTO

@RestController
@RequestMapping("/api/applications/restaurant")
public class RestaurantApplicationController {

    @Autowired
    private RestaurantApplicationService applicationService;

@PostMapping("/apply")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> applyForRestaurant(@RequestBody RestaurantApplicationRequest request) {
        try {
            RestaurantApplication newApplication = applicationService.applyForRestaurant(request);
            return ResponseEntity.ok(newApplication);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            // Generic catch for any other unexpected errors
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}