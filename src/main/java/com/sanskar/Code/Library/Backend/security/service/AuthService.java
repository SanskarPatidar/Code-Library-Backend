package com.sanskar.Code.Library.Backend.security.service;


import com.sanskar.Code.Library.Backend.exception.UserAlreadyExistsException;
import com.sanskar.Code.Library.Backend.model.UserProfile;
import com.sanskar.Code.Library.Backend.repository.userprofile.UserProfileRepository;
import com.sanskar.Code.Library.Backend.security.dto.AuthResponse;
import com.sanskar.Code.Library.Backend.security.dto.LoginRequest;
import com.sanskar.Code.Library.Backend.security.dto.RegisterRequest;
import com.sanskar.Code.Library.Backend.security.model.Role;
import com.sanskar.Code.Library.Backend.security.model.Token;
import com.sanskar.Code.Library.Backend.security.model.User;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.security.repository.token.TokenRepository;
import com.sanskar.Code.Library.Backend.service.redis.RefreshTokenService;
import com.sanskar.Code.Library.Backend.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AuthService { // Main JWT business logic service class, MyUserDetailService is just for mapping

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    AuthenticationManager authManager;
    /*
    Raw username and password from request body
    Spring creates a UsernamePasswordAuthenticationToken
    Spring loads user from DB using username via MyUserDetailsService
    Spring compares raw password with hashed password in DB
    Authenticated token created with authorities and saved in SecurityContext
     */

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private Utils utils;

    @Autowired
    private UserProfileRepository profileRepository;

    public AuthResponse register(RegisterRequest registerRequest) {
        if (repo.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists.");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(encoder.encode(registerRequest.getPassword()))
                .isPasswordSet(true)
                .email(registerRequest.getEmail())
                .build();
        var savedUser = repo.save(user);

        String accessToken = jwtGenerator.generateAccessToken(user.getUsername());
        Token accessTokenEntity = utils.createToken(savedUser.getId(), accessToken);

        String refreshToken = jwtGenerator.generateRefreshToken(user.getUsername());

        tokenRepository.save(accessTokenEntity);
        refreshTokenService.storeRefreshToken(savedUser.getId(), accessTokenEntity.getDeviceId(), refreshToken); // Store refresh token in Redis

        UserProfile profile = UserProfile.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(List.of(Role.USER))
                .build();
        profileRepository.save(profile);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .userId(savedUser.getId())
                .deviceId(accessTokenEntity.getDeviceId())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // Calls UserDetailsService which was passed to AuthProvider in SecurityConfig
            // Authentication Object contains principal(UserDetails)
            // Using this approach when we don't trust user's details
            // call it DELEGATED AUTHENTICATION
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginString(), loginRequest.getPassword())
            );
            // This will do all validations of username and password

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // no revoke logic as we can't blindly trust devic id
            String accessToken = jwtGenerator.generateAccessToken(userPrincipal.getUsername());
            Token accessTokenEntity = utils.createToken(userPrincipal.getId(), accessToken);

            String refreshToken = jwtGenerator.generateRefreshToken(userPrincipal.getUsername());

            tokenRepository.save(accessTokenEntity);
            refreshTokenService.storeRefreshToken(userPrincipal.getId(), accessTokenEntity.getDeviceId(), refreshToken);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(userPrincipal.getUsername())
                    .email(userPrincipal.getEmail())
                    .userId(userPrincipal.getId())
                    .deviceId(accessTokenEntity.getDeviceId())
                    .build();

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password.");
        }
    }

    private void revokeAllUserTokens(User user, String deviceId) {
        var validUserTokens = tokenRepository.findValidTokensByUserIdAndDeviceId(user.getId(), deviceId);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        refreshTokenService.deleteRefreshToken(user.getId(), deviceId);
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthResponse refresh(String deviceId, HttpServletRequest request) throws IOException {
        // JWT filter like validation
        String raw = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (raw == null || !raw.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or malformed token.");
        }
        String refreshToken = raw.substring(7);
        String username = jwtGenerator.extractUserName(refreshToken);
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            User user = repo.findByUsername(username)
                    .orElseThrow(() -> new BadCredentialsException("User not found."));
            boolean isTokenValid = refreshTokenService.isRefreshTokenValid(user.getId(), deviceId, refreshToken); // this also validates for deviceId

            if (jwtGenerator.validateToken(refreshToken, userDetails) && isTokenValid) {
                revokeAllUserTokens(user, deviceId);

                String accessToken = jwtGenerator.generateAccessToken(username);
                Token accessTokenEntity = utils.createToken(user.getId(), accessToken);
                refreshToken = jwtGenerator.generateRefreshToken(username);

                accessTokenEntity.setDeviceId(deviceId);

                tokenRepository.save(accessTokenEntity);
                refreshTokenService.storeRefreshToken(user.getId(), deviceId, refreshToken);

                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .userId(user.getId())
                        .deviceId(deviceId)
                        .build();
            }
        }
        throw new BadCredentialsException("Invalid refresh token.");
    }
}