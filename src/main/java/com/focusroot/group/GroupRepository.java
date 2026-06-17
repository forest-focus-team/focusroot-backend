package com.focusroot.group;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<FocusGroup, Long> {

    List<FocusGroup> findByOwner(User owner);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user AND gm.status = 'ACTIVE'")
    List<FocusGroup> findGroupsByActiveMember(@Param("user") User user);
}
