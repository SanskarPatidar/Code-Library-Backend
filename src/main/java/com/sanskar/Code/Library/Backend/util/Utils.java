package com.sanskar.Code.Library.Backend.util;

import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.model.SnippetVersion;
import com.sanskar.Code.Library.Backend.repository.snippetversion.SnippetVersionRepository;
import com.sanskar.Code.Library.Backend.security.model.Token;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class Utils {

    @Autowired
    private SnippetVersionRepository snippetVersionRepository;

    public void saveVersionHistory(Snippet snippet) {

        SnippetVersion version = SnippetVersion.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .version(snippet.getVersion())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .tags(new ArrayList<>(snippet.getTags()))
                .language(snippet.getLanguage())
                .code(snippet.getCode())
                .updatedAt(LocalDateTime.now())
                .build();

        snippetVersionRepository.save(version);
    }

    public String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public Token createToken(String userId, String token) {
        return Token.builder()
                .userId(userId)
                .token(token)
                .expired(false)
                .revoked(false)
                .deviceId(UUID.randomUUID().toString())
                .build();
    }

    public String getAuthenticatedUserId() {
        UserPrincipal userPrincipal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getId();
    }
}
