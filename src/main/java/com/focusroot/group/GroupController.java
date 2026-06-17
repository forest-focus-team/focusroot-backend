package com.focusroot.group;

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

@Tag(name = "Group", description = "Focus group management")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Create a new focus group")
    @PostMapping
    public ResponseEntity<ApiResponse<FocusGroup>> createGroup(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CreateGroupRequest request) {
        FocusGroup group = groupService.createGroup(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Group created", group));
    }

    @Operation(summary = "Join an existing group")
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<GroupMember>> joinGroup(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        GroupMember member = groupService.joinGroup(principal.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("Joined group", member));
    }

    @Operation(summary = "Get all groups current user belongs to")
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<FocusGroup>>> getMyGroups(
            @AuthenticationPrincipal UserDetails principal) {
        List<FocusGroup> groups = groupService.getMyGroups(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(groups));
    }
}
