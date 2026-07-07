package com.focusroot.group;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupMemberResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private String username;
    private GroupMember.Status status;
    private LocalDateTime joinedAt;
}
