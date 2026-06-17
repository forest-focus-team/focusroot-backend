package com.focusroot.forest;

import com.focusroot.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForestRepository extends JpaRepository<MyForest, Long> {

    List<MyForest> findByUserOrderByPlantedAtDesc(User user);

    long countByUserAndIsAlive(User user, Boolean isAlive);
}
