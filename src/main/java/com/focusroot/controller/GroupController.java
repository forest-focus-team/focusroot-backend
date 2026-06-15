package com.focusroot.controller;

import com.focusroot.dto.request.CreateGroupRequest;
import com.focusroot.dto.request.JoinGroupRequest;
import com.focusroot.dto.response.GroupResponse;
import com.focusroot.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @RequestBody JoinGroupRequest request) {
        return ResponseEntity.ok(groupService.joinGroup(groupId, request));
    }
}
