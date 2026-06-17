package com.focusroot.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GroupSessionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/groups/{groupId}/join")
    @SendTo("/topic/groups/{groupId}/members")
    public GroupEvent onMemberJoined(@DestinationVariable Long groupId, Principal principal) {
        log.info("User {} joined group {}", principal.getName(), groupId);
        return new GroupEvent("MEMBER_JOINED", principal.getName(), groupId);
    }

    @MessageMapping("/groups/{groupId}/session/start")
    @SendTo("/topic/groups/{groupId}/session")
    public GroupEvent onSessionStarted(@DestinationVariable Long groupId, Principal principal) {
        log.info("Group session started in group {} by {}", groupId, principal.getName());
        return new GroupEvent("SESSION_STARTED", principal.getName(), groupId);
    }

    @MessageMapping("/groups/{groupId}/session/end")
    @SendTo("/topic/groups/{groupId}/session")
    public GroupEvent onSessionEnded(@DestinationVariable Long groupId, Principal principal) {
        log.info("Group session ended in group {} by {}", groupId, principal.getName());
        return new GroupEvent("SESSION_ENDED", principal.getName(), groupId);
    }

    public record GroupEvent(String type, String username, Long groupId) {}
}
