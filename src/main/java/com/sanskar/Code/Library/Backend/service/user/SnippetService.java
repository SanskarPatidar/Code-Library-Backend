package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.PublicSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetCreateRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetUpdateRequestDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SnippetService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Utils utils;

    public PrivateSnippetResponseDTO createSnippet(SnippetCreateRequestDTO snippetRequest) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = Snippet.builder()
                .id(UUID.randomUUID().toString())
                .title(snippetRequest.getTitle())
                .description(snippetRequest.getDescription())
                .tags(snippetRequest.getTags() != null ? snippetRequest.getTags() : new ArrayList<>())
                .language(snippetRequest.getLanguage())
                .code(snippetRequest.getCode())
                .publicVisibility(snippetRequest.isPublicVisibility())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .authorName(username)
                .build();

        // set collaborators later

        utils.saveVersionHistory(snippet);

        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public PrivateSnippetResponseDTO updateSnippetDirectly(SnippetUpdateRequestDTO updatedSnippet) {

        String username = utils.getAuthenticatedUsername();

        Snippet existingSnippet = snippetRepository.findByIdAndDeletedFalse(updatedSnippet.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        if (!existingSnippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can directly update the snippet.");
        }

        existingSnippet.setTitle(updatedSnippet.getTitle());
        existingSnippet.setDescription(updatedSnippet.getDescription());
        existingSnippet.setTags(updatedSnippet.getTags() != null? updatedSnippet.getTags() : new ArrayList<>());
        existingSnippet.setLanguage(updatedSnippet.getLanguage());
        existingSnippet.setCode(updatedSnippet.getCode());
        existingSnippet.setUpdatedAt(LocalDateTime.now());
        existingSnippet.setVersion(existingSnippet.getVersion() + 1);

        utils.saveVersionHistory(existingSnippet);
        return new PrivateSnippetResponseDTO(snippetRepository.save(existingSnippet));
    }

    public PrivateSnippetResponseDTO getSnippetById(String snippetId) { // By both author and collaborators
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));
        String userId = utils.getAuthenticatedUserId();

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(userId)) {
            throw new UnauthorizedException("You do not have permission to view this snippet.");
        }

        return new PrivateSnippetResponseDTO(snippet);
    }

    public Page<PrivateSnippetResponseDTO> getMySnippets(Pageable pageable) {
        String username = utils.getAuthenticatedUsername();
        return snippetRepository.findAllByAuthorNameAndDeletedFalse(username, pageable)
                .map(PrivateSnippetResponseDTO::new); // map function of page
    }

    public Page<PrivateSnippetResponseDTO> getCollaboratingSnippets(Pageable pageable) {
        String userId = utils.getAuthenticatedUserId();
        return snippetRepository.findByCollaboratorIdAndDeletedFalse(userId, pageable)
                .map(PrivateSnippetResponseDTO::new);
    }

    public void deleteSnippet(String snippetId) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can delete this snippet.");
        }

        snippet.setDeleted(true);
        snippetRepository.save(snippet);
    }

    public void pullSnippet(String snippetId) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));
        String userId = utils.getAuthenticatedUserId();

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(userId)) {
            throw new UnauthorizedException("You do not have permission to pull this snippet.");
        }

        snippet.getPullHistory().add(Pair.of(username, LocalDateTime.now()));
        snippetRepository.save(snippet);
    }

    public PrivateSnippetResponseDTO togglePublicVisibility(String snippetId, boolean isPublic) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can change visibility.");
        }

        snippet.setPublicVisibility(isPublic);
        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public Page<PublicSnippetResponseDTO> getPublicSnippets(List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return snippetRepository.findByPublicVisibilityTrueAndDeletedFalse(pageable)
                    .map(PublicSnippetResponseDTO::new);
        }
        return snippetRepository.findByPublicVisibilityTrueAndDeletedFalseAndTagsIn(tags, pageable)
                .map(PublicSnippetResponseDTO::new);
    }
}
