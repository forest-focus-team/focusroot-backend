package com.focusroot.service;

import com.focusroot.dto.request.CreateGroupRequest;
import com.focusroot.dto.request.JoinGroupRequest;
import com.focusroot.dto.response.GroupResponse;
import com.focusroot.entity.Group;
import com.focusroot.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupResponse createGroup(CreateGroupRequest request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new RuntimeException("Group name already exists");
        }

        Group group = Group.builder()
                .name(request.getName())
                .ownerId(request.getOwnerId())
                .build();

        group.getMemberIds().add(request.getOwnerId());
        Group saved = groupRepository.save(group);
        return mapToResponse(saved);
    }

    public GroupResponse joinGroup(Long groupId, JoinGroupRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        group.getMemberIds().add(request.getUserId());
        Group saved = groupRepository.save(group);
        return mapToResponse(saved);
    }

    private GroupResponse mapToResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .ownerId(group.getOwnerId())
                .memberIds(group.getMemberIds())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
