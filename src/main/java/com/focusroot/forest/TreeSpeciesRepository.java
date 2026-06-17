package com.focusroot.forest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreeSpeciesRepository extends JpaRepository<TreeSpecies, Long> {
}
