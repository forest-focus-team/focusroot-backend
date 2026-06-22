package com.focusroot.group;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String ownerUsername;
    private Integer penaltyCoins;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
