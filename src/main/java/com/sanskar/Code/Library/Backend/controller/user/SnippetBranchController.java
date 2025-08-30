package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.CreateBranchRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.CreateBranchResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetbranch.MergeRequestDTO;
import com.sanskar.Code.Library.Backend.model.Branch;
import com.sanskar.Code.Library.Backend.service.user.BranchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Snippet-Branch-Endpoints", description = "Operations for managing branches of code snippets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/snippet-branch")
public class SnippetBranchController {

    @Autowired
    private BranchService branchService;

    @Operation(
        summary = "Create a new branch for a code snippet",
        description = "Creates a new branch for the specified code snippet. Returns the created branch details."
    )
    @PostMapping
    public ResponseEntity<CreateBranchResponseDTO> createBranch(@Valid @RequestBody CreateBranchRequestDTO createRequest) {
        return ResponseEntity.ok(branchService.createBranch(createRequest));
    }

    @Operation(
        summary = "Get all branches of a code snippet",
        description = "Retrieves all branches for the specified code snippet. Supports pagination."
    )
    @GetMapping("/get")
    public ResponseEntity<Page<Branch>> getBranchesBySnippetId(
            @RequestParam String snippetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(branchService.getBranches(snippetId, PageRequest.of(page, size)));
    }

    @Operation(
        summary = "Get a specific branch by ID",
        description = "Retrieves the details of a specific branch by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable String id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Merge a branch into another",
        description = "Merges the specified branch into the target branch. Returns the details of the merged branch."
    )
    @PatchMapping("rename/{id}/{newBranchName}")
    public CreateBranchResponseDTO renameBranch(
            @PathVariable String id,
            @RequestParam String newBranchName
    ) {
        return branchService.renameBranch(id, newBranchName);
    }

    @Operation(
        summary = "Merge a branch into another",
        description = "Merges the specified branch into the target branch. Returns the details of the merged branch."
    )
    @PostMapping("/merge")
    public ResponseEntity<BranchVersionResponseDTO> mergeBranch(
            @RequestBody @Valid MergeRequestDTO mergeRequest
    ) {
        return ResponseEntity.ok(branchService.mergeBranch(mergeRequest));
    }
}
