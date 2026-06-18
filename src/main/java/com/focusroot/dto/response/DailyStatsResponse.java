package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyStatsResponse {

    private LocalDate date;
    private int minutes;
    private int trees;
    private int sessionCount;
    private int successCount;
}
