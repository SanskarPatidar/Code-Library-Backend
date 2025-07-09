package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.service.user.CollaboratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Collaborator-Related-Endpoints", description = "Operations for managing snippet collaborators")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/collaborator")
public class CollaboratorController {

    @Autowired
    private CollaboratorService collaboratorService;

    @Operation(
        summary = "Add a collaborator to a snippet",
        description = "Allows a user to add a collaborator to their private snippet by providing the snippet ID and collaborator ID."
    )
    @PostMapping("/{snippetId}/{collaboratorId}")
    public ResponseEntity<PrivateSnippetResponseDTO> addCollaborator(@PathVariable String snippetId, @PathVariable String collaboratorId) {
        return ResponseEntity.ok(collaboratorService.addCollaborator(snippetId, collaboratorId));
    }

    @Operation(
        summary = "Remove a collaborator from a snippet",
        description = "Allows a user to remove a collaborator from their private snippet by providing the snippet ID and collaborator ID."
    )
    @DeleteMapping("/{snippetId}/{collaboratorId}")
    public ResponseEntity<PrivateSnippetResponseDTO> removeCollaborator(@PathVariable String snippetId, @PathVariable String collaboratorId) {
        return ResponseEntity.ok(collaboratorService.removeCollaborator(snippetId, collaboratorId));
    }
}
