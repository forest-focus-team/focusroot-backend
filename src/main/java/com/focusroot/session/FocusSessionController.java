package com.focusroot.session;

import com.focusroot.auth.UserPrincipal;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionService sessionService;

    @PostMapping("/start")
    public ResponseEntity<SessionResponse> start(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody StartSessionRequest req) {
        return ResponseEntity.ok(sessionService.startSession(user.getId(), req));
    }

    @PostMapping("/end")
    public ResponseEntity<SessionResponse> end(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody EndSessionRequest req) {
        return ResponseEntity.ok(sessionService.endSession(user.getId(), req));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<SessionResponse>> history(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(sessionService.getHistory(user.getId(), page));
    }
}
