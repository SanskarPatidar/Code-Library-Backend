package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.profile.PrivateProfileResponseDTO;
import com.sanskar.Code.Library.Backend.dto.profile.PublicProfileResponseDTO;
import com.sanskar.Code.Library.Backend.dto.profile.UpdateProfileRequestDTO;
import com.sanskar.Code.Library.Backend.service.user.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Profile-Endpoints", description = "User profile operations for viewing and updating profiles")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(
        summary = "Get My Profile",
        description = "Fetches the private profile of the authenticated user."
    )
    @GetMapping("/me")
    public ResponseEntity<PrivateProfileResponseDTO> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @Operation(
        summary = "Update My Profile",
        description = "Updates the profile of the authenticated user and returns his private profile."
    )
    @PutMapping("/me")
    public ResponseEntity<String> updateMyProfile(@RequestBody UpdateProfileRequestDTO dto) {
        profileService.updateMyProfile(dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @Operation(
        summary = "Get Public Profile",
        description = "Fetches the public profile of mentioned user by his username."
    )
    @GetMapping("/public/{username}")
    public ResponseEntity<PublicProfileResponseDTO> getPublicProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getPublicProfile(username));
    }
}
