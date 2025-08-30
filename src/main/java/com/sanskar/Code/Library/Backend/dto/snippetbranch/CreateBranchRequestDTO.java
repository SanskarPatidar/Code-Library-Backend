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
public class CreateBranchRequestDTO {
    @NotBlank(message = "Snippet ID cannot be blank")
    private String snippetId;
    @NotBlank(message = "Branch name cannot be blank")
    private String branchName;
    private String sourceBranchId;
    private String sourceVersionId;
}
