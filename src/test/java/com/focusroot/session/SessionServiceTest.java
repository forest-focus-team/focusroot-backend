package com.focusroot.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.focusroot.forest.ForestRepository;
import com.focusroot.forest.MyForest;
import com.focusroot.forest.TreeSpeciesRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import com.focusroot.websocket.GroupFocusRealtimeService;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock SessionRepository sessionRepository;
    @Mock UserRepository userRepository;
    @Mock ForestRepository forestRepository;
    @Mock TreeSpeciesRepository treeSpeciesRepository;
    @Mock GroupFocusRealtimeService groupFocusRealtimeService;

    @InjectMocks SessionService sessionService;

    @Test
    @DisplayName("startSession broadcast IN_PROGRESS sau khi tạo session")
    void startSession_shouldBroadcastInProgressStatus() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .build();
        StartSessionRequest request = new StartSessionRequest();
        request.setPlannedDuration(25);

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(sessionRepository.findByUserAndStatus(user, FocusSession.Status.IN_PROGRESS)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(FocusSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FocusSession result = sessionService.startSession("alice", request);

        assertThat(result.getStatus()).isEqualTo(FocusSession.Status.IN_PROGRESS);
        verify(groupFocusRealtimeService)
                .broadcastFocusStatusChangeAfterCommit("alice", FocusSession.Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("endSession broadcast FAILED khi user give up")
    void endSession_shouldBroadcastFailedStatus_whenGiveUp() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .coin(0)
                .totalFocusMinutes(0)
                .build();
        FocusSession session = FocusSession.builder()
                .id(5L)
                .user(user)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .plannedDuration(25)
                .status(FocusSession.Status.IN_PROGRESS)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(sessionRepository.findById(5L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(FocusSession.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(forestRepository.save(any(MyForest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FocusSession result = sessionService.endSession("alice", 5L, true);

        assertThat(result.getStatus()).isEqualTo(FocusSession.Status.FAILED);
        verify(groupFocusRealtimeService)
                .broadcastFocusStatusChangeAfterCommit("alice", FocusSession.Status.FAILED);
    }
}
