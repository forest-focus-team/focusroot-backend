package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String ownerId;
    private Set<String> memberIds;
    private LocalDateTime createdAt;
}
