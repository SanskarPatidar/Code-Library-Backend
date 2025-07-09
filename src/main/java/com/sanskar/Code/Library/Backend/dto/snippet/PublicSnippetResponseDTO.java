package com.sanskar.Code.Library.Backend.dto.snippet;

import com.sanskar.Code.Library.Backend.model.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSnippetResponseDTO {
    private String snippetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorName;
    private int version;
    private Map<String, String> collaborators;
    private String title;
    private String description;
    private List<String> tags;
    private String language;
    private String code;

    public PublicSnippetResponseDTO(Snippet snippet) {
        this.snippetId = snippet.getId();
        this.createdAt = snippet.getCreatedAt();
        this.updatedAt = snippet.getUpdatedAt();
        this.authorName = snippet.getAuthorName();
        this.version = snippet.getVersion();
        this.collaborators = snippet.getCollaborators();
        this.title = snippet.getTitle();
        this.description = snippet.getDescription();
        this.tags = snippet.getTags();
        this.language = snippet.getLanguage();
        this.code = snippet.getCode();
    }
}
