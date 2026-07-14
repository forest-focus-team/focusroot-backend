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

    /**
     * Lấy danh sách Timeline khu rừng của người dùng (Đã sắp xếp theo thời gian trồng mới nhất)
     */
    public List<MyForest> getForest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return forestRepository.findByUserOrderByPlantedAtDesc(user);
    }

    /**
     * Lấy danh sách tất cả các loài cây hiện có
     */
    public List<TreeSpecies> getAllSpecies() {
        return treeSpeciesRepository.findAll();
    }

    /**
     * Nghiệp vụ Mua cây bằng coin tại Shop (Kiểm soát an toàn ví coin, không âm tiền)
     */
    public MyForest buyTree(String username, Long speciesId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
                
        TreeSpecies species = treeSpeciesRepository.findById(speciesId)
                .orElseThrow(() -> new EntityNotFoundException("Tree species not found"));

        // Kiểm tra số dư coin của tài khoản (Trường coin mới dạng Integer)
        if (user.getCoin() < species.getCoinCost()) {
            throw new IllegalArgumentException("Insufficient coins to buy this tree");
        }

        // Trừ coin an toàn (Atomic) trên Persistent Context
        user.setCoin(user.getCoin() - species.getCoinCost());
        userRepository.save(user);

        // Gieo cây mới với trạng thái isAlive = true (Cây mua được coi là sống)
        MyForest plant = MyForest.builder()
                .user(user)
                .treeSpecies(species)
                .plantedAt(LocalDateTime.now())
                .isAlive(true)
                .build();

        return forestRepository.save(plant);
    }

    /**
     * Xử lý gieo cây mọc hoặc cây chết héo sau khi phiên kết thúc (Spring Event Listener)
     */
    public MyForest handleSessionEnd(User user, TreeSpecies species, boolean succeeded) {
        MyForest plant = new MyForest();
        plant.setUser(user);
        plant.setTreeSpecies(species);
        plant.setPlantedAt(LocalDateTime.now());
        plant.setIsAlive(succeeded);

        if (succeeded) {
            // Sử dụng trường coin mới được refactor
            user.setCoin(user.getCoin() + species.getCoinCost());
            userRepository.save(user);
        }

        return forestRepository.save(plant);
    }
}
