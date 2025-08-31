package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.branchpushrequest.BranchPushRequestIncDTO;
import com.sanskar.Code.Library.Backend.dto.branchpushrequest.BranchPushRequestOutDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.*;
import com.sanskar.Code.Library.Backend.repository.branchpushrequest.BranchPushRequestRepository;
import com.sanskar.Code.Library.Backend.repository.version.VersionRepository;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.repository.branch.BranchRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BranchPushRequestService {

    @Autowired
    private BranchPushRequestRepository branchPushRequestRepository;

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Autowired
    private Utils utils;

    public BranchPushRequestOutDTO createPushRequest(BranchPushRequestIncDTO request) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(request.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        Branch targetBranch = branchRepository.findByIdAndDeletedFalse(request.getTargetBranchId())
                .orElseThrow(() -> new NotFoundException("Target branch not found"));

        if(targetBranch.isDeleted())
            throw new NotFoundException("Target branch is already deleted.");

        String userId = utils.getAuthenticatedUserId();

        if (!snippet.getCollaborators().containsKey(userId)) {
            throw new UnauthorizedException("You are not allowed to push to this snippet.");
        }

        BranchPushRequest savedRequest = branchPushRequestRepository.save(
                BranchPushRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .snippetId(request.getSnippetId())
                        .targetBranchId(targetBranch.getId())
                        .requestedBy(username)
                        .requestedAt(LocalDateTime.now())
                        .message(request.getMessage())
                        .proposedCode(request.getProposedCode())
                        .proposedTitle(request.getProposedTitle())
                        .proposedDescription(request.getProposedDescription())
                        .proposedTags(request.getProposedTags())
                        .proposedLanguage(request.getProposedLanguage())
                        .build()
                );

        return new BranchPushRequestOutDTO(savedRequest);
    }

    @Transactional
    public BranchPushRequestOutDTO approvePushRequest(String requestId) {
        String username = utils.getAuthenticatedUsername();

        BranchPushRequest pushRequest = branchPushRequestRepository.findByIdValid(requestId)
                .orElseThrow(() -> new NotFoundException("Push request not found"));

        Branch targetBranch = branchRepository.findByIdAndDeletedFalse(pushRequest.getTargetBranchId())
                .orElseThrow(() -> new NotFoundException("Target branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(targetBranch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the snippet author can approve push requests.");
        }

        Version version = Version.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .branchId(pushRequest.getTargetBranchId())
                .version(targetBranch.getLatestVersion() + 1)
                .createdAt(LocalDateTime.now())
                .message(pushRequest.getMessage())
                .description(pushRequest.getProposedDescription())
                .code(pushRequest.getProposedCode())
                .language(pushRequest.getProposedLanguage())
                .build();

        versionRepository.save(version);

        if(targetBranch.getBranchName().equals("main")) {
            snippet.setMainUpdatedAt(LocalDateTime.now());
            snippet.setLatestCode(pushRequest.getProposedCode());
            snippet.setLatestDescription(pushRequest.getProposedDescription());
            snippet.setLatestLanguage(pushRequest.getProposedLanguage());
            snippetRepository.save(snippet);
        }

        // Update push request
        pushRequest.setStatus(BranchPushRequestStatus.APPROVED);
        branchPushRequestRepository.save(pushRequest);

        return new BranchPushRequestOutDTO(pushRequest);
    }

    @Transactional
    public void rejectPushRequest(String requestId) {
        String username = utils.getAuthenticatedUsername();

        BranchPushRequest pushRequest = branchPushRequestRepository.findByIdValid(requestId)
                .orElseThrow(() -> new NotFoundException("Push request not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(pushRequest.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username))
            throw new UnauthorizedException("Only the snippet head can reject push requests.");

        pushRequest.setStatus(BranchPushRequestStatus.REJECTED);
        branchPushRequestRepository.save(pushRequest);
    }


    public Page<BranchPushRequestOutDTO> getValidPushRequestsForSnippet(String snippetId, Pageable pageable) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        String userId = utils.getAuthenticatedUserId();

        if (!snippet.getAuthorName().equals(username))
            throw new UnauthorizedException("Only the author can view push requests for this snippet.");

        return branchPushRequestRepository.findAllBySnippetIdAndValidAsPage(snippetId, pageable)
                .map(BranchPushRequestOutDTO::new);
    }

    public Page<BranchPushRequestOutDTO> getMyPushRequests(Pageable pageable) { // for requester to see their own requests
        String username = utils.getAuthenticatedUsername();
        return branchPushRequestRepository.findByRequestedByOrderByRequestedAtDesc(username, pageable)
                .map(BranchPushRequestOutDTO::new);
    }

}
