package com.sanskar.Code.Library.Backend.dto.profile;

import com.sanskar.Code.Library.Backend.model.UserProfile;
import com.sanskar.Code.Library.Backend.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicProfileResponseDTO {
    private String username;
    private String bio;
    private String profileImageUrl;
    private List<Role> roles;

    public PublicProfileResponseDTO(UserProfile profile){
        this.username = profile.getUsername();
        this.bio = profile.getBio();
        this.profileImageUrl = profile.getProfileImageUrl();
        this.roles = profile.getRoles();
    }
}
