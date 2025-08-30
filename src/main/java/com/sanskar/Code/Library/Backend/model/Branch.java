package com.sanskar.Code.Library.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("branches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    @Id
    private String id;
    private String snippetId;
    private String branchName;
    private LocalDateTime createdAt;
    private String sourceBranchId; // branch this was forked from
    private int sourceVersion; // version at which it was forked
    private String sourceVersionId;
    private int latestVersion;
    @Builder.Default
    private boolean deleted = false;
}
