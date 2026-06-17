package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerUsername;
    private Boolean isActive;
    private Integer penaltyCoins;
    private LocalDateTime createdAt;
}
