package com.sanskar.Code.Library.Backend.security.controller;

import com.sanskar.Code.Library.Backend.security.config.AppCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Utility-Endpoints", description = "Utility operations for managing application cache and health checks")
@RestController
@RequestMapping("/util")
public class UtilityController {

    @Autowired
    private AppCache appCache;

    @Operation(
        summary = "Reload Application Cache keys",
        description = "Reloads the application cache keys from the database. Only accessible by ADMIN users."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cache/reload")
    public ResponseEntity<String> reloadCacheKeys() {
        appCache.loadSecretsFromDB();
        return ResponseEntity.ok("App cache reloaded.");
    }

    @Operation(
        summary = "Clear Application Cache keys",
        description = "Clears the application cache keys from the database. Only accessible by ADMIN users."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cache/clear")
    public ResponseEntity<String> clearCacheKeys() {
        appCache.appCacheValues.clear();
        return ResponseEntity.ok("App cache cleared.");
    }

    @Operation(
        summary = "Get all Application Cache keys",
        description = "Retrieves all application cache keys. Only accessible by ADMIN users."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cache/all")
    public ResponseEntity<Map<String, String>> getAllCacheKeys() {
        return ResponseEntity.ok(appCache.getAll());
    }

    @Operation(
        summary = "Health Check(PUBLIC ENDPOINT)",
        description = "Performs a health check of the application. No authentication required."
    )
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}