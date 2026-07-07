package com.focusroot.session;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<FocusSession, Long> {

    List<FocusSession> findByUserOrderByStartTimeDesc(User user);

    Optional<FocusSession> findByUserAndStatus(User user, FocusSession.Status status);

    long countByUserAndStatus(User user, FocusSession.Status status);

    // --- Thống kê (gộp từ FocusSessionRepository cũ khi hợp nhất session module, issue #43) ---

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, FocusSession.Status status);

    @Query("SELECT COALESCE(SUM(s.coinEarned), 0) FROM FocusSession s WHERE s.user.id = :userId AND s.coinEarned > 0")
    long sumCoinEarnedByUserId(@Param("userId") Long userId);
}
