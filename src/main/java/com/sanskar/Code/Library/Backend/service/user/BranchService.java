package com.sanskar.Code.Library.Backend.service.user;

import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.CreateBranchRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.CreateBranchResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.MergeRequestDTO;
import com.sanskar.Code.Library.Backend.exception.InvalidResourceStateException;
import com.sanskar.Code.Library.Backend.exception.NotFoundException;
import com.sanskar.Code.Library.Backend.exception.UnauthorizedException;
import com.sanskar.Code.Library.Backend.model.Version;
import com.sanskar.Code.Library.Backend.model.Snippet;
import com.sanskar.Code.Library.Backend.model.Branch;
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
import java.util.List;
import java.util.UUID;

@Service
public class BranchService {

    @Autowired
    private Utils utils;

    @Autowired
    private SnippetRepository snippetRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private VersionRepository versionRepository;

    @Transactional
    public CreateBranchResponseDTO createBranch(CreateBranchRequestDTO createRequest){
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(createRequest.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if(!username.equals(snippet.getAuthorName()))
            throw new UnauthorizedException("You are not authorized to create a branch for this snippet");

        if (branchRepository.existsBySnippetIdAndBranchName(createRequest.getSnippetId(), createRequest.getBranchName()))
            throw new InvalidResourceStateException("Branch name already exists for this snippet.");

        Version forkedVersion = versionRepository.findByIdAndDeletedFalse(createRequest.getSourceVersionId())
                .orElseThrow(() -> new NotFoundException("Source version not found"));

        Branch forkedBranch = branchRepository.findByIdAndDeletedFalse(forkedVersion.getBranchId())
                .orElseThrow(() -> new NotFoundException("Source branch not found"));

        Branch branch = Branch.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(createRequest.getSnippetId())
                .branchName(createRequest.getBranchName())
                .createdAt(LocalDateTime.now())
                .sourceBranchId(createRequest.getSourceBranchId())
                .sourceVersionId(forkedVersion.getId())
                .sourceVersion(forkedVersion.getVersion())
                .latestVersion(0)
                .build();

        branchRepository.save(branch);

        Version version = Version.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(createRequest.getSnippetId())
                .branchId(branch.getId())
                .version(0)
                .createdAt(LocalDateTime.now())
                .message("Branch created from " + forkedBranch.getBranchName() + " at version " + forkedVersion.getVersion())
                .description(forkedVersion.getDescription())
                .code(forkedVersion.getCode())
                .language(forkedVersion.getLanguage())
                .build();

        versionRepository.save(version);

        return new CreateBranchResponseDTO(branch);
    }

    public Page<Branch> getBranches(String snippetId, Pageable pageable) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(snippetId)
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!username.equals(snippet.getAuthorName())) {
            throw new UnauthorizedException("You are not authorized to view branches for this snippet");
        }

        return branchRepository.findAllBySnippetIdAndDeletedFalse(snippetId, pageable);
    }

    @Transactional
    public void deleteBranch(String branchId) {
        String username = utils.getAuthenticatedUsername();

        Branch branch = branchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(branch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (!username.equals(snippet.getAuthorName())) {
            throw new UnauthorizedException("You are not authorized to delete this branch");
        }

        if(branch.getBranchName().equals("main"))
            throw new InvalidResourceStateException("Cannot delete the main branch");

        if (branch.isDeleted())
            throw new InvalidResourceStateException("Branch is already deleted");

        deleteBranchesAndVersionsRecursively(branch);
    }

    // sadly, no cascade deletion in spring MongoDB
    private void deleteBranchesAndVersionsRecursively(Branch branch) {
        branch.setDeleted(true);
        branchRepository.save(branch);

        List<Version> versions = versionRepository.findAllByBranchId(branch.getId());
        for (Version version : versions) {
            version.setDeleted(true);
        }
        versionRepository.saveAll(versions);

        List<Branch> childBranches = branchRepository.findAllBySourceBranchId(branch.getId());
        for (Branch childBranch : childBranches) {
            if (!childBranch.isDeleted()) {
                deleteBranchesAndVersionsRecursively(childBranch);
            }
        }
    }

    public CreateBranchResponseDTO renameBranch(String branchId, String newName) {
        String username = utils.getAuthenticatedUsername();

        Branch branch = branchRepository.findByIdAndDeletedFalse(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(branch.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        if (branchRepository.existsBySnippetIdAndBranchName(snippet.getId(), newName))
            throw new InvalidResourceStateException("Branch name already exists for this snippet.");

        if (!username.equals(snippet.getAuthorName()))
            throw new UnauthorizedException("You are not authorized to rename this branch");


        if (branch.getBranchName().equals("main"))
            throw new InvalidResourceStateException("Cannot rename the main branch");


        branch.setBranchName(newName);
        branchRepository.save(branch);

        return new CreateBranchResponseDTO(branch);
    }

    @Transactional
    public BranchVersionResponseDTO mergeBranch(MergeRequestDTO mergeRequest) {
        String username = utils.getAuthenticatedUsername();

        Snippet snippet = snippetRepository.findByIdAndDeletedFalse(mergeRequest.getSnippetId())
                .orElseThrow(() -> new NotFoundException("Snippet not found"));

        Branch targetBranch = branchRepository.findByIdAndDeletedFalse(mergeRequest.getTargetBranchId())
                .orElseThrow(() -> new NotFoundException("Target branch not found"));

        Branch sourceBranch = branchRepository.findByIdAndDeletedFalse(mergeRequest.getSourceBranchId())
                .orElseThrow(() -> new NotFoundException("Source branch not found"));

        if(!username.equals(snippet.getAuthorName()) || !targetBranch.getSnippetId().equals(snippet.getId()) || !sourceBranch.getSnippetId().equals(snippet.getId()))
            throw new UnauthorizedException("You are not authorized to merge branches for this snippet");

        if(targetBranch.isDeleted() || sourceBranch.isDeleted())
            throw new InvalidResourceStateException("Cannot merge deleted branches");

        Version latestSourceVersion = versionRepository.findTopByBranchIdOrderByVersionDesc(sourceBranch.getId())
                .orElseThrow(() -> new NotFoundException("No versions found for source branch"));

        Version latestTargetVersion = Version.builder()
                .id(UUID.randomUUID().toString())
                .snippetId(snippet.getId())
                .branchId(targetBranch.getId())
                .version(targetBranch.getLatestVersion() + 1)
                .createdAt(LocalDateTime.now())
                .message("Merge from " + sourceBranch.getBranchName() + " at version " + latestSourceVersion.getVersion())
                .description(latestSourceVersion.getDescription())
                .code(latestSourceVersion.getCode())
                .language(latestSourceVersion.getLanguage())
                .build();

        if(targetBranch.getBranchName().equals("main")) {
            snippet.setLatestCode(latestSourceVersion.getCode());
            snippet.setLatestDescription(latestSourceVersion.getDescription());
            snippet.setLatestLanguage(latestSourceVersion.getLanguage());
            snippet.setMainUpdatedAt(LocalDateTime.now());
            snippetRepository.save(snippet);
        }

        targetBranch.setLatestVersion(targetBranch.getLatestVersion() + 1);

        branchRepository.save(targetBranch);
        versionRepository.save(latestTargetVersion);
        return new BranchVersionResponseDTO(latestTargetVersion);
    }
}
