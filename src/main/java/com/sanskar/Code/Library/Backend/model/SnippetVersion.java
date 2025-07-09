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

@Document("snippet_versions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnippetVersion {
    @Id
    private String id; // UUID
    private String snippetId; // Link to main snippet
    private int version;
    private LocalDateTime updatedAt; // version only changes when code actually updates instead of SnippetPushRequest

    private String title;
    private String description;

    @Builder.Default
    private List<String> tags = new ArrayList<>();
    private String language;
    private String code;


}
