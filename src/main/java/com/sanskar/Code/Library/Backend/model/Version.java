package com.sanskar.Code.Library.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("snippet_versions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Version {
    @Id
    private String id; // UUID
    private String snippetId;
    private String branchId;
    @Builder.Default
    private int version = 0;
    private LocalDateTime createdAt;
    private String message;

    private String description;
    private String language;
    private String code;

    @Builder.Default
    private boolean deleted = false;
}
