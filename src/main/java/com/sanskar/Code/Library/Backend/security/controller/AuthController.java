package com.sanskar.Code.Library.Backend.security.controller;

import com.sanskar.Code.Library.Backend.security.dto.AuthResponse;
import com.sanskar.Code.Library.Backend.security.dto.LoginRequest;
import com.sanskar.Code.Library.Backend.security.dto.RegisterRequest;
import com.sanskar.Code.Library.Backend.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth-Public-Endpoints", description = "Authentication operations for user registration, login, and token refresh")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService userSecurityService;

    @Operation(
        summary = "Register a new user",
        description = "Endpoint for user registration. Accepts a RegisterRequest object containing user details."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
        return ResponseEntity.ok(userSecurityService.register(registerRequest)); // can check difference between different functions of ResponseEntity
    }

    @Operation(
        summary = "User login",
        description = "Endpoint for user login. Accepts a LoginRequest object containing loginString(username or email) and password."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(userSecurityService.login(loginRequest));
    }

    @Operation(
        summary = "Refresh authentication token",
        description = "Endpoint to refresh the authentication token using the device ID and refresh token."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh/{deviceId}")
    public AuthResponse refresh(@PathVariable String deviceId, HttpServletRequest request) throws IOException {
        return userSecurityService.refresh(deviceId, request);
    }

}
