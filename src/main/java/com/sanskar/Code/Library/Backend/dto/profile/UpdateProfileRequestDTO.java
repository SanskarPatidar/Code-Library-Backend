package com.sanskar.Code.Library.Backend.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequestDTO {
    private String bio;
    private String profileImageUrl;

    @Schema(defaultValue = "false")
    @Builder.Default
    private boolean isPublic = false;
}
