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
public class PrivateSnippetResponseDTO {
    private String snippetId;
    private String title;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime mainUpdatedAt;
    private String authorName;
    private boolean publicVisibility;
    private boolean allowPublicDownload;
    private Map<String, String> collaborators;
    private List<String> pendingPushRequestIds;

    public PrivateSnippetResponseDTO(Snippet snippet){
        this.snippetId = snippet.getId();
        this.title = snippet.getTitle();
        this.tags = snippet.getTags();
        this.createdAt = snippet.getCreatedAt();
        this.mainUpdatedAt = snippet.getMainUpdatedAt();
        this.authorName = snippet.getAuthorName();
        this.publicVisibility = snippet.isPublicVisibility();
        this.allowPublicDownload = snippet.isAllowPublicDownload();
        this.collaborators = snippet.getCollaborators();
    }
}