package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsSummaryResponse {

    private long totalSessions;
    private long successSessions;
    private long failedSessions;
    private double successRate;

    private int totalFocusMinutes;
    private int currentCoins;
    private long totalCoinsEarned;

    private long totalTreesPlanted;
    private long aliveTreesCount;
    private long deadTreesCount;

    private int currentStreak;
}
