package com.focusroot.prediction;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final UserActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public List<UserActivityLog> getActivityLogs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return activityLogRepository.findByUserOrderByLogDateDesc(user);
    }

    /**
     * Simple prediction: average of last 7 days × planned sessions.
     * Replace with ML model output when prediction service is integrated.
     */
    public PredictionResult predictSuccess(String username, int plannedMinutes) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<UserActivityLog> recentLogs =
                activityLogRepository.findByUserAndLogDateAfter(user, sevenDaysAgo);

        if (recentLogs.isEmpty()) {
            return new PredictionResult(0.5, "No history — default 50% estimate");
        }

        double avgMinutes = recentLogs.stream()
                .mapToInt(UserActivityLog::getTotalMinutes)
                .average()
                .orElse(0);

        double successRate = recentLogs.stream()
                .mapToDouble(log -> log.getSessionCount() > 0
                        ? (double) log.getSuccessCount() / log.getSessionCount() : 0)
                .average()
                .orElse(0);

        double capacityFactor = avgMinutes > 0 ? Math.min(1.0, avgMinutes / plannedMinutes) : 0;
        double probability = (successRate * 0.7) + (capacityFactor * 0.3);

        return new PredictionResult(probability,
                String.format("Based on %d days of history", recentLogs.size()));
    }

    public record PredictionResult(double probability, String explanation) {}
}
