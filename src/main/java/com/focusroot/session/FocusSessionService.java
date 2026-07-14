package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import com.focusroot.forest.TreeSpeciesRepository; // Giả định Repository quản lý loài cây của nhóm
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository sessionRepository;
    private final TreeSpeciesRepository treeSpeciesRepository; // Inject thêm để validate treeId

    @Transactional
    public SessionResponse startSession(Long userId, StartSessionRequest request) {
        // 1. Edge Case: Kiểm tra thời gian mục tiêu hợp lệ
        if (request.getTargetDuration() == null || request.getTargetDuration() <= 0) {
            throw new IllegalArgumentException("Thời gian mục tiêu tập trung phải lớn hơn 0 phút.");
        }

        // 2. Edge Case: Kiểm tra loại cây (treeId) có tồn tại trong hệ thống không
        if (!treeSpeciesRepository.existsById(request.getTreeId())) {
            throw new IllegalArgumentException("Loại cây được chọn không tồn tại trên hệ thống.");
        }

        // 3. Validation: Chặn trùng phiên ACTIVE
        if (sessionRepository.existsByUserIdAndStatus(userId, SessionStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đang có một phiên tập trung đang chạy. Vui lòng hoàn thành hoặc hủy nó trước.");
        }

        Session session = new Session();
        session.setUserId(userId);
        session.setTreeId(request.getTreeId());
        session.setTargetDuration(request.getTargetDuration());
        session.setStartTime(LocalDateTime.now());
        session.setStatus(SessionStatus.ACTIVE);
        
        return mapToResponse(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse endSession(Long userId, EndSessionRequest request) {
        Session session = sessionRepository.findByUserIdAndStatus(userId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên tập trung nào đang chạy."));

        session.setEndTime(LocalDateTime.now());
        long actualDurationMins = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        session.setActualDuration(actualDurationMins);

        // Logic trạng thái và Tính toán CoinEarned theo Spec dự án
        if (request.isQuitEarly() || actualDurationMins < session.getTargetDuration()) {
            session.setStatus(SessionStatus.FAILED);
            session.setCoinEarned(0L); // Thất bại thì không nhận được coin (Tâm lý sợ mất mát)
        } else {
            session.setStatus(SessionStatus.COMPLETED);
            // Ví dụ Spec: 1 phút tập trung hoàn thành đổi được 2 xu (Coin)
            session.setCoinEarned(actualDurationMins * 2); 
        }

        return mapToResponse(sessionRepository.save(session));
    }

    private SessionResponse mapToResponse(Session s) {
        return new SessionResponse(
                s.getId(), s.getTargetDuration(), s.getActualDuration(), 
                s.getStartTime(), s.getEndTime(), s.getStatus().name(),
                s.getCoinEarned() // Đảm bảo DTO Response của bạn đã thêm trường này
        );
    }
}
