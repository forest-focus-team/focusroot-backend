package com.focusroot.forest;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ForestService {

    private final ForestRepository forestRepository;
    private final UserRepository userRepository;
    private final TreeSpeciesRepository treeSpeciesRepository;

    public List<MyForest> getForest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return forestRepository.findByUserOrderByPlantedAtDesc(user);
    }

    public List<TreeSpecies> getAllSpecies() {
        return treeSpeciesRepository.findAll();
    }

    public MyForest handleSessionEnd(User user, TreeSpecies species, boolean succeeded) {
        MyForest plant = new MyForest();
        plant.setUser(user);
        plant.setTreeSpecies(species);
        plant.setPlantedAt(LocalDateTime.now());
        plant.setIsAlive(succeeded);

        if (succeeded) {
            user.setCoin(user.getCoin() + species.getCoinCost());
            userRepository.save(user);
        }

        return forestRepository.save(plant);
    }
}