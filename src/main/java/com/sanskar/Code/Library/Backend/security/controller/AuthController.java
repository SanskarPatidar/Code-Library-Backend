package com.sanskar.Code.Library.Backend.security.controller;

import com.sanskar.Code.Library.Backend.security.dto.AuthResponseDTO;
import com.sanskar.Code.Library.Backend.security.dto.LoginRequestDTO;
import com.sanskar.Code.Library.Backend.security.dto.RegisterRequestDTO;
import com.sanskar.Code.Library.Backend.security.service.AuthService;
import com.sanskar.Code.Library.Backend.security.service.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Auth-Public-Endpoints", description = "Authentication operations for user registration, login, and token refresh")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService userSecurityService;

    @Autowired
    private LogoutService logoutService;

    @Operation(
        summary = "Register a new user",
        description = "Endpoint for user registration. Accepts a RegisterRequestDTO object containing user details."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.ok(userSecurityService.register(registerRequestDTO)); // can check difference between different functions of ResponseEntity
    }

    @Operation(
        summary = "User login",
        description = "Endpoint for user login. Accepts a LoginRequestDTO object containing loginString(username or email) and password."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO){
        return ResponseEntity.ok(userSecurityService.login(loginRequestDTO));
    }

    @Operation(
        summary = "Refresh authentication token",
        description = "Endpoint to refresh the authentication token using the device ID and refresh token."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh/{deviceId}")
    public ResponseEntity<AuthResponseDTO> refresh(@PathVariable String deviceId, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userSecurityService.refresh(deviceId, request));
    }

    @Operation(
        summary = "Logout user",
        description = "Endpoint to logout the user by invalidating the access token and refresh token."
    )
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
        return ResponseEntity.ok("Logged out successfully");
    }

}
