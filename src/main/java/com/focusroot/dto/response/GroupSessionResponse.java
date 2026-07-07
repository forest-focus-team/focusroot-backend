package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupSessionResponse {
    private Long id;
    private Long groupId;
    private String status;
    private String startedBy;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
