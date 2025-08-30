package com.sanskar.Code.Library.Backend.dto.branchpushrequest;

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
public class BranchPushRequestIncDTO {
    @NotBlank(message = "Snippet ID cannot be blank")
    private String snippetId;
    @NotBlank(message = "Branch ID cannot be blank")
    private String targetBranchId;
    private String message;

    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;
    private String proposedLanguage;
    private List<String> proposedTags;
}
