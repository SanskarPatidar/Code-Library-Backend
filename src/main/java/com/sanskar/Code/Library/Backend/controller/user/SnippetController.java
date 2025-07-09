package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.PageResponse;
import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.PublicSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetCreateRequestDTO;
import com.sanskar.Code.Library.Backend.dto.snippet.SnippetUpdateRequestDTO;
import com.sanskar.Code.Library.Backend.service.user.SnippetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Snippet-Endpoints", description = "Operations for creating, updating, retrieving, and deleting code snippets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/snippet")
public class SnippetController {

    @Autowired
    private SnippetService snippetService;

    @Operation(
        summary = "Create a new code snippet",
        description = "Creates a new code snippet with the provided details. Returns the created snippet."
    )
    @PostMapping
    public ResponseEntity<PrivateSnippetResponseDTO> createSnippet(@RequestBody @Valid SnippetCreateRequestDTO snippetRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(snippetService.createSnippet(snippetRequest)); // 201 Created
    }

    @Operation(
        summary = "Update an existing code snippet",
        description = "Updates an existing code snippet with the provided details. Returns the updated snippet."
    )
    @PutMapping
    public ResponseEntity<PrivateSnippetResponseDTO> updateSnippet(@RequestBody @Valid SnippetUpdateRequestDTO updatedSnippet) {
        return ResponseEntity.ok(snippetService.updateSnippetDirectly(updatedSnippet));
    }

    @Operation(
        summary = "Get a code snippet by ID",
        description = "Retrieves a code snippet by its snippetId. Returns the snippet details."
    )
    @GetMapping("/{snippetId}")
    public ResponseEntity<PrivateSnippetResponseDTO> getSnippetById(@PathVariable String snippetId) {
        return ResponseEntity.ok(snippetService.getSnippetById(snippetId));
    }

    @Operation(
        summary = "Get all code snippets created by the user",
        description = "Retrieves all code snippets created by the authenticated user. Supports pagination."
    )
    @GetMapping("/get")
    public ResponseEntity<PageResponse<PrivateSnippetResponseDTO>> getMySnippets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(snippetService.getMySnippets(PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Get all code snippets the user is collaborating on",
        description = "Retrieves all code snippets the authenticated user is collaborating on. Supports pagination."
    )
    @GetMapping("/collaborating")
    public ResponseEntity<PageResponse<PrivateSnippetResponseDTO>> getCollaboratingSnippets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(snippetService.getCollaboratingSnippets(PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Delete a code snippet by ID",
        description = "Deletes a code snippet by its snippetId. Returns 204 No Content on success."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable String id) {
        snippetService.deleteSnippet(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @Operation(
        summary = "Toggle the public visibility of a code snippet",
        description = "Change the public visibility of a code snippet and Returns the updated snippet. Default visibility is false (private)."
    )
    @PatchMapping("/visibility/{id}")
    public ResponseEntity<PrivateSnippetResponseDTO> togglePublicVisibility(@PathVariable String id, @RequestParam(defaultValue = "false") boolean isPublic) {
        return ResponseEntity.ok(snippetService.togglePublicVisibility(id, isPublic));
    }

    @Operation(
        summary = "Get public code snippets",
        description = "Retrieves public code snippets based on optional tags sent as list of tags. Supports pagination."
    )
    @GetMapping("/public") // Get usually do not carry a body, here structure will be --> GET /public?tags=java&tags=spring&tags=backend
    public ResponseEntity<PageResponse<PublicSnippetResponseDTO>> getPublicSnippets(
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(snippetService.getPublicSnippets(tags, PageRequest.of(page, size))));
    }
}


