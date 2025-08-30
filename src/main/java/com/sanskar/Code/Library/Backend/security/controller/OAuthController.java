package com.sanskar.Code.Library.Backend.security.controller;

import com.sanskar.Code.Library.Backend.security.dto.AuthResponseDTO;
import com.sanskar.Code.Library.Backend.security.dto.OAuthCodeExchangeRequestDTO;
import com.sanskar.Code.Library.Backend.security.dto.PasswordRequestDTO;
import com.sanskar.Code.Library.Backend.security.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "OAuth-Public-Endpoints", description = "OAuth operations for handling Google authentication and setting passwords for OAuth users")
@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @Operation(
        summary = "Handle Google OAuth Callback",
        description = "Exchanges the authorization code received from Google for an access token and user information."
    )
    @GetMapping("/google/callback")
    public ResponseEntity<AuthResponseDTO> handleGoogleCallback(@RequestBody OAuthCodeExchangeRequestDTO oAuthCodeExchangeRequestDTO){
        return ResponseEntity.ok(oAuthService.handleGoogleCallback(oAuthCodeExchangeRequestDTO));
    }

    @Operation(
        summary = "Set Password for OAuth User",
        description = "Sets a password for a user authenticated via OAuth, allowing them to log in with email and password. Should be called after the OAuth login process."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/set-password")
    public ResponseEntity<String> setPasswordForOAuthUser(@RequestBody PasswordRequestDTO passwordRequestDTO) {
        oAuthService.setPasswordForOAuthUser(passwordRequestDTO);
        return ResponseEntity.ok("Password set successfully for OAuth user.");
    }
}
