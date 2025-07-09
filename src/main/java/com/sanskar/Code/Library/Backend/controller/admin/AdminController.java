package com.sanskar.Code.Library.Backend.controller.admin;

import com.sanskar.Code.Library.Backend.dto.PageResponse;
import com.sanskar.Code.Library.Backend.dto.profile.PublicProfileResponseDTO;
import com.sanskar.Code.Library.Backend.service.admin.AdminService;
import com.sanskar.Code.Library.Backend.service.redis.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin-Endpoints(ADMIN ONLY)", description = "Admin operations for managing users and moderators")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin") // only affect http requests, not websocket
@PreAuthorize("hasRole('ADMIN')") // or "hasAuthority('ROLE_ADMIN')", will also work for msg-mapping as configured in SecurityConfig for WS
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Operation(
        summary = "Promote a user to moderator",
        description = "Promotes a user to moderator role. Only accessible by ADMIN."
    )
    @PostMapping("/promote-to-moderator/{userId}")
    public ResponseEntity<String> promoteToModerator(@PathVariable String userId) {
        adminService.promoteToModerator(userId);
        return ResponseEntity.ok("User promoted to MODERATOR.");
    }

    @Operation(
        summary = "Demote a moderator to user",
        description = "Demotes a moderator back to user role. Only accessible by ADMIN."
    )
    @PostMapping("/demote-to-user/{userId}")
    public ResponseEntity<String> demoteToUser(@PathVariable String userId) {
        adminService.demoteToUser(userId);
        return ResponseEntity.ok("Moderator demoted to USER.");
    }

    @Operation(
        summary = "Get all users",
        description = "Fetches a paginated list of all public profiles. Only accessible by ADMIN."
    )
    @GetMapping("/user-profiles/all")
    public ResponseEntity<PageResponse<PublicProfileResponseDTO>> getAllUsersProfile(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(adminService.getAllUsers(PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Get all moderators",
        description = "Fetches a paginated list of all moderator roled user profiles, even if profile is private. Only accessible by ADMIN."
    )
    @GetMapping("/moderator-profiles/all")
    public ResponseEntity<PageResponse<PublicProfileResponseDTO>> getAllModerators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(adminService.getAllModerators(PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Clear Redis cache",
        description = "Clears all cached refresh tokens in Redis. Only accessible by ADMIN."
    )
    @DeleteMapping("/clear-redis-cache")
    public ResponseEntity<String> clearRedisCache() {
        refreshTokenService.clearAllCache();
        return ResponseEntity.ok("Redis cache cleared successfully.");
    }
}
