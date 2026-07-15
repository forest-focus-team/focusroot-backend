package com.focusroot.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.focusroot.session.FocusSession;
import com.focusroot.session.SessionRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock GroupRepository groupRepository;
    @Mock GroupMemberRepository memberRepository;
    @Mock SessionRepository sessionRepository;
    @Mock UserRepository userRepository;

    @InjectMocks GroupService groupService;

    @Test
    @DisplayName("getGroupMembers trả danh sách active members kèm trạng thái focusing hiện tại")
    void getGroupMembers_shouldReturnMembersWithFocusingStatus() {
        User alice = User.builder().id(1L).username("alice").email("alice@test.com").passwordHash("hash").build();
        User bob = User.builder().id(2L).username("bob").email("bob@test.com").passwordHash("hash").build();
        FocusGroup group = FocusGroup.builder().id(10L).name("Deep Work").owner(alice).build();

        GroupMember aliceMember = GroupMember.builder()
                .id(100L)
                .group(group)
                .user(alice)
                .status(GroupMember.Status.ACTIVE)
                .joinedAt(LocalDateTime.now().minusMinutes(10))
                .build();
        GroupMember bobMember = GroupMember.builder()
                .id(101L)
                .group(group)
                .user(bob)
                .status(GroupMember.Status.ACTIVE)
                .joinedAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(memberRepository.existsByGroupAndUserAndStatus(group, alice, GroupMember.Status.ACTIVE)).thenReturn(true);
        when(memberRepository.findByGroupAndStatusOrderByJoinedAtAsc(group, GroupMember.Status.ACTIVE))
                .thenReturn(List.of(aliceMember, bobMember));
        when(sessionRepository.existsByUserAndStatus(alice, FocusSession.Status.IN_PROGRESS)).thenReturn(true);
        when(sessionRepository.existsByUserAndStatus(bob, FocusSession.Status.IN_PROGRESS)).thenReturn(false);

        List<GroupMemberResponse> result = groupService.getGroupMembers("alice", 10L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
        assertThat(result.get(0).getFocusing()).isTrue();
        assertThat(result.get(1).getUsername()).isEqualTo("bob");
        assertThat(result.get(1).getFocusing()).isFalse();
    }

    @Test
    @DisplayName("getGroupMembers ném 403 khi requester không phải active member")
    void getGroupMembers_shouldThrowForbidden_whenRequesterIsNotActiveMember() {
        User alice = User.builder().id(1L).username("alice").email("alice@test.com").passwordHash("hash").build();
        FocusGroup group = FocusGroup.builder().id(10L).name("Deep Work").owner(alice).build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(groupRepository.findById(10L)).thenReturn(Optional.of(group));
        when(memberRepository.existsByGroupAndUserAndStatus(group, alice, GroupMember.Status.ACTIVE)).thenReturn(false);

        assertThatThrownBy(() -> groupService.getGroupMembers("alice", 10L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }
}
