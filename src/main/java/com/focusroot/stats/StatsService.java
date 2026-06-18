package com.focusroot.stats;

import com.focusroot.dto.response.DailyStatsPageResponse;
import com.focusroot.dto.response.DailyStatsResponse;
import com.focusroot.dto.response.StatsSummaryResponse;
import com.focusroot.forest.ForestRepository;
import com.focusroot.prediction.UserActivityLog;
import com.focusroot.prediction.UserActivityLogRepository;
import com.focusroot.session.FocusSession;
import com.focusroot.session.FocusSessionRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final UserRepository userRepository;
    private final FocusSessionRepository sessionRepository;
    private final ForestRepository forestRepository;
    private final UserActivityLogRepository activityLogRepository;

    public StatsSummaryResponse getSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        long totalSessions    = sessionRepository.countByUser_Id(userId);
        long successSessions  = sessionRepository.countByUser_IdAndStatus(userId, FocusSession.Status.SUCCESS);
        long failedSessions   = sessionRepository.countByUser_IdAndStatus(userId, FocusSession.Status.FAILED);
        double successRate    = totalSessions > 0
                ? Math.round((double) successSessions / totalSessions * 10000.0) / 100.0
                : 0.0;

        long totalCoinsEarned  = sessionRepository.sumCoinEarnedByUserId(userId);
        long totalTreesPlanted = forestRepository.countByUser_Id(userId);
        long aliveTreesCount   = forestRepository.countByUser_IdAndIsAlive(userId, true);
        long deadTreesCount    = forestRepository.countByUser_IdAndIsAlive(userId, false);
        int  currentStreak     = calculateStreak(userId);

        return StatsSummaryResponse.builder()
                .totalSessions(totalSessions)
                .successSessions(successSessions)
                .failedSessions(failedSessions)
                .successRate(successRate)
                .totalFocusMinutes(user.getTotalFocusMinutes())
                .currentCoins(user.getCoin())
                .totalCoinsEarned(totalCoinsEarned)
                .totalTreesPlanted(totalTreesPlanted)
                .aliveTreesCount(aliveTreesCount)
                .deadTreesCount(deadTreesCount)
                .currentStreak(currentStreak)
                .build();
    }

    public DailyStatsPageResponse getDailyStats(Long userId, int range) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDate today    = LocalDate.now();
        LocalDate fromDate = today.minusDays(range - 1);

        // Load activity log trong range, đưa vào map date→log để lookup O(1)
        Map<LocalDate, UserActivityLog> logMap = activityLogRepository
                .findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(userId, fromDate)
                .stream()
                .collect(Collectors.toMap(UserActivityLog::getLogDate, l -> l));

        // Load số cây trồng mỗi ngày trong range, group by date
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        Map<LocalDate, Long> treesMap = forestRepository
                .countTreesPerDaySince(userId, fromDateTime)
                .stream()
                .collect(Collectors.toMap(
                        ForestRepository.DailyTreeCount::getDate,
                        ForestRepository.DailyTreeCount::getCount));

        // Tạo danh sách đủ range ngày, từ today lùi dần; zero-fill ngày thiếu
        List<DailyStatsResponse> data = IntStream.rangeClosed(0, range - 1)
                .<DailyStatsResponse>mapToObj(i -> {
                    LocalDate date = today.minusDays(i);
                    UserActivityLog log = logMap.get(date);
                    return DailyStatsResponse.builder()
                            .date(date)
                            .minutes(log != null ? log.getTotalMinutes() : 0)
                            .trees(treesMap.getOrDefault(date, 0L).intValue())
                            .sessionCount(log != null ? log.getSessionCount() : 0)
                            .successCount(log != null ? log.getSuccessCount() : 0)
                            .build();
                })
                .collect(Collectors.toList());

        return DailyStatsPageResponse.builder()
                .range(range)
                .data(data)
                .build();
    }

    /**
     * Tính streak liên tiếp (số ngày liên tục có session thành công).
     *
     * Logic:
     * - Lấy danh sách ngày có successCount > 0, sắp xếp giảm dần.
     * - Bắt đầu từ hôm nay; nếu hôm nay chưa có activity thì chấp nhận tính từ hôm qua
     *   (user có thể chưa focus hôm nay nhưng streak vẫn còn hiệu lực).
     * - Đếm liên tục cho đến khi gặp ngày bị gián đoạn.
     */
    private int calculateStreak(Long userId) {
        List<LocalDate> successDates = activityLogRepository.findSuccessDatesByUserIdDesc(userId);
        if (successDates.isEmpty()) return 0;

        LocalDate expected = LocalDate.now();
        LocalDate mostRecent = successDates.get(0);

        // Nếu ngày gần nhất không phải hôm nay và không phải hôm qua → streak đã bị đứt
        if (!mostRecent.equals(expected) && !mostRecent.equals(expected.minusDays(1))) {
            return 0;
        }
        // Bắt đầu đếm từ ngày gần nhất có activity
        expected = mostRecent;

        int streak = 0;
        for (LocalDate date : successDates) {
            if (date.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
