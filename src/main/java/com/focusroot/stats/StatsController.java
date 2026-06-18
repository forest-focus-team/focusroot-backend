package com.focusroot.stats;

import com.focusroot.auth.UserPrincipal;
import com.focusroot.common.ApiResponse;
import com.focusroot.dto.response.DailyStatsPageResponse;
import com.focusroot.dto.response.StatsSummaryResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
@Validated
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<StatsSummaryResponse>> getSummary(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.ok(statsService.getSummary(user.getId())));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyStatsPageResponse>> getDailyStats(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "7")
            @Min(value = 1, message = "range phải >= 1")
            @Max(value = 30, message = "range phải <= 30")
            int range) {
        return ResponseEntity.ok(ApiResponse.ok(statsService.getDailyStats(user.getId(), range)));
    }
}
