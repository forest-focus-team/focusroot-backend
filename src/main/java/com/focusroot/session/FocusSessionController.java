package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class FocusSessionController {
    
    private final FocusSessionService sessionService;
    // Lưu ý: Đảm bảo class UserDetailsImpl được import đúng từ Auth module của nhóm bạn

    @PostMapping("/start")
    public ResponseEntity<SessionResponse> start(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody StartSessionRequest req) {
        return ResponseEntity.ok(sessionService.startSession(user.getId(), req));
    }

    @PostMapping("/end")
    public ResponseEntity<SessionResponse> end(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody EndSessionRequest req) {
        return ResponseEntity.ok(sessionService.endSession(user.getId(), req));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<SessionResponse>> history(@AuthenticationPrincipal UserDetailsImpl user, @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(sessionService.getHistory(user.getId(), page));
    }
}
