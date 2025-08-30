package com.sanskar.Code.Library.Backend.dto.branchpushrequest;

import com.sanskar.Code.Library.Backend.model.BranchPushRequest;
import com.sanskar.Code.Library.Backend.model.BranchPushRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPushRequestOutDTO {
    private String id;
    private String snippetId;
    private String targetBranchId;
    private String message;
    private LocalDateTime requestedAt;
    private BranchPushRequestStatus status;

    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;
    private String proposedLanguage;
    private List<String> proposedTags;

    public BranchPushRequestOutDTO(BranchPushRequest branchPushRequest){
        this.id = branchPushRequest.getId();
        this.snippetId = branchPushRequest.getSnippetId();
        this.targetBranchId = branchPushRequest.getTargetBranchId();
        this.message = branchPushRequest.getMessage();
        this.requestedAt = branchPushRequest.getRequestedAt();
        this.status = branchPushRequest.getStatus();
        this.proposedCode = branchPushRequest.getProposedCode();
        this.proposedTitle = branchPushRequest.getProposedTitle();
        this.proposedDescription = branchPushRequest.getProposedDescription();
        this.proposedLanguage = branchPushRequest.getProposedLanguage();
        this.proposedTags = branchPushRequest.getProposedTags();
    }
}
