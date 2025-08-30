package com.sanskar.Code.Library.Backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document("snippets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Snippet {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime mainUpdatedAt;
    private String authorName;

    private String title;
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private String latestDescription;
    private String latestCode;
    private String latestLanguage;

    private boolean publicVisibility;
    private boolean allowPublicDownload;
    @Builder.Default
    private Map<String, String> collaborators = new HashMap<>();
    @Builder.Default
    private boolean deleted = false; // softly delete
}
