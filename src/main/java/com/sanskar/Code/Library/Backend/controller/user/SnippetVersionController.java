package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.PageResponse;
import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetversion.SnippetVersionResponseDTO;
import com.sanskar.Code.Library.Backend.service.user.SnippetVersionService;
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
public class SnippetVersionController {

    @Autowired
    private SnippetVersionService versionService;

    @Operation(
        summary = "Get versions of a snippet",
        description = "Retrieve all versions of a specific code snippet by its ID, with pagination support. This operation can be done by author or collaborator of that snippet."
    )
    @GetMapping("/{snippetId}")
    public ResponseEntity<PageResponse<SnippetVersionResponseDTO>> getVersions(
            @PathVariable String snippetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(versionService.getVersions(snippetId, PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Restore version of a snippet",
        description = "Retrieve a specific version of a code snippet by its version ID. This operation can be done by only the author of that snippet."
    )
    @PostMapping("/restore/{versionId}")
    public ResponseEntity<PrivateSnippetResponseDTO> restoreVersion(@PathVariable String versionId) {
        return ResponseEntity.ok(versionService.restoreVersion(versionId));
    }
}