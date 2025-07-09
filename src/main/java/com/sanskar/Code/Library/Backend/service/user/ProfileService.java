package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.profile.PrivateProfileResponseDTO;
import com.sanskar.Code.Library.Backend.dto.profile.PublicProfileResponseDTO;
import com.sanskar.Code.Library.Backend.dto.profile.UpdateProfileRequestDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.UserProfile;
import com.sanskar.Code.Library.Backend.repository.userprofile.UserProfileRepository;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private UserProfileRepository profileRepository;

    public PrivateProfileResponseDTO getMyProfile() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userPrincipal.getId();

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        return new PrivateProfileResponseDTO(profile);
    }

    public PrivateProfileResponseDTO updateMyProfile(UpdateProfileRequestDTO updatedProfile) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userPrincipal.getId();

        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        profile.setBio(updatedProfile.getBio());
        profile.setProfileImageUrl(updatedProfile.getProfileImageUrl());
        profile.setPublic(updatedProfile.isPublic());

        return new PrivateProfileResponseDTO(profileRepository.save(profile));
    }

    public PublicProfileResponseDTO getPublicProfile(String username) {
        UserProfile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!profile.isPublic()) {
            throw new UnauthorizedException("This profile is private.");
        }

        return new PublicProfileResponseDTO(profile);
    }
}

