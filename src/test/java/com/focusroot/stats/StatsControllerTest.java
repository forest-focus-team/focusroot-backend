package com.focusroot.stats;

import com.focusroot.auth.JwtService;
import com.focusroot.auth.UserPrincipal;
import com.focusroot.dto.response.DailyStatsPageResponse;
import com.focusroot.dto.response.DailyStatsResponse;
import com.focusroot.dto.response.StatsSummaryResponse;
import com.focusroot.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Exclude UserDetailsServiceAutoConfiguration để tránh xung đột với @MockBean UserDetailsService
@WebMvcTest(
    controllers = StatsController.class,
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class StatsControllerTest {

    @Autowired MockMvc mvc;

    @MockBean StatsService       statsService;
    @MockBean JwtService         jwtService;
    @MockBean UserDetailsService userDetailsService; // đủ để JwtFilter được tạo, không conflict

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .totalFocusMinutes(300)
                .coin(150)
                .build();
        principal = UserPrincipal.from(user);
    }

    // ──────────────────────────────────────────────
    //  GET /stats/summary
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /stats/summary → 200 với đầy đủ field bao gồm currentStreak")
    void getSummary_shouldReturn200_withAllFields() throws Exception {
        StatsSummaryResponse response = StatsSummaryResponse.builder()
                .totalSessions(10L).successSessions(8L).failedSessions(2L)
                .successRate(80.0).totalFocusMinutes(300).currentCoins(150)
                .totalCoinsEarned(400L).totalTreesPlanted(8L)
                .aliveTreesCount(7L).deadTreesCount(1L).currentStreak(3)
                .build();
        when(statsService.getSummary(1L)).thenReturn(response);

        mvc.perform(get("/stats/summary").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.totalSessions").value(10))
                .andExpect(jsonPath("$.data.successRate").value(80.0))
                .andExpect(jsonPath("$.data.currentStreak").value(3));
    }

    @Test
    @DisplayName("GET /stats/summary không có token → 401 (endpoint yêu cầu auth)")
    void getSummary_shouldReturn401_withoutAuth() throws Exception {
        // @WebMvcTest chỉ verify security enforcement (status 401).
        // Verification format ErrorResponse { code, message, timestamp } cho 401
        // được cover bởi SecurityConfig.authenticationEntryPoint + integration test.
        mvc.perform(get("/stats/summary"))
                .andExpect(status().isUnauthorized());
    }

    // ──────────────────────────────────────────────
    //  GET /stats/daily
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /stats/daily (default range=7) → 200 với range và data array")
    void getDailyStats_shouldReturn200_withDefaultRange() throws Exception {
        DailyStatsPageResponse pageResponse = DailyStatsPageResponse.builder()
                .range(7)
                .data(List.of(
                        DailyStatsResponse.builder()
                                .date(LocalDate.now()).minutes(50).trees(2)
                                .sessionCount(2).successCount(2).build()
                ))
                .build();
        when(statsService.getDailyStats(1L, 7)).thenReturn(pageResponse);

        mvc.perform(get("/stats/daily").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.range").value(7))
                .andExpect(jsonPath("$.data.data[0].minutes").value(50))
                .andExpect(jsonPath("$.data.data[0].trees").value(2));
    }

    @Test
    @DisplayName("GET /stats/daily?range=14 → 200 với range=14")
    void getDailyStats_shouldReturn200_withCustomRange() throws Exception {
        DailyStatsPageResponse pageResponse = DailyStatsPageResponse.builder()
                .range(14).data(List.of()).build();
        when(statsService.getDailyStats(1L, 14)).thenReturn(pageResponse);

        mvc.perform(get("/stats/daily?range=14").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.range").value(14));
    }

    @Test
    @DisplayName("GET /stats/daily?range=31 → 400 VALIDATION_ERROR (vượt max=30)")
    void getDailyStats_shouldReturn400_whenRangeExceeds30() throws Exception {
        mvc.perform(get("/stats/daily?range=31").with(user(principal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("GET /stats/daily?range=0 → 400 VALIDATION_ERROR (dưới min=1)")
    void getDailyStats_shouldReturn400_whenRangeIsZero() throws Exception {
        mvc.perform(get("/stats/daily?range=0").with(user(principal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("GET /stats/daily?range=abc → 400 BAD_REQUEST (kiểu sai)")
    void getDailyStats_shouldReturn400_whenRangeIsNotInteger() throws Exception {
        mvc.perform(get("/stats/daily?range=abc").with(user(principal)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    @DisplayName("GET /stats/daily không có token → 401")
    void getDailyStats_shouldReturn401_withoutAuth() throws Exception {
        mvc.perform(get("/stats/daily"))
                .andExpect(status().isUnauthorized());
    }
}
