package com.focusroot.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DailyStatsPageResponse {

    private int range;
    private List<DailyStatsResponse> data;
}
