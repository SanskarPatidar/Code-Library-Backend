package com.sanskar.Code.Library.Backend.controller.moderator;

import com.sanskar.Code.Library.Backend.service.moderator.ModeratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Moderator-Endpoints(MODERATOR ONLY)", description = "Moderator operations for managing public snippets")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/moderator")
@PreAuthorize("hasRole('MODERATOR')")
public class ModeratorController {

    @Autowired
    private ModeratorService moderatorService;

    @Operation(
        summary = "Delete a public snippet",
        description = "Deletes a public snippet by its snippetId. Only accessible by MODERATOR."
    )
    @DeleteMapping("/delete-snippet/{snippetId}")
    public ResponseEntity<String> deletePublicSnippet(@PathVariable String snippetId) {
        moderatorService.deletePublicSnippet(snippetId);
        return ResponseEntity.ok("Snippet deleted successfully by MODERATOR.");
    }
}
