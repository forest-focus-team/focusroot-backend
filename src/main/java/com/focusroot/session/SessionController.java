package com.focusroot.session;

import com.focusroot.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Focus Session", description = "Focus timer session management")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "Start a new focus session")
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<FocusSession>> startSession(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody StartSessionRequest request) {
        FocusSession session = sessionService.startSession(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Session started", session));
    }

    @Operation(summary = "End a focus session (complete or give up)")
    @PostMapping("/{id}/end")
    public ResponseEntity<ApiResponse<FocusSession>> endSession(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean giveUp) {
        FocusSession session = sessionService.endSession(principal.getUsername(), id, giveUp);
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @Operation(summary = "Get session history for current user")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<FocusSession>>> getHistory(
            @AuthenticationPrincipal UserDetails principal) {
        List<FocusSession> history = sessionService.getHistory(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
