package com.sanskar.Code.Library.Backend.dto.snippet;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetCreateRequestDTO {
    @NotBlank(message = "title cannot be blank")
    private String title;
    private List<String> tags = new ArrayList<>();
    private boolean publicVisibility;
    private boolean allowPublicDownload;
    private String message;

    private String description;
    private String code;
    private String language;
}
