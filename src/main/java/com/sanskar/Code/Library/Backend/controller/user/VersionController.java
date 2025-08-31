package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.PageResponse;
import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionRequestDTO;
import com.sanskar.Code.Library.Backend.dto.branchversion.BranchVersionResponseDTO;
import com.sanskar.Code.Library.Backend.service.user.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Snippet-Version-Endpoints", description = "Operations for managing versions of code snippets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/snippet-version")
public class VersionController {

    @Autowired
    private VersionService versionService;

    @Operation(
        summary = "Get versions of a snippet",
        description = "Retrieve all versions of a specific code snippet by its ID, with pagination support. This operation can be done by author or collaborator of that snippet."
    )
    @GetMapping("/{snippetId}")
    public ResponseEntity<PageResponse<BranchVersionResponseDTO>> getVersions(
            @PathVariable String snippetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(versionService.getVersions(snippetId, PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Get a specific version by ID",
        description = "Retrieve a specific version of a code snippet by its version ID. This operation can be done by author or collaborator of that snippet."
    )
    @GetMapping("/get/{versionId}") // pull functionality
    public ResponseEntity<BranchVersionResponseDTO> getVersionById(@PathVariable String versionId) {
        return ResponseEntity.ok(versionService.getVersionById(versionId));
    }

    @Operation(
        summary = "Restore version of a snippet",
        description = "Retrieve a specific version of a code snippet by its version ID. This operation can be done by only the author of that snippet."
    )
    @PostMapping("/restore/{versionId}")
    public ResponseEntity<BranchVersionResponseDTO> restoreVersion(@PathVariable String versionId) {
        return ResponseEntity.ok(versionService.restoreVersionOnSameBranch(versionId));
    }

    @Operation(
        summary = "Push version of a snippet",
        description = "Push a new version of a code snippet to the specified branch. This operation can be done by only the author of that snippet."
    )
    @PostMapping("/{snippetId}/push")
    public ResponseEntity<BranchVersionResponseDTO> pushVersion(@RequestBody BranchVersionRequestDTO branchRequest) {
        return ResponseEntity.ok(versionService.pushVersion(branchRequest));
    }
}