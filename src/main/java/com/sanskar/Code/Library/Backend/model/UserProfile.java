package com.sanskar.Code.Library.Backend.model;

import com.sanskar.Code.Library.Backend.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_profiles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    private String id;
    private String userId; // reference to User

    private String username;
    private String bio;
    private String profileImageUrl;
    private String email;
    private List<Role> roles;
    @Builder.Default
    private boolean isPublic = false;
}