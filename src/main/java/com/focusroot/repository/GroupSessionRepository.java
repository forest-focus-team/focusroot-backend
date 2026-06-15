package com.focusroot.repository;

import com.focusroot.entity.GroupSession;
import com.focusroot.entity.enums.GroupSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupSessionRepository extends JpaRepository<GroupSession, Long> {
    Optional<GroupSession> findTopByGroupIdAndStatusOrderByStartedAtDesc(Long groupId, GroupSessionStatus status);
}
