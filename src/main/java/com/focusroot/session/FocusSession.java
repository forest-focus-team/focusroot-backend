package com.focusroot.session;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.focusroot.forest.TreeSpecies;
import com.focusroot.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Bỏ qua thuộc tính nội bộ của Hibernate proxy khi serialize entity qua HTTP (LAZY + OSIV) — bug demo Tuần 5
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "focus_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusSession {

    public enum Status {
        IN_PROGRESS, SUCCESS, FAILED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_species_id")
    private TreeSpecies treeSpecies;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "planned_duration", nullable = false)
    private Integer plannedDuration;

    @Column(name = "actual_duration")
    private Integer actualDuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column(name = "coin_earned")
    @Builder.Default
    private Integer coinEarned = 0;
}
