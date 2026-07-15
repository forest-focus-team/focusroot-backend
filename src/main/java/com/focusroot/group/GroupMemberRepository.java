package com.focusroot.group;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.focusroot.user.User;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupAndUser(FocusGroup group, User user);

    @Query("SELECT gm FROM GroupMember gm JOIN gm.user u WHERE u.username = :username AND gm.status = :status")
    List<GroupMember> findByUsernameAndStatus(@Param("username") String username, @Param("status") GroupMember.Status status);

    @Query("""
            SELECT gm
            FROM GroupMember gm
            JOIN FETCH gm.user
            WHERE gm.group = :group AND gm.status = :status
            ORDER BY gm.joinedAt ASC
            """)
    List<GroupMember> findByGroupAndStatusOrderByJoinedAtAsc(
            @Param("group") FocusGroup group,
            @Param("status") GroupMember.Status status
    );

    boolean existsByGroupAndUserAndStatus(FocusGroup group, User user, GroupMember.Status status);

    long countByGroupAndStatus(FocusGroup group, GroupMember.Status status);
}
