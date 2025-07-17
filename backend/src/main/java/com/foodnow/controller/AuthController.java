package com.foodnow.controller;

import com.foodnow.dto.JwtAuthenticationResponse;
import com.foodnow.dto.LoginRequest;
import com.foodnow.dto.SignUpRequest;
import com.foodnow.model.User;
import com.foodnow.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String jwt = authenticationService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        User result = authenticationService.registerUser(signUpRequest);
        return ResponseEntity.ok("User registered successfully!");
    }
}