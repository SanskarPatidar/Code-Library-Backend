package com.sanskar.Code.Library.Backend.service.moderator;

import com.sanskar.Code.Library.Backend.exception.InvalidResourceStateException;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModeratorService {

    @Autowired
    private SnippetRepository snippetRepository;

    public void deletePublicSnippet(String snippetId) {
        Snippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.isPublicVisibility()) {
            throw new UnauthorizedException("Only public snippets can be deleted by moderators.");
        }

        if (snippet.isDeleted()) {
            throw new InvalidResourceStateException("Snippet is already deleted.");
        }

        snippet.setDeleted(true);
        snippetRepository.save(snippet);
    }
}
