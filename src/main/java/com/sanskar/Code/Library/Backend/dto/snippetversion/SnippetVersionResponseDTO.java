package com.sanskar.Code.Library.Backend.dto.snippetversion;

import com.sanskar.Code.Library.Backend.model.SnippetVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SnippetVersionResponseDTO {
    private String id;
    private String snippetId;
    private int version;
    private LocalDateTime updatedAt;
    private String title;
    private String description;
    private List<String> tags;
    private String language;
    private String code;

    public SnippetVersionResponseDTO(SnippetVersion snippetVersion){
        this.id = snippetVersion.getId();
        this.snippetId = snippetVersion.getSnippetId();
        this.version = snippetVersion.getVersion();
        this.updatedAt = snippetVersion.getUpdatedAt();
        this.title = snippetVersion.getTitle();
        this.description = snippetVersion.getDescription();
        this.tags = snippetVersion.getTags();
        this.language = snippetVersion.getLanguage();
        this.code = snippetVersion.getCode();
    }
}
