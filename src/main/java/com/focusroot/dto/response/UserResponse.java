// File: UserResponse.java
package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Integer totalCoins; 
    private String tier; 
}
