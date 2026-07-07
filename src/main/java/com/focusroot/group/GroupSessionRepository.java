package com.focusroot.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, Long> {

    List<GroupSession> findByGroupOrderByStartTimeDesc(FocusGroup group);

    Optional<GroupSession> findByGroupAndStatus(FocusGroup group, GroupSession.Status status);
}
