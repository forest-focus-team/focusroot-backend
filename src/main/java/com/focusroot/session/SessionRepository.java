package com.focusroot.session;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<FocusSession, Long> {

    List<FocusSession> findByUserOrderByStartTimeDesc(User user);

    Optional<FocusSession> findByUserAndStatus(User user, FocusSession.Status status);

    long countByUserAndStatus(User user, FocusSession.Status status);
}
