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
@Transactional
public MyForest buyTree(String username, Long speciesId) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
            
    TreeSpecies species = treeSpeciesRepository.findById(speciesId)
            .orElseThrow(() -> new EntityNotFoundException("Tree species not found"));

    // Kiểm tra điều kiện số dư coin của tài khoản
    if (user.getTotalCoins() < species.getBuyCostCoins()) {
        throw new IllegalArgumentException("Insufficient coins to buy this tree");
    }

    // Trừ coin atomic trực tiếp trên đối tượng được quản lý bởi Hibernate Persistence Context
    user.setTotalCoins(user.getTotalCoins() - species.getBuyCostCoins());
    userRepository.save(user);

    // Tiến hành gieo cây mới vào khu rừng ở trạng thái PURCHASED
    MyForest plant = new MyForest();
    plant.setUser(user);
    plant.setTreeSpecies(species);
    plant.setPlantedAt(LocalDateTime.now());
    plant.setStatus("PURCHASED");

    return forestRepository.save(plant);
}
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

    /**
     * Core logic nghiệp vụ Tuần 3: Xử lý gieo cây mọc hoặc cây chết héo sau khi phiên kết thúc.
     * Hàm này sẽ tự động chạy thông qua Spring Event Listener từ cấu phần của Bắc gửi sang.
     */
    public MyForest handleSessionEnd(User user, TreeSpecies species, String sessionStatus) {
        MyForest plant = new MyForest();
        plant.setUser(user);
        plant.setTreeSpecies(species);
        plant.setPlantedAt(LocalDateTime.now());

        if ("COMPLETED".equals(sessionStatus)) {
            plant.setStatus("ALIVE");
            // Logic cộng điểm thưởng tích lũy cho người dùng sau khi tập trung thành công
            user.setTotalPoints(user.getTotalPoints() + species.getCostPoints());
            userRepository.save(user);
        } else {
            plant.setStatus("WITHERED"); // Cây héo rũ nếu phiên làm việc thất bại/bỏ cuộc
        }

        return forestRepository.save(plant);
    }
}