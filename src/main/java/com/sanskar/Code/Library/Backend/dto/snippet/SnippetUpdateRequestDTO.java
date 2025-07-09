package com.sanskar.Code.Library.Backend.dto.snippet;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUpdateRequestDTO {
    @NotBlank(message = "snippetId cannot be blank")
    private String snippetId;

    @NotBlank(message = "title cannot be blank")
    private String title;

    private String description;
    private String language;
    private String code;
    private List<String> tags;

    @Schema(defaultValue = "false")
    @Builder.Default
    private boolean publicVisibility = false;

    @Schema(defaultValue = "false")
    @Builder.Default
    private boolean allowPublicDownload = false;
}
