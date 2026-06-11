package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository sessionRepository;

    @Transactional
    public SessionResponse startSession(Long userId, StartSessionRequest request) {
        // Chặn nếu đã có phiên ACTIVE
        if (sessionRepository.existsByUserIdAndStatus(userId, SessionStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đang có một phiên tập trung đang chạy. Vui lòng hoàn thành hoặc hủy nó trước.");
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setTargetDuration(request.getTargetDuration());
        session.setStartTime(LocalDateTime.now());
        session.setStatus(SessionStatus.ACTIVE);
        
        Session saved = sessionRepository.save(session);
        return mapToResponse(saved);
    }

    @Transactional
    public SessionResponse endSession(Long userId, EndSessionRequest request) {
        // Tìm phiên ACTIVE hiện tại
        Session session = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên tập trung nào đang chạy."));

        // Tính toán thời gian
        session.setEndTime(LocalDateTime.now());
        long actualDurationMins = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        session.setActualDuration(actualDurationMins);

        // Logic trạng thái: FAILED nếu thoát sớm hoặc chưa đạt thời gian mục tiêu
        if (request.isQuitEarly() || actualDurationMins < session.getTargetDuration()) {
            session.setStatus(SessionStatus.FAILED);
        } else {
            session.setStatus(SessionStatus.COMPLETED);
        }

        return mapToResponse(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getHistory(Long userId, Pageable pageable) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    private SessionResponse mapToResponse(Session s) {
        return new SessionResponse(
                s.getId(), 
                s.getTargetDuration(), 
                s.getActualDuration(), 
                s.getStartTime(), 
                s.getEndTime(), 
                s.getStatus().name()
        );
    }
}
