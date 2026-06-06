package com.focusroot.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "forests")
public class Forest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "forest", cascade = CascadeType.ALL)
    private List<Tree> trees;

    // Getters and Setters...
}
