package com.sanskar.Code.Library.Backend.repository.userprofile;

import com.sanskar.Code.Library.Backend.model.UserProfile;
import com.sanskar.Code.Library.Backend.security.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
    Optional<UserProfile> findByUsername(String username);
    Page<UserProfile> findAllByIsPublicTrueOrderByUsernameAsc(Pageable pageable);
    Page<UserProfile> findAllByRolesContaining(Role role, Pageable pageable);
}