// File: SessionResponse.java
package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private Long id;
    private Long treeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private String status; 
    private Integer earnedCoins; 
}
