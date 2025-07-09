package com.sanskar.Code.Library.Backend.security.service;

import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.security.dto.AuthResponse;
import com.sanskar.Code.Library.Backend.security.dto.OAuthCodeExchangeRequest;
import com.sanskar.Code.Library.Backend.security.dto.PasswordRequest;
import com.sanskar.Code.Library.Backend.security.model.Token;
import com.sanskar.Code.Library.Backend.security.model.User;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.security.repository.token.TokenRepository;
import com.sanskar.Code.Library.Backend.service.redis.RefreshTokenService;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class OAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private Utils utils;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse handleGoogleCallback(OAuthCodeExchangeRequest oAuthCodeExchangeRequest) {
        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> tokenRequestParams = new LinkedMultiValueMap<>();
            tokenRequestParams.add("code", oAuthCodeExchangeRequest.getCode());
            tokenRequestParams.add("client_id", clientId);
            tokenRequestParams.add("client_secret", clientSecret);
            tokenRequestParams.add("redirect_uri", oAuthCodeExchangeRequest.getRedirectUri());
            tokenRequestParams.add("grant_type", "authorization_code");

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> tokenRequest =
                    new HttpEntity<>(tokenRequestParams, tokenHeaders);

            ResponseEntity<Map> tokenResponse =
                    restTemplate.postForEntity(tokenEndpoint, tokenRequest, Map.class);

            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoEndpoint = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            ResponseEntity<Map> userInfoResponse =
                    restTemplate.getForEntity(userInfoEndpoint, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");

                User user;
                try {
                    user = userRepository.findByEmail(email)
                            .orElseThrow();
                } catch (Exception e) {
                    user = userRepository.save(User.builder()
                            .username(email)
                            .email(email)
                            .password(UUID.randomUUID().toString())
                            .build());
                }
                String accessToken = jwtGenerator.generateAccessToken(user.getUsername());
                Token accessTokenEntity = utils.createToken(user.getId(), accessToken);

                String refreshToken = jwtGenerator.generateRefreshToken(user.getUsername());

                tokenRepository.save(accessTokenEntity);
                refreshTokenService.storeRefreshToken(user.getId(), accessTokenEntity.getDeviceId(), refreshToken); // Store refresh token in Redis

                return AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .userId(user.getId())
                        .deviceId(accessTokenEntity.getDeviceId())
                        .build();

            }

            throw new UnauthorizedException("Failed to retrieve user information from Google");

        } catch (Exception e) {
            throw new RuntimeException("Failed to handle Google callback", e);
        }
    }

    public void setPasswordForOAuthUser(PasswordRequest passwordRequest) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userPrincipal.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found."));

        if(user.isPasswordSet()) {
            throw new UnauthorizedException("Password is already set for this OAuth user.");
        }

        String encodedPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        user.setPassword(encodedPassword);
        user.setPasswordSet(true);
        userRepository.save(user);
    }
}
