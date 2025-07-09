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
    private String id; // UUID from clients
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName;

    @Builder.Default
    private int version = 0;
    private boolean publicVisibility;

    @Builder.Default
    private Map<String, String> collaborators = new HashMap<>();  // ids who have access to this snippet's updates, not including owner

    @Builder.Default
    private List<String> pendingPushRequestIds = new ArrayList<>();

    @Builder.Default
    private boolean deleted = false; // soft delete

    private String title;
    private String description;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private String language;
    private String code;
}
