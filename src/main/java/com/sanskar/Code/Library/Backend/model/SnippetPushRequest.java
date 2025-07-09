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
public class SnippetPushRequest {
    @Id
    private String id; // UUID
    private String snippetId;
    private String requesterUsername;
    private String message;

    @Builder.Default
    private boolean approved = false;

    @Builder.Default
    private boolean rejected = false;

    private LocalDateTime requestedAt;
    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;

    @Builder.Default
    private List<String> proposedTags = new ArrayList<>();

    private String proposedLanguage;

}

