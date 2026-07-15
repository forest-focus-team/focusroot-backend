package com.focusroot.session;

import com.focusroot.forest.MyForest;
import com.focusroot.forest.ForestRepository;
import com.focusroot.forest.TreeSpecies;
import com.focusroot.forest.TreeSpeciesRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import com.focusroot.websocket.GroupFocusRealtimeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ForestRepository forestRepository;
    private final TreeSpeciesRepository treeSpeciesRepository;
    private final GroupFocusRealtimeService groupFocusRealtimeService;

    @Transactional
    public FocusSession startSession(String username, StartSessionRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        sessionRepository.findByUserAndStatus(user, FocusSession.Status.IN_PROGRESS)
                .ifPresent(s -> { throw new IllegalArgumentException("A session is already in progress"); });

        TreeSpecies species = null;
        if (request.getTreeSpeciesId() != null) {
            species = treeSpeciesRepository.findById(request.getTreeSpeciesId())
                    .orElseThrow(() -> new EntityNotFoundException("Tree species not found"));
        }

        FocusSession session = FocusSession.builder()
                .user(user)
                .treeSpecies(species)
                .startTime(LocalDateTime.now())
                .plannedDuration(request.getPlannedDuration())
                .build();

        FocusSession savedSession = sessionRepository.save(session);
        groupFocusRealtimeService.broadcastFocusStatusChangeAfterCommit(username, savedSession.getStatus());
        return savedSession;
    }

    @Transactional
    public FocusSession endSession(String username, Long sessionId, boolean giveUp) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        FocusSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Session does not belong to current user");
        }
        if (session.getStatus() != FocusSession.Status.IN_PROGRESS) {
            throw new IllegalArgumentException("Session is not in progress");
        }

        LocalDateTime now = LocalDateTime.now();
        int actualMinutes = (int) ChronoUnit.MINUTES.between(session.getStartTime(), now);
        session.setEndTime(now);
        session.setActualDuration(actualMinutes);

        boolean succeeded = !giveUp && actualMinutes >= session.getPlannedDuration();
        session.setStatus(succeeded ? FocusSession.Status.SUCCESS : FocusSession.Status.FAILED);

        if (succeeded) {
            int coinsEarned = actualMinutes;
            session.setCoinEarned(coinsEarned);
            user.setCoin(user.getCoin() + coinsEarned);
            user.setTotalFocusMinutes(user.getTotalFocusMinutes() + actualMinutes);
            userRepository.save(user);

            MyForest tree = MyForest.builder()
                    .user(user)
                    .focusSession(session)
                    .treeSpecies(session.getTreeSpecies())
                    .plantedAt(now)
                    .isAlive(true)
                    .build();
            forestRepository.save(tree);
        } else {
            MyForest tree = MyForest.builder()
                    .user(user)
                    .focusSession(session)
                    .treeSpecies(session.getTreeSpecies())
                    .plantedAt(now)
                    .isAlive(false)
                    .build();
            forestRepository.save(tree);
        }

        FocusSession savedSession = sessionRepository.save(session);
        groupFocusRealtimeService.broadcastFocusStatusChangeAfterCommit(username, savedSession.getStatus());
        return savedSession;
    }

    public List<FocusSession> getHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return sessionRepository.findByUserOrderByStartTimeDesc(user);
    }
}
