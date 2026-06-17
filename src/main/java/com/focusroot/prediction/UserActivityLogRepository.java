package com.focusroot.prediction;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    List<UserActivityLog> findByUserOrderByLogDateDesc(User user);

    List<UserActivityLog> findByUserAndLogDateAfter(User user, LocalDate date);

    Optional<UserActivityLog> findByUserAndLogDate(User user, LocalDate date);
}
