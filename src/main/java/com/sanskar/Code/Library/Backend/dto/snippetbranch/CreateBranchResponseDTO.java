package com.sanskar.Code.Library.Backend.dto.snippetbranch;

import com.sanskar.Code.Library.Backend.model.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBranchResponseDTO {
    private String branchId;
    private String snippetId;
    private String branchName;
    private String sourceBranchId;
    private int sourceVersion;
    private String sourceVersionId;
    private int latestVersion;
    private String createdAt;

    public CreateBranchResponseDTO(Branch branch) {
        this.branchId = branch.getId();
        this.snippetId = branch.getSnippetId();
        this.branchName = branch.getBranchName();
        this.sourceBranchId = branch.getSourceBranchId();
        this.sourceVersion = branch.getSourceVersion();
        this.sourceVersionId = String.valueOf(branch.getSourceVersion());
        this.latestVersion = branch.getLatestVersion();
        this.createdAt = branch.getCreatedAt().toString();
    }
}
