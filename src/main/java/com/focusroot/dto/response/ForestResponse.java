// File: ForestResponse.java
package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ForestResponse {
    private Long userId;
    private Integer totalTrees;
    private Integer totalFocusMinutes;
    private List<TreeItemResponse> plantedTrees;
    
    @Data
    @Builder
    public static class TreeItemResponse {
        private Long id;
        private String treeName;
        private LocalDateTime plantedAt;
        private String status;
    }
}
