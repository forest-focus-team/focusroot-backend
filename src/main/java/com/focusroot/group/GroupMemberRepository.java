package com.focusroot.group;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(FocusGroup group, User user);

    long countByGroupAndStatus(FocusGroup group, GroupMember.Status status);
}
