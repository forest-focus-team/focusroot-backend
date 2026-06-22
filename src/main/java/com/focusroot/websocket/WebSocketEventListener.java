package com.focusroot.websocket;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.focusroot.group.GroupMember;
import com.focusroot.group.GroupMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final GroupMemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
        if (username == null) {
            return;
        }

        log.info("User {} disconnected", username);

        List<GroupMember> activeMembers = memberRepository.findByUsernameAndStatus(username, GroupMember.Status.ACTIVE);
        for (GroupMember member : activeMembers) {
            messagingTemplate.convertAndSend(
                    "/topic/groups/" + member.getGroup().getId() + "/members",
                    new GroupSessionHandler.GroupEvent("MEMBER_LEFT", username, member.getGroup().getId())
            );
            log.info("Broadcast MEMBER_LEFT for user {} in group {}", username, member.getGroup().getId());
        }
    }
}
