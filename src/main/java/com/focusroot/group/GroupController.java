package com.focusroot.group;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.focusroot.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Group", description = "Focus group management")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Create a new focus group")
    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateGroupRequest request) {
        FocusGroup group = groupService.createGroup(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Group created", groupService.mapGroupToResponse(group)));
    }

    @Operation(summary = "Join an existing group")
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> joinGroup(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        GroupMember member = groupService.joinGroup(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("Joined group", groupService.mapMemberToResponse(member)));
    }

    @Operation(summary = "Get active members of a group with current focusing status")
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getGroupMembers(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(members));
    }

    @Operation(summary = "Get all groups current user belongs to")
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(
            @AuthenticationPrincipal UserDetails principal) {
        List<FocusGroup> groups = groupService.getMyGroups(principal.getUsername());
        List<GroupResponse> response = groups.stream()
                .map(groupService::mapGroupToResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
