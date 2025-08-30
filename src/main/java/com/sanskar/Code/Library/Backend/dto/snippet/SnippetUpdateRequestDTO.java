package com.sanskar.Code.Library.Backend.dto.snippet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUpdateRequestDTO {
    @NotBlank(message = "snippetId cannot be blank")
    private String snippetId;

    @NotBlank(message = "title cannot be blank")
    private String title;
    @Schema(defaultValue = "false")
    @Builder.Default
    private boolean publicVisibility = false;
    @Schema(defaultValue = "false")
    @Builder.Default
    private boolean allowPublicDownload = false;
}
