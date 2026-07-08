package com.focusroot.forest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.focusroot.session.FocusSession;
import com.focusroot.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Bỏ qua thuộc tính nội bộ của Hibernate proxy khi serialize entity qua HTTP (LAZY + OSIV) — bug demo Tuần 5
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "my_forest")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyForest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "focus_session_id", unique = true)
    private FocusSession focusSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_species_id")
    private TreeSpecies treeSpecies;

    @Column(name = "planted_at", nullable = false)
    private LocalDateTime plantedAt;

    @Column(name = "is_alive", nullable = false)
    @Builder.Default
    private Boolean isAlive = true;
}
