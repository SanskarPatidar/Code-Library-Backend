package com.sanskar.Code.Library.Backend.dto.branchversion;

import com.sanskar.Code.Library.Backend.model.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BranchVersionResponseDTO {
    private String id;
    private String snippetId;
    private String branchId;
    private int version;
    private LocalDateTime createdAt;
    private String message;

    private String description;
    private String language;
    private String code;

    public BranchVersionResponseDTO(Version version){
        this.id = version.getId();
        this.snippetId = version.getSnippetId();
        this.version = version.getVersion();
        this.createdAt = version.getCreatedAt();
        this.message = version.getMessage();
        this.description = version.getDescription();
        this.language = version.getLanguage();
        this.code = version.getCode();
    }
}
