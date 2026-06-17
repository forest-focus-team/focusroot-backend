package com.focusroot.dto.response;

import com.focusroot.entity.enums.GroupSessionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupSessionResponse {
    private Long id;
    private Long groupId;
    private GroupSessionStatus status;
    private String startedBy;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
