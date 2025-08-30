package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionRequestDTO;
import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionResponseDTO;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Version;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.model.Branch;
import com.sanskar.Code.Library.Backend.repository.branchversion.BranchVersionRepository;
import com.sanskar.Code.Library.Backend.repository.snippet.SnippetRepository;
import com.sanskar.Code.Library.Backend.repository.snippetbranch.SnippetBranchRepository;
import com.sanskar.Code.Library.Backend.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VersionService {

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private BranchVersionRepository branchVersionRepository;

    @Autowired
    private SnippetBranchRepository snippetBranchRepository;

    @Autowired
    private Utils utils;


    public Page<BranchVersionResponseDTO> getVersions(String branchId, Pageable pageable) {
        String username = utils.getAuthenticatedUsername();

        Branch branch = snippetBranchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(branch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(username)) {
            throw new UnauthorizedException("Not authorized to view version history.");
        }

        return branchVersionRepository.findByBranchIdOrderByVersionDesc(branchId, pageable)
                .map(BranchVersionResponseDTO::new);
    }

    // pull functionality
    public BranchVersionResponseDTO getVersionById(String versionId) {
        String username = utils.getAuthenticatedUsername();

        Version version = branchVersionRepository.findByIdAndDeletedFalse(versionId)
                .orElseThrow(() -> new NotFoundException("Version not found"));

        Branch branch = snippetBranchRepository.findByIdAndDeletedFalse(version.getBranchId())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(branch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username) && !snippet.getCollaborators().containsKey(username))
            throw new UnauthorizedException("Not authorized to view version details.");

        return new BranchVersionResponseDTO(version);
    }

    @Transactional
    public BranchVersionResponseDTO restoreVersionOnSameBranch(String versionId) {
        String username = utils.getAuthenticatedUsername();

        Version version = branchVersionRepository.findByIdAndDeletedFalse(versionId)
                .orElseThrow(() -> new NotFoundException("Version not found"));

        Branch branch = snippetBranchRepository.findByIdAndDeletedFalse(version.getBranchId())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(version.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username)) {
            throw new UnauthorizedException("Only the author can restore versions.");
        }

        Version latestVersion = Version.builder()
                .snippetId(version.getSnippetId())
                .branchId(version.getBranchId())
                .version(branch.getLatestVersion() + 1)
                .createdAt(LocalDateTime.now())
                .message("Restored to version " + version.getVersion())
                .description(version.getDescription())
                .language(version.getLanguage())
                .code(version.getCode())
                .build();

        branchVersionRepository.save(latestVersion);

        branch.setLatestVersion(branch.getLatestVersion() + 1);
        branchVersionRepository.save(latestVersion);

        if(branch.getBranchName().equals("main")) {
            snippet.setMainUpdatedAt(LocalDateTime.now());
            snippet.setLatestCode(latestVersion.getCode());
            snippet.setLatestDescription(latestVersion.getDescription());
            snippet.setLatestLanguage(latestVersion.getLanguage());
            snippetRepository.save(snippet);
        }

        return new BranchVersionResponseDTO(latestVersion);
    }

    @Transactional
    public BranchVersionResponseDTO pushVersion(BranchVersionRequestDTO versionRequest) {
        String username = utils.getAuthenticatedUsername();

        Branch branch = snippetBranchRepository.findByIdAndDeletedFalse(versionRequest.getBranchId())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(branch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!snippet.getAuthorName().equals(username))
            throw new UnauthorizedException("Only the author can push versions.");

        Version version = Version.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .branchId(branch.getId())
                .version(branch.getLatestVersion() + 1)
                .createdAt(LocalDateTime.now())
                .message(versionRequest.getMessage())
                .description(versionRequest.getDescription())
                .code(versionRequest.getCode())
                .language(versionRequest.getLanguage())
                .build();

        branchVersionRepository.save(version);

        branch.setLatestVersion(branch.getLatestVersion() + 1);
        snippetBranchRepository.save(branch);

        if(branch.getBranchName().equals("main")) {
            snippet.setMainUpdatedAt(LocalDateTime.now());
            snippet.setLatestCode(version.getCode());
            snippet.setLatestDescription(version.getDescription());
            snippet.setLatestLanguage(version.getLanguage());
            snippetRepository.save(snippet);
        }

        return new BranchVersionResponseDTO(version);
    }
}
