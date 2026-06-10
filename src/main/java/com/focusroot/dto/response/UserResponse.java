package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Integer coin;
    private Integer totalFocusMinutes;
    private LocalDateTime createdAt;
}
