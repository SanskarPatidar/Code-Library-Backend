package com.sanskar.Code.Library.Backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthCodeExchangeRequest {
    @NotBlank(message = "Code cannot be blank")
    private String code;
    private String redirectUri;
}
