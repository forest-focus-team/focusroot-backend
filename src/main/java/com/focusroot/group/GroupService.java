package com.focusroot.group;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.focusroot.common.AppConstants;
import com.focusroot.session.FocusSession;
import com.focusroot.session.SessionRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public FocusGroup createGroup(String username, CreateGroupRequest request) {
        User owner = findUser(username);
        FocusGroup group = FocusGroup.builder()
                .name(request.getName())
                .owner(owner)
                .penaltyCoins(request.getPenaltyCoins() != null ? request.getPenaltyCoins() : 0)
                .build();
        group = groupRepository.save(group);

        GroupMember ownerMember = GroupMember.builder()
                .group(group)
                .user(owner)
                .build();
        memberRepository.save(ownerMember);
        return group;
    }

    @Transactional
    public GroupMember joinGroup(String username, Long groupId) {
        User user = findUser(username);
        FocusGroup group = findGroup(groupId);

        if (!group.getIsActive()) {
            throw new IllegalArgumentException("Group is not active");
        }

        memberRepository.findByGroupAndUser(group, user).ifPresent(m -> {
            if (m.getStatus() == GroupMember.Status.ACTIVE) {
                throw new IllegalArgumentException("Already a member of this group");
            }
        });

        long activeCount = memberRepository.countByGroupAndStatus(group, GroupMember.Status.ACTIVE);
        if (activeCount >= AppConstants.MAX_GROUP_MEMBERS) {
            throw new IllegalArgumentException("Group is full (max " + AppConstants.MAX_GROUP_MEMBERS + " members)");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();
        return memberRepository.save(member);
    }

    public List<FocusGroup> getMyGroups(String username) {
        User user = findUser(username);
        return groupRepository.findGroupsByActiveMember(user);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(String username, Long groupId) {
        User requester = findUser(username);
        FocusGroup group = findGroup(groupId);

        if (!memberRepository.existsByGroupAndUserAndStatus(group, requester, GroupMember.Status.ACTIVE)) {
            throw new AccessDeniedException("Access denied");
        }

        return memberRepository.findByGroupAndStatusOrderByJoinedAtAsc(group, GroupMember.Status.ACTIVE).stream()
                .map(this::mapMemberToResponse)
                .toList();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private FocusGroup findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + id));
    }

    public GroupResponse mapGroupToResponse(FocusGroup group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .ownerUsername(group.getOwner().getUsername())
                .penaltyCoins(group.getPenaltyCoins())
                .isActive(group.getIsActive())
                .createdAt(group.getCreatedAt())
                .build();
    }

    public GroupMemberResponse mapMemberToResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .groupId(member.getGroup().getId())
                .groupName(member.getGroup().getName())
                .username(member.getUser().getUsername())
                .status(member.getStatus())
                .focusing(sessionRepository.existsByUserAndStatus(member.getUser(), FocusSession.Status.IN_PROGRESS))
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
