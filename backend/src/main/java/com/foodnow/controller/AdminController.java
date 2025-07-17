package com.foodnow.controller;

import com.foodnow.model.RestaurantApplication;
import com.foodnow.service.RestaurantApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private RestaurantApplicationService applicationService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, String>> getAdminDashboard() {
        return ResponseEntity.ok(Map.of("message", "Welcome to the Admin Dashboard!"));
    }

    @GetMapping("/applications/pending")
    public ResponseEntity<List<RestaurantApplication>> getPendingApplications() {
        return ResponseEntity.ok(applicationService.getPendingApplications());
    }

    @PostMapping("/applications/{applicationId}/approve")
    public ResponseEntity<?> approveRestaurantApplication(@PathVariable int applicationId) {
        try {
            return ResponseEntity.ok(applicationService.approveApplication(applicationId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add reject endpoint
    @PostMapping("/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectRestaurantApplication(@PathVariable int applicationId, 
                                                        @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Rejection reason is required");
            }
            applicationService.rejectApplication(applicationId, reason);
            return ResponseEntity.ok(Map.of("message", "Application rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}