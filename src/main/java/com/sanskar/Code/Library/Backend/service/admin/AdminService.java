package com.sanskar.Code.Library.Backend.service.admin;

import com.sanskar.Code.Library.Backend.dto.profile.PublicProfileResponseDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.model.UserProfile;
import com.sanskar.Code.Library.Backend.repository.userprofile.UserProfileRepository;
import com.sanskar.Code.Library.Backend.security.model.Role;
import com.sanskar.Code.Library.Backend.security.model.User;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserProfileRepository userProfileRepository;

    public void promoteToModerator(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getRoles().contains(Role.MODERATOR)) {
            user.getRoles().add(Role.MODERATOR);
            userRepository.save(user);
        }

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        if (!profile.getRoles().contains(Role.MODERATOR)) {
            profile.getRoles().add(Role.MODERATOR);
            userProfileRepository.save(profile);
        }

    }

    public void demoteToUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.getRoles().remove(Role.MODERATOR);
        if (!user.getRoles().contains(Role.USER)) {
            user.getRoles().add(Role.USER);
        }
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        profile.getRoles().remove(Role.MODERATOR);
        if (!profile.getRoles().contains(Role.USER)) {
            profile.getRoles().add(Role.USER);
        }
        userProfileRepository.save(profile);

    }

    public Page<PublicProfileResponseDTO> getAllUsers(Pageable pageable) {
        return userProfileRepository.findAllByIsPublicTrueOrderByUsernameAsc(pageable)
                .map(PublicProfileResponseDTO::new);
    }

    public Page<PublicProfileResponseDTO> getAllModerators(Pageable pageable) {
        return userProfileRepository.findAllByRolesContaining(Role.MODERATOR, pageable)
                .map(PublicProfileResponseDTO::new);
    }
}
