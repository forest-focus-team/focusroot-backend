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
     * Lấy danh sách Timeline khu rừng (Mặc định đã được Danh sort OrderByPlantedAtDesc cực chuẩn)
     */
    public List<MyForest> getForest(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return forestRepository.findByUserOrderByPlantedAtDesc(user);
    }

    public List<TreeSpecies> getAllSpecies() {
        return treeSpeciesRepository.findAll();
    }

    /**
     * Nghiệp vụ Phần 1: Mua cây bằng coin tại Cửa hàng
     * Đảm bảo kiểm tra điều kiện ví và trừ coin Atomic an toàn, chống số dư âm
     */
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

    /**
     * Core logic nghiệp vụ Tuần 3 & Cải tiến Tuần 5: Xử lý gieo cây mọc hoặc cây chết héo sau khi phiên kết thúc.
     * Đã bổ sung cơ chế cộng coin thưởng tích lũy khi thành công để User có tiền đi Shop mua cây.
     */
    public MyForest handleSessionEnd(User user, TreeSpecies species, String sessionStatus) {
        MyForest plant = new MyForest();
        plant.setUser(user);
        plant.setTreeSpecies(species);
        plant.setPlantedAt(LocalDateTime.now());

        if ("COMPLETED".equals(sessionStatus)) {
            plant.setStatus("ALIVE");
            
            // 1. Logic cộng điểm thưởng tích lũy cũ
            user.setTotalPoints(user.getTotalPoints() + species.getCostPoints());
            
            // 2. Logic bổ sung tuần 5: Thưởng 10 coins tạo dòng tiền dương khép kín cho tài khoản
            user.setTotalCoins(user.getTotalCoins() + 10);
            
            userRepository.save(user);
        } else {
            plant.setStatus("WITHERED"); // Cây héo rũ nếu phiên làm việc thất bại/bỏ cuộc
        }

        return forestRepository.save(plant);
    }
}