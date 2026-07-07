package com.focusroot.stats;

import com.focusroot.dto.response.DailyStatsPageResponse;
import com.focusroot.dto.response.DailyStatsResponse;
import com.focusroot.dto.response.StatsSummaryResponse;
import com.focusroot.forest.ForestRepository;
import com.focusroot.prediction.UserActivityLog;
import com.focusroot.prediction.UserActivityLogRepository;
import com.focusroot.session.FocusSession;
import com.focusroot.session.SessionRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock UserRepository userRepository;
    @Mock SessionRepository sessionRepository;
    @Mock ForestRepository forestRepository;
    @Mock UserActivityLogRepository activityLogRepository;

    @InjectMocks StatsService statsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .totalFocusMinutes(300)
                .coin(150)
                .build();
    }

    // ──────────────────────────────────────────────
    //  getSummary
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("getSummary trả đúng tất cả field, bao gồm currentStreak")
    void getSummary_shouldReturnAllFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.countByUser_Id(1L)).thenReturn(10L);
        when(sessionRepository.countByUser_IdAndStatus(1L, FocusSession.Status.SUCCESS)).thenReturn(8L);
        when(sessionRepository.countByUser_IdAndStatus(1L, FocusSession.Status.FAILED)).thenReturn(2L);
        when(sessionRepository.sumCoinEarnedByUserId(1L)).thenReturn(400L);
        when(forestRepository.countByUser_Id(1L)).thenReturn(8L);
        when(forestRepository.countByUser_IdAndIsAlive(1L, true)).thenReturn(7L);
        when(forestRepository.countByUser_IdAndIsAlive(1L, false)).thenReturn(1L);
        // Streak: 2 ngày liên tiếp (hôm qua + hôm nay)
        LocalDate today = LocalDate.now();
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L))
                .thenReturn(List.of(today, today.minusDays(1)));

        StatsSummaryResponse result = statsService.getSummary(1L);

        assertThat(result.getTotalSessions()).isEqualTo(10L);
        assertThat(result.getSuccessSessions()).isEqualTo(8L);
        assertThat(result.getFailedSessions()).isEqualTo(2L);
        assertThat(result.getSuccessRate()).isEqualTo(80.0);
        assertThat(result.getTotalFocusMinutes()).isEqualTo(300);
        assertThat(result.getCurrentCoins()).isEqualTo(150);
        assertThat(result.getTotalCoinsEarned()).isEqualTo(400L);
        assertThat(result.getTotalTreesPlanted()).isEqualTo(8L);
        assertThat(result.getAliveTreesCount()).isEqualTo(7L);
        assertThat(result.getDeadTreesCount()).isEqualTo(1L);
        assertThat(result.getCurrentStreak()).isEqualTo(2);
    }

    @Test
    @DisplayName("getSummary ném EntityNotFoundException khi user không tồn tại")
    void getSummary_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statsService.getSummary(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    @DisplayName("successRate = 0.0 khi chưa có session nào")
    void getSummary_shouldReturnZeroSuccessRate_whenNoSessions() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.countByUser_Id(1L)).thenReturn(0L);
        when(sessionRepository.countByUser_IdAndStatus(any(), any())).thenReturn(0L);
        when(sessionRepository.sumCoinEarnedByUserId(1L)).thenReturn(0L);
        when(forestRepository.countByUser_Id(1L)).thenReturn(0L);
        when(forestRepository.countByUser_IdAndIsAlive(anyLong(), anyBoolean())).thenReturn(0L);
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L)).thenReturn(List.of());

        StatsSummaryResponse result = statsService.getSummary(1L);

        assertThat(result.getSuccessRate()).isEqualTo(0.0);
        assertThat(result.getCurrentStreak()).isEqualTo(0);
    }

    // ──────────────────────────────────────────────
    //  calculateStreak (kiểm tra qua getSummary)
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("Streak = 0 khi chưa có ngày thành công nào")
    void streak_shouldBeZero_whenNoSuccessDates() {
        stubSummaryDependencies();
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L)).thenReturn(List.of());

        assertThat(statsService.getSummary(1L).getCurrentStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("Streak = 1 khi chỉ có hôm nay")
    void streak_shouldBeOne_whenOnlyToday() {
        stubSummaryDependencies();
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L))
                .thenReturn(List.of(LocalDate.now()));

        assertThat(statsService.getSummary(1L).getCurrentStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("Streak tính từ hôm qua khi hôm nay chưa có activity")
    void streak_shouldCountFromYesterday_whenTodayMissing() {
        stubSummaryDependencies();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L))
                .thenReturn(List.of(yesterday, yesterday.minusDays(1)));

        assertThat(statsService.getSummary(1L).getCurrentStreak()).isEqualTo(2);
    }

    @Test
    @DisplayName("Streak = 0 khi ngày gần nhất là 2 ngày trước (bị đứt)")
    void streak_shouldBeZero_whenGapMoreThanOneDay() {
        stubSummaryDependencies();
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L))
                .thenReturn(List.of(twoDaysAgo, twoDaysAgo.minusDays(1)));

        assertThat(statsService.getSummary(1L).getCurrentStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("Streak dừng lại khi có ngày bị gián đoạn giữa chuỗi")
    void streak_shouldStopAtGap() {
        stubSummaryDependencies();
        LocalDate today = LocalDate.now();
        // today, yesterday, nhưng bỏ qua ngày -2, rồi -3 → streak = 2
        when(activityLogRepository.findSuccessDatesByUserIdDesc(1L))
                .thenReturn(List.of(today, today.minusDays(1), today.minusDays(3)));

        assertThat(statsService.getSummary(1L).getCurrentStreak()).isEqualTo(2);
    }

    // ──────────────────────────────────────────────
    //  getDailyStats
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("getDailyStats trả về đúng số phần tử theo range")
    void getDailyStats_shouldReturnCorrectNumberOfDays() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(anyLong(), any()))
                .thenReturn(List.of());
        when(forestRepository.countTreesPerDaySince(anyLong(), any()))
                .thenReturn(List.of());

        DailyStatsPageResponse result = statsService.getDailyStats(1L, 7);

        assertThat(result.getRange()).isEqualTo(7);
        assertThat(result.getData()).hasSize(7);
    }

    @Test
    @DisplayName("Ngày không có activity được zero-fill (minutes=0, trees=0)")
    void getDailyStats_shouldZeroFillMissingDays() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(anyLong(), any()))
                .thenReturn(List.of());
        when(forestRepository.countTreesPerDaySince(anyLong(), any()))
                .thenReturn(List.of());

        DailyStatsPageResponse result = statsService.getDailyStats(1L, 3);

        assertThat(result.getData()).allSatisfy(day -> {
            assertThat(day.getMinutes()).isEqualTo(0);
            assertThat(day.getTrees()).isEqualTo(0);
            assertThat(day.getSessionCount()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("Ngày có data được map đúng minutes và trees")
    void getDailyStats_shouldMapActivityAndTreesCorrectly() {
        LocalDate today = LocalDate.now();
        UserActivityLog todayLog = UserActivityLog.builder()
                .logDate(today)
                .totalMinutes(50)
                .sessionCount(2)
                .successCount(2)
                .build();

        ForestRepository.DailyTreeCount treeCount = mockDailyTreeCount(today, 3L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(anyLong(), any()))
                .thenReturn(List.of(todayLog));
        when(forestRepository.countTreesPerDaySince(anyLong(), any()))
                .thenReturn(List.of(treeCount));

        DailyStatsPageResponse result = statsService.getDailyStats(1L, 7);

        DailyStatsResponse todayResult = result.getData().get(0);
        assertThat(todayResult.getDate()).isEqualTo(today);
        assertThat(todayResult.getMinutes()).isEqualTo(50);
        assertThat(todayResult.getTrees()).isEqualTo(3);
        assertThat(todayResult.getSessionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("getDailyStats ném EntityNotFoundException khi user không tồn tại")
    void getDailyStats_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statsService.getDailyStats(99L, 7))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Danh sách kết quả sắp xếp giảm dần theo ngày (today first)")
    void getDailyStats_shouldBeSortedDescending() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(activityLogRepository.findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(anyLong(), any()))
                .thenReturn(List.of());
        when(forestRepository.countTreesPerDaySince(anyLong(), any()))
                .thenReturn(List.of());

        List<DailyStatsResponse> data = statsService.getDailyStats(1L, 3).getData();

        assertThat(data.get(0).getDate()).isEqualTo(LocalDate.now());
        assertThat(data.get(1).getDate()).isEqualTo(LocalDate.now().minusDays(1));
        assertThat(data.get(2).getDate()).isEqualTo(LocalDate.now().minusDays(2));
    }

    // ──────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────

    /** Stub các dependency không liên quan để test streak trong isolation. */
    private void stubSummaryDependencies() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sessionRepository.countByUser_Id(1L)).thenReturn(0L);
        when(sessionRepository.countByUser_IdAndStatus(anyLong(), any())).thenReturn(0L);
        when(sessionRepository.sumCoinEarnedByUserId(1L)).thenReturn(0L);
        when(forestRepository.countByUser_Id(1L)).thenReturn(0L);
        when(forestRepository.countByUser_IdAndIsAlive(anyLong(), anyBoolean())).thenReturn(0L);
    }

    /** Tạo anonymous implementation của DailyTreeCount projection để dùng trong test. */
    private ForestRepository.DailyTreeCount mockDailyTreeCount(LocalDate date, long count) {
        return new ForestRepository.DailyTreeCount() {
            @Override public LocalDate getDate()  { return date; }
            @Override public long     getCount()  { return count; }
        };
    }
}
