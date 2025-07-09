package com.sanskar.Code.Library.Backend.dto.snippetpushrequest;

import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetPushRequestOutDTO {
    private String id;
    private String snippetId;
    private String message;
    private String proposedCode;
    private String proposedTitle;
    private String proposedDescription;
    private String proposedLanguage;
    private List<String> proposedTags;
    private LocalDateTime requestedAt;

    public SnippetPushRequestOutDTO(SnippetPushRequest snippetPushRequest){
        this.id = snippetPushRequest.getId();
        this.snippetId = snippetPushRequest.getSnippetId();
        this.message = snippetPushRequest.getMessage();
        this.proposedCode = snippetPushRequest.getProposedCode();
        this.proposedTitle = snippetPushRequest.getProposedTitle();
        this.proposedDescription = snippetPushRequest.getProposedDescription();
        this.proposedLanguage = snippetPushRequest.getProposedLanguage();
        this.proposedTags = snippetPushRequest.getProposedTags();
        this.requestedAt = snippetPushRequest.getRequestedAt();
    }
}
