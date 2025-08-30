package com.sanskar.Code.Library.Backend.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanskar.Code.Library.Backend.security.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String username;
    private String email;
    private String userId;
    private String deviceId;
    private List<Role> roles;
}
