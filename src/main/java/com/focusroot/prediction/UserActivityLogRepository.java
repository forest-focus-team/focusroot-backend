package com.focusroot.prediction;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    List<UserActivityLog> findByUserOrderByLogDateDesc(User user);

    List<UserActivityLog> findByUserAndLogDateAfter(User user, LocalDate date);

    Optional<UserActivityLog> findByUserAndLogDate(User user, LocalDate date);

    /** Lấy log trong range ngày để fill chart, không cần load User entity. */
    List<UserActivityLog> findByUser_IdAndLogDateGreaterThanEqualOrderByLogDateDesc(
            Long userId, LocalDate fromDate);

    /**
     * Lấy danh sách ngày có ít nhất 1 session thành công, sắp xếp giảm dần.
     * Dùng để tính streak liên tiếp.
     */
    @Query("SELECT l.logDate FROM UserActivityLog l " +
           "WHERE l.user.id = :userId AND l.successCount > 0 " +
           "ORDER BY l.logDate DESC")
    List<LocalDate> findSuccessDatesByUserIdDesc(@Param("userId") Long userId);
}
