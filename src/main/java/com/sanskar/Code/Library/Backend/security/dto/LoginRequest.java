package com.sanskar.Code.Library.Backend.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username or email cannot be blank")
    @Size(min = 3, message = "Username or email must be at least 3 characters")
    private String loginString; // can be username or email

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

}
