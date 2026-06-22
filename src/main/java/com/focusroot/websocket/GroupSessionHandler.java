package com.focusroot.websocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    @MessageMapping("/groups/{groupId}/focus/start")
    @SendTo("/topic/groups/{groupId}/focus")
    public GroupEvent onFocusStarted(@DestinationVariable Long groupId, Principal principal) {
        log.info("User {} started focusing in group {}", principal.getName(), groupId);
        return new GroupEvent("FOCUS_STARTED", principal.getName(), groupId);
    }

    @MessageMapping("/groups/{groupId}/focus/end")
    @SendTo("/topic/groups/{groupId}/focus")
    public GroupEvent onFocusEnded(@DestinationVariable Long groupId, Principal principal) {
        log.info("User {} stopped focusing in group {}", principal.getName(), groupId);
        return new GroupEvent("FOCUS_ENDED", principal.getName(), groupId);
    }

    @MessageMapping("/groups/{groupId}/leave")
    @SendTo("/topic/groups/{groupId}/members")
    public GroupEvent onMemberLeft(@DestinationVariable Long groupId, Principal principal) {
        log.info("User {} left group {}", principal.getName(), groupId);
        return new GroupEvent("MEMBER_LEFT", principal.getName(), groupId);
    }

    public record GroupEvent(String type, String username, Long groupId) {}
}
