package com.sanskar.Code.Library.Backend.controller.user;

import com.sanskar.Code.Library.Backend.dto.PageResponse;
import com.sanskar.Code.Library.Backend.dto.snippet.PrivateSnippetResponseDTO;
import com.sanskar.Code.Library.Backend.dto.snippetpushrequest.SnippetPushRequestIncDTO;
import com.sanskar.Code.Library.Backend.dto.snippetpushrequest.SnippetPushRequestOutDTO;
import com.sanskar.Code.Library.Backend.service.user.SnippetPushRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Snippet-Push-Request-Endpoints", description = "Operations for creating, approving, rejecting, and retrieving snippet push requests")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/push-request")
public class SnippetPushRequestController {
    @Autowired
    private SnippetPushRequestService pushService;

    @Operation(
        summary = "Create a new snippet push request",
        description = "Creates a new push request for a snippet. The request must be valid and the user must have permission to create it (i.e. must be a collaborator of that snippet)."
    )
    @PostMapping
    public ResponseEntity<SnippetPushRequestOutDTO> createPushRequest(@RequestBody @Valid SnippetPushRequestIncDTO pushRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pushService.createPushRequest(pushRequest));
    }

    @Operation(
        summary = "Approve a snippet push request",
        description = "Approves a push request for a snippet. The user must have permission to approve the request (i.e. must be the author of that snippet)."
    )
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<PrivateSnippetResponseDTO> approvePushRequest(@PathVariable String requestId) {
        return ResponseEntity.ok(pushService.approvePushRequest(requestId));
    }

    @Operation(
        summary = "Reject a snippet push request",
        description = "Rejects a push request for a snippet. The user must have permission to reject the request (i.e. must be the author of that snippet)."
    )
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectPushRequest(@PathVariable String requestId) {
        pushService.rejectPushRequest(requestId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Get all valid push requests for a snippet",
        description = "Retrieves all valid push requests for a specific snippet. The user must be author or collaborator of that snippet."
    )
    @GetMapping("/snippet/{snippetId}")
    public ResponseEntity<PageResponse<SnippetPushRequestOutDTO>> getValidPushRequestsForSnippet(
            @PathVariable String snippetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(pushService.getValidPushRequestsForSnippet(snippetId, PageRequest.of(page, size))));
    }

    @Operation(
        summary = "Get all push requests created by the user",
        description = "Retrieves all push requests created by the authenticated user."
    )
    @GetMapping("/my")
    public ResponseEntity<PageResponse<SnippetPushRequestOutDTO>> getMyPushRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new PageResponse<>(pushService.getMyPushRequests(PageRequest.of(page, size))));
    }
}
