package com.sanskar.Code.Library.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("snippet_push_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchPushRequest {
    @Id
    private String id;
    private String snippetId;
    private String targetBranchId;
    private String requestedBy;
    private LocalDateTime requestedAt;
    private String message;
    @Builder.Default
    private BranchPushRequestStatus status = BranchPushRequestStatus.PENDING;

    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;
    @Builder.Default
    private List<String> proposedTags = new ArrayList<>();
    private String proposedLanguage;
}

