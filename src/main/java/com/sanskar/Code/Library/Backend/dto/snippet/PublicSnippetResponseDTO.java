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
    private LocalDateTime mainUpdatedAt;
    private String authorName;
    private int mainVersion;
    private Map<String, String> collaborators;

    private String title;
    private List<String> tags;
    private String description;
    private String code;
    private String language;

    public PublicSnippetResponseDTO(Snippet snippet) {
        this.snippetId = snippet.getId();
        this.createdAt = snippet.getCreatedAt();
        this.mainUpdatedAt = snippet.getMainUpdatedAt();
        this.authorName = snippet.getAuthorName();
        this.collaborators = snippet.getCollaborators();

        this.title = snippet.getTitle();
        this.tags = snippet.getTags();
        this.description = snippet.getLatestDescription();
        this.code = snippet.getLatestCode();
        this.language = snippet.getLatestLanguage();
    }
}
