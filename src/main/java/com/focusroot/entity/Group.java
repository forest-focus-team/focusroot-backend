package com.focusroot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "groups_table")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;
    private String joinCode;
    private String status; // ACTIVE, FINISHED

    // Getters and Setters...
}
