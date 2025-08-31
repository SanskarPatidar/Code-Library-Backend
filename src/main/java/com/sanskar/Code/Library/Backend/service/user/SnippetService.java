package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.PublicSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetCreateRequestDTO;
import com.sanskar.Code.Library.Backend.exception.InvalidResourceStateException;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.*;
import com.sanskar.Code.Library.Backend.repository.branchpushrequest.BranchPushRequestRepository;
import com.sanskar.Code.Library.Backend.repository.version.VersionRepository;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.repository.branch.BranchRepository;
import com.sanskar.Code.Library.Backend.security.repository.UserRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SnippetService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private BranchPushRequestRepository branchPushRequestRepository;

    @Autowired
    private Utils utils;

    @Transactional
    public PrivateSnippetResponseDTO createSnippet(SnippetCreateRequestDTO snippetRequest) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = Snippet.builder()
                .id(UUID.randomUUID().toString())
                .title(snippetRequest.getTitle())
                .tags(snippetRequest.getTags())
                .publicVisibility(snippetRequest.isPublicVisibility())
                .allowPublicDownload(snippetRequest.isAllowPublicDownload())
                .createdAt(LocalDateTime.now())
                .mainUpdatedAt(LocalDateTime.now())
                .latestDescription(snippetRequest.getDescription())
                .latestCode(snippetRequest.getCode())
                .latestLanguage(snippetRequest.getLanguage())
                .authorName(username)
                .build();

        Branch branch = Branch.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .branchName("main")
                .latestVersion(0)
                .createdAt(LocalDateTime.now())
                .build();

        Version mainVersion = Version.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .branchId(branch.getId())
                .createdAt(LocalDateTime.now())
                .message(snippetRequest.getMessage())
                .description(snippetRequest.getDescription())
                .code(snippetRequest.getCode())
                .language(snippetRequest.getLanguage())
                .build();

        branchRepository.save(branch);
        versionRepository.save(mainVersion);
        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public PrivateSnippetResponseDTO getSnippetById(String snippetId) { // By both author and collaborators
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        String userId = utils.getAuthenticatedUserId();

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(userId))
            throw new UnauthorizedException("You do not have permission to view this snippet.");

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

    @Transactional
    public void deleteSnippet(String snippetId) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        if (!snippet.getAuthorName().equals(username))
            throw new UnauthorizedException("Only the author can delete this snippet.");

        if(snippet.isDeleted())
            throw new InvalidResourceStateException("Snippet is already deleted.");

        snippet.setDeleted(true);
        snippetRepository.save(snippet);

        List<Branch> branches = branchRepository.findAllBySnippetIdAndDeletedFalse(snippetId);
        branches.forEach(branch -> branch.setDeleted(true));
        branchRepository.saveAll(branches);

        List<Version> versions = versionRepository.findAllBySnippetIdAndDeletedFalse(snippetId);
        versions.forEach(version -> version.setDeleted(true));
        versionRepository.saveAll(versions);

        List<BranchPushRequest> pushRequests = branchPushRequestRepository.findAllValidBySnippetId(snippetId);
        pushRequests.forEach(pr -> pr.setStatus(BranchPushRequestStatus.REJECTED));
        branchPushRequestRepository.saveAll(pushRequests);
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

    public PrivateSnippetResponseDTO toggleAllowPublicDownload(String snippetId, boolean allowDownload) {
        String username = utils.getAuthenticatedUsername();
        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found."));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can change download permissions.");
        }

        snippet.setAllowPublicDownload(allowDownload);
        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public Page<PublicSnippetResponseDTO> getPublicSnippets(List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            snippetRepository.findByPublicVisibilityTrueAndDeletedFalse(pageable)
                    .map(PublicSnippetResponseDTO::new);

        }
        return snippetRepository.findByPublicVisibilityTrueAndDeletedFalseAndTagsIn(tags, pageable)
                .map(PublicSnippetResponseDTO::new);
    }
}
