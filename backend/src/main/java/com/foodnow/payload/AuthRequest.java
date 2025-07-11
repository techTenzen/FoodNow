package com.foodnow.payload;

import jakarta.validation.constraints.NotBlank;

public class AuthRequest {
    @NotBlank
    private String usernameOrEmail;
    
    @NotBlank
    private String password;
    
    // Getters and Setters
    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}