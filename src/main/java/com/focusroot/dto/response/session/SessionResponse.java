package com.focusroot.dto.response.session;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private Long treeSpeciesId;
    private Integer plannedDuration;
    private Integer actualDuration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer coinEarned;
}
