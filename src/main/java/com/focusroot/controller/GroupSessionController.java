package com.focusroot.controller;

import com.focusroot.dto.response.GroupSessionResponse;
import com.focusroot.service.GroupSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupSessionController {

    private final GroupSessionService groupSessionService;

    @PostMapping("/{groupId}/session/start")
    public ResponseEntity<GroupSessionResponse> startSession(
            @PathVariable Long groupId,
            @RequestParam String userId) {
        return ResponseEntity.ok(groupSessionService.startSession(groupId, userId));
    }

    @PostMapping("/{groupId}/session/end")
    public ResponseEntity<GroupSessionResponse> endSession(
            @PathVariable Long groupId,
            @RequestParam String userId) {
        return ResponseEntity.ok(groupSessionService.endSession(groupId, userId));
    }
}
