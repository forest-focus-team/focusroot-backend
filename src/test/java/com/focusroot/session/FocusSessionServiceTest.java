package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.forest.TreeSpeciesRepository;
import com.focusroot.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FocusSessionServiceTest {

    @Mock
    private FocusSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TreeSpeciesRepository treeSpeciesRepository;

    @InjectMocks
    private FocusSessionService sessionService;

    @Test
    @DisplayName("Ném lỗi IllegalStateException khi User bắt đầu phiên mới nhưng đã có phiên IN_PROGRESS")
    void startSession_ShouldThrowException_WhenActiveSessionExists() {
        Long userId = 1L;
        StartSessionRequest request = new StartSessionRequest();
        request.setTreeId(1L);
        request.setDurationMinutes(25);

        when(sessionRepository.existsByUser_IdAndStatus(userId, FocusSession.Status.IN_PROGRESS)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                sessionService.startSession(userId, request));

        assertEquals("Bạn đang có một phiên tập trung đang chạy. Vui lòng hoàn thành hoặc hủy nó trước.", exception.getMessage());
        verify(sessionRepository, never()).save(any(FocusSession.class));
    }
}
