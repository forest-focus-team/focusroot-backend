// File: GroupResponse.java
package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String groupName;
    private String passcode;
    private Integer targetDurationMinutes;
    private Integer currentMemberCount;
    private String status; 
}
