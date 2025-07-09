package com.sanskar.Code.Library.Backend.dto.snippetpushrequest;

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
public class SnippetPushRequestIncDTO {
    @NotBlank(message = "Snippet ID cannot be blank")
    private String snippetId;
    private String message;
    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;
    private String proposedLanguage;
    private List<String> proposedTags;
}
