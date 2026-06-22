package com.focusroot.group;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.focusroot.common.AppConstants;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupSessionRepository groupSessionRepository;
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

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private FocusGroup findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + id));
    }
}
