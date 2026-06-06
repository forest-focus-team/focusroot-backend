package com.focusroot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trees")
public class Tree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String treeType; // Loài cây
    private String status;   // Trạng thái: Sống/Chết

    @ManyToOne
    @JoinColumn(name = "forest_id", nullable = false)
    private Forest forest;

    @OneToOne
    @JoinColumn(name = "session_id")
    private Session session;

    // Getters and Setters...
}
