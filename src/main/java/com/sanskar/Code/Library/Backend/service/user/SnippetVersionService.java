package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetversion.SnippetVersionResponseDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.model.SnippetVersion;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.repository.snippetversion.SnippetVersionRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SnippetVersionService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private SnippetVersionRepository snippetVersionRepository;

    @Autowired
    private Utils utils;

    public Page<SnippetVersionResponseDTO> getVersions(String snippetId, Pageable pageable) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(username)) {
            throw new UnauthorizedException("Not authorized to view version history.");
        }

        return snippetVersionRepository.findBySnippetIdOrderByVersionDesc(snippetId, pageable)
                .map(SnippetVersionResponseDTO::new);
    }

    public PrivateSnippetResponseDTO restoreVersion(String versionId) {
        String username = utils.getAuthenticatedUsername();
        SnippetVersion version = snippetVersionRepository.findById(versionId)
                .orElseThrow(() -> new NotFoundException("Version not found"));

        Snippet snippet = snippetRepository.findById(version.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can restore versions.");
        }

        // Apply rollback content
        snippet.setTitle(version.getTitle());
        snippet.setDescription(version.getDescription());
        snippet.setTags(version.getTags());
        snippet.setLanguage(version.getLanguage());
        snippet.setCode(version.getCode());
        snippet.setUpdatedAt(LocalDateTime.now());
        snippet.setVersion(snippet.getVersion() + 1);

        // Save new rollback version
        utils.saveVersionHistory(snippet);

        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }
}
