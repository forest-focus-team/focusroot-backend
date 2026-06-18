package com.focusroot.session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FocusSessionRepository extends JpaRepository<FocusSession, Long> {

    boolean existsByUser_IdAndStatus(Long userId, FocusSession.Status status);

    Optional<FocusSession> findByUser_IdAndStatus(Long userId, FocusSession.Status status);

    Page<FocusSession> findByUser_IdOrderByStartTimeDesc(Long userId, Pageable pageable);

    long countByUser_Id(Long userId);

    long countByUser_IdAndStatus(Long userId, FocusSession.Status status);

    @Query("SELECT COALESCE(SUM(s.coinEarned), 0) FROM FocusSession s WHERE s.user.id = :userId AND s.coinEarned > 0")
    long sumCoinEarnedByUserId(@Param("userId") Long userId);
}
