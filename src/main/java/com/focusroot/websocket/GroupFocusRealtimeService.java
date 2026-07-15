package com.focusroot.websocket;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.focusroot.group.GroupMember;
import com.focusroot.group.GroupMemberRepository;
import com.focusroot.session.FocusSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupFocusRealtimeService {

    private final GroupMemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastFocusStatusChangeAfterCommit(String username, FocusSession.Status sessionStatus) {
        Runnable broadcastAction = () -> broadcastFocusStatusChange(username, sessionStatus);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    broadcastAction.run();
                }
            });
            return;
        }

        broadcastAction.run();
    }

    private void broadcastFocusStatusChange(String username, FocusSession.Status sessionStatus) {
        boolean focusing = sessionStatus == FocusSession.Status.IN_PROGRESS;
        List<GroupMember> activeMembers = memberRepository.findByUsernameAndStatus(username, GroupMember.Status.ACTIVE);

        for (GroupMember member : activeMembers) {
            Long groupId = member.getGroup().getId();
            messagingTemplate.convertAndSend(
                    "/topic/groups/" + groupId + "/focus",
                    GroupSessionHandler.GroupEvent.focusStatusChanged(
                            username,
                            groupId,
                            sessionStatus.name(),
                            focusing
                    )
            );
            log.info("Broadcast focus status {} for user {} in group {}", sessionStatus, username, groupId);
        }
    }
}
