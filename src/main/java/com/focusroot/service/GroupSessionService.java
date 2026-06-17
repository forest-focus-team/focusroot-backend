package com.focusroot.service;

import com.focusroot.dto.response.GroupSessionResponse;
import com.focusroot.entity.Group;
import com.focusroot.entity.GroupSession;
import com.focusroot.entity.enums.GroupSessionStatus;
import com.focusroot.repository.GroupRepository;
import com.focusroot.repository.GroupSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GroupSessionService {

    private final GroupSessionRepository groupSessionRepository;
    private final GroupRepository groupRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GroupSessionResponse startSession(Long groupId, String userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        GroupSession session = GroupSession.builder()
                .group(group)
                .status(GroupSessionStatus.ACTIVE)
                .startedBy(userId)
                .startedAt(LocalDateTime.now())
                .build();

        GroupSession saved = groupSessionRepository.save(session);
        GroupSessionResponse response = mapToResponse(saved);

        messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
        return response;
    }

    public GroupSessionResponse endSession(Long groupId, String userId) {
        GroupSession session = groupSessionRepository
                .findTopByGroupIdAndStatusOrderByStartedAtDesc(groupId, GroupSessionStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active session found"));

        session.setStatus(GroupSessionStatus.ENDED);
        session.setEndedAt(LocalDateTime.now());

        GroupSession saved = groupSessionRepository.save(session);
        GroupSessionResponse response = mapToResponse(saved);

        messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
        return response;
    }

    private GroupSessionResponse mapToResponse(GroupSession session) {
        return GroupSessionResponse.builder()
                .id(session.getId())
                .groupId(session.getGroup().getId())
                .status(session.getStatus())
                .startedBy(session.getStartedBy())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .build();
    }
}
