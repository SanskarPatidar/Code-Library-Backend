package com.sanskar.Code.Library.Backend.dto.snippetbranch;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MergeRequestDTO {
    @NotBlank(message = "Snippet ID cannot be blank")
    private String snippetId;
    @NotBlank(message = "Source branch ID cannot be blank")
    private String sourceBranchId;
    @NotBlank(message = "Target branch ID cannot be blank")
    private String targetBranchId;
}
