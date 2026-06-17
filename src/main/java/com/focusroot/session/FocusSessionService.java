package com.focusroot.session;

import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import com.focusroot.forest.TreeSpecies;
import com.focusroot.forest.TreeSpeciesRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final TreeSpeciesRepository treeSpeciesRepository;

    @Transactional
    public SessionResponse startSession(Long userId, StartSessionRequest request) {
        if (sessionRepository.existsByUser_IdAndStatus(userId, FocusSession.Status.IN_PROGRESS)) {
            throw new IllegalStateException("Bạn đang có một phiên tập trung đang chạy. Vui lòng hoàn thành hoặc hủy nó trước.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng."));

        TreeSpecies treeSpecies = treeSpeciesRepository.findById(request.getTreeId())
                .orElseThrow(() -> new IllegalArgumentException("Loài cây không tồn tại."));

        FocusSession session = FocusSession.builder()
                .user(user)
                .treeSpecies(treeSpecies)
                .plannedDuration(request.getDurationMinutes())
                .startTime(LocalDateTime.now())
                .status(FocusSession.Status.IN_PROGRESS)
                .build();

        return mapToResponse(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse endSession(Long userId, EndSessionRequest request) {
        FocusSession session = sessionRepository.findByUser_IdAndStatus(userId, FocusSession.Status.IN_PROGRESS)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiên tập trung nào đang chạy."));

        LocalDateTime endTime = LocalDateTime.now();
        int actualDuration = (int) Duration.between(session.getStartTime(), endTime).toMinutes();

        session.setEndTime(endTime);
        session.setActualDuration(actualDuration);

        if (request.isQuitEarly() || actualDuration < session.getPlannedDuration()) {
            session.setStatus(FocusSession.Status.FAILED);
            session.setCoinEarned(0);
        } else {
            session.setStatus(FocusSession.Status.SUCCESS);
            session.setCoinEarned(session.getPlannedDuration());
        }

        return mapToResponse(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getHistory(Long userId, Pageable pageable) {
        return sessionRepository.findByUser_IdOrderByStartTimeDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    private SessionResponse mapToResponse(FocusSession s) {
        Long treeSpeciesId = s.getTreeSpecies() != null ? s.getTreeSpecies().getId() : null;
        return new SessionResponse(
                s.getId(),
                treeSpeciesId,
                s.getPlannedDuration(),
                s.getActualDuration(),
                s.getStartTime(),
                s.getEndTime(),
                s.getStatus().name(),
                s.getCoinEarned()
        );
    }
}
