package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.security.model.User;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollaboratorService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    public PrivateSnippetResponseDTO addCollaborator(String snippetId, String collaboratorId) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can manage collaborators.");
        }

        if (!snippet.getCollaborators().containsKey(collaboratorId)) {
            User collaborator = userRepository.findById(collaboratorId)
                    .orElseThrow(() -> new NotFoundException("Collaborator not found"));

            snippet.getCollaborators().put(collaboratorId, collaborator.getUsername());
        }


        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public PrivateSnippetResponseDTO removeCollaborator(String snippetId, String collaboratorId) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can manage collaborators.");
        }

        snippet.getCollaborators().remove(collaboratorId); // immutable map, so this works

        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }
}
