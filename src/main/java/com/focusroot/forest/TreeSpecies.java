package com.focusroot.forest;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tree_species")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "required_minutes", nullable = false)
    private Integer requiredMinutes;

    @Column(name = "coin_cost", nullable = false)
    @Builder.Default
    private Integer coinCost = 0;
}
