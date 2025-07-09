package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetpushrequest.SnippetPushRequestIncDTO;
import com.sanskar.Code.Library.Backend.dto.snippetpushrequest.SnippetPushRequestOutDTO;
import com.sanskar.Code.Library.Backend.exception.InvalidResourceStateException;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.model.SnippetPushRequest;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.repository.snippetpushrequest.SnippetPushRequestRepository;
import com.sanskar.Code.Library.Backend.security.model.UserPrincipal;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SnippetPushRequestService {

    @Autowired
    private SnippetPushRequestRepository snippetPushRequestRepository;

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private Utils utils;

    public SnippetPushRequestOutDTO createPushRequest(SnippetPushRequestIncDTO request) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(request.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userPrincipal.getId();

        if (!snippet.getCollaborators().containsKey(userId)) {
            throw new UnauthorizedException("You are not allowed to push to this snippet.");
        }

        SnippetPushRequest savedRequest = snippetPushRequestRepository.save(
                SnippetPushRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .snippetId(request.getSnippetId())
                        .message(request.getMessage())
                        .proposedCode(request.getProposedCode())
                        .proposedTitle(request.getProposedTitle())
                        .proposedDescription(request.getProposedDescription())
                        .proposedTags(request.getProposedTags())
                        .proposedLanguage(request.getProposedLanguage())
                        .requestedAt(LocalDateTime.now())
                        .requesterUsername(username)
                        .build()
                );

        List<String> pending = snippet.getPendingPushRequestIds();
        if (pending == null)pending = new ArrayList<>();
        pending.add(savedRequest.getId());
        snippet.setPendingPushRequestIds(pending);
        snippetRepository.save(snippet); // Save snippet with new request ID

        return new SnippetPushRequestOutDTO(savedRequest);
    }

    public PrivateSnippetResponseDTO approvePushRequest(String requestId) {
        String username = utils.getAuthenticatedUsername();

        SnippetPushRequest pushRequest = snippetPushRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Push request not found"));

        if(pushRequest.isApproved() || pushRequest.isRejected())throw new InvalidResourceStateException("Push request is already processed.");

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(pushRequest.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the snippet author can approve push requests.");
        }

        if (!snippet.getPendingPushRequestIds().contains(requestId)) {
            throw new NotFoundException("Push request not found in pending list.");
        }

        // Apply updates with fallback logic
        snippet.setTitle(pushRequest.getProposedTitle() == null || pushRequest.getProposedTitle().isEmpty() ? snippet.getTitle() : pushRequest.getProposedTitle());
        snippet.setDescription(pushRequest.getProposedDescription() == null || pushRequest.getProposedDescription().isEmpty() ? snippet.getDescription() : pushRequest.getProposedDescription());
        snippet.setTags(pushRequest.getProposedTags() == null || pushRequest.getProposedTags().isEmpty() ? snippet.getTags() : pushRequest.getProposedTags());
        snippet.setLanguage(pushRequest.getProposedLanguage() == null || pushRequest.getProposedLanguage().isEmpty() ? snippet.getLanguage() : pushRequest.getProposedLanguage());
        snippet.setCode(pushRequest.getProposedCode() == null || pushRequest.getProposedCode().isEmpty() ? snippet.getCode() : pushRequest.getProposedCode());
        snippet.setUpdatedAt(LocalDateTime.now());
        snippet.setVersion(snippet.getVersion() + 1);

        // Remove request ID from pending list
        snippet.getPendingPushRequestIds().remove(requestId);

        // Update push request
        pushRequest.setApproved(true);
        snippetPushRequestRepository.save(pushRequest);

        // Save version history
        utils.saveVersionHistory(snippet);

        return new PrivateSnippetResponseDTO(snippetRepository.save(snippet));
    }

    public void rejectPushRequest(String requestId) {
        String username = utils.getAuthenticatedUsername();

        SnippetPushRequest pushRequest = snippetPushRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Push request not found"));

        if(pushRequest.isApproved() || pushRequest.isRejected())throw new InvalidResourceStateException("Push request is already processed.");

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(pushRequest.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the snippet head can reject push requests.");
        }

        if (!snippet.getPendingPushRequestIds().contains(requestId)) {
            throw new NotFoundException("Push request not found in pending list.");
        }

        pushRequest.setRejected(true);
        snippetPushRequestRepository.save(pushRequest);

        snippet.getPendingPushRequestIds().remove(requestId);
        snippetRepository.save(snippet);
    }


    public Page<SnippetPushRequestOutDTO> getValidPushRequestsForSnippet(String snippetId, Pageable pageable) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findById(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = userPrincipal.getId();

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(userId)) {
            throw new UnauthorizedException("Only the author can view push requests for this snippet.");
        }

        return snippetPushRequestRepository.findBySnippetIdValid(snippetId, pageable)
                .map(SnippetPushRequestOutDTO::new);
    }

    public Page<SnippetPushRequestOutDTO> getMyPushRequests(Pageable pageable) { // for requester to see their own requests
        String username = utils.getAuthenticatedUsername();
        return snippetPushRequestRepository.findByRequesterUsernameOrderByRequestedAtDesc(username, pageable)
                .map(SnippetPushRequestOutDTO::new);
    }

}
