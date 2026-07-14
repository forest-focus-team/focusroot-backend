package com.focusroot.forest;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;package com.focusroot.forest;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForestShopTest {

    @Mock private ForestRepository forestRepository;
    @Mock private UserRepository userRepository;
    @Mock private TreeSpeciesRepository treeSpeciesRepository;
    @InjectMocks private ForestService forestService;

    @Test
    public void testBuyTree_Success_EnoughCoins() {
        // Sử dụng builder khớp với cấu trúc User và TreeSpecies mới
        User user = User.builder().username("hungit").coin(100).build();
        TreeSpecies species = TreeSpecies.builder().id(2L).coinCost(20).build();
        MyForest expected = MyForest.builder().isAlive(true).build();

        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(2L)).thenReturn(Optional.of(species));
        when(forestRepository.save(any(MyForest.class))).thenReturn(expected);

        MyForest result = forestService.buyTree("hungit", 2L);
        assertTrue(result.getIsAlive()); // Cây mua mặc định trạng thái isAlive = true
        assertEquals(80, user.getCoin()); // 100 - 20 = 80 (Kiểu Integer)
    }

    @Test
    public void testBuyTree_Failed_InsufficientCoins() {
        User user = User.builder().username("hungit").coin(10).build();
        TreeSpecies species = TreeSpecies.builder().id(2L).coinCost(20).build();

        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(2L)).thenReturn(Optional.of(species));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            forestService.buyTree("hungit", 2L);
        });
        assertEquals("Insufficient coins to buy this tree", exception.getMessage());
        assertEquals(10, user.getCoin()); // Không bị trừ coin
    }

    @Test
    public void testBuyTree_Failed_TreeNotFound() {
        User user = User.builder().username("hungit").build();
        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            forestService.buyTree("hungit", 99L);
        });
    }
}
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForestShopTest {

    @Mock private ForestRepository forestRepository;
    @Mock private UserRepository userRepository;
    @Mock private TreeSpeciesRepository treeSpeciesRepository;
    @InjectMocks private ForestService forestService;

    @Test
    public void testBuyTree_Success_EnoughCoins() {
        User user = new User(); user.setUsername("hungit"); user.setTotalCoins(100L);
        TreeSpecies species = new TreeSpecies(); species.setId(2L); species.setBuyCostCoins(20);
        MyForest expected = new MyForest(); expected.setStatus("PURCHASED");

        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(2L)).thenReturn(Optional.of(species));
        when(forestRepository.save(any(MyForest.class))).thenReturn(expected);

        MyForest result = forestService.buyTree("hungit", 2L);
        assertEquals("PURCHASED", result.getStatus());
        assertEquals(80L, user.getTotalCoins()); // 100 - 20 = 80
    }

    @Test
    public void testBuyTree_Failed_InsufficientCoins() {
        User user = new User(); user.setUsername("hungit"); user.setTotalCoins(10L);
        TreeSpecies species = new TreeSpecies(); species.setId(2L); species.setBuyCostCoins(20);

        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(2L)).thenReturn(Optional.of(species));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            forestService.buyTree("hungit", 2L);
        });
        assertEquals("Insufficient coins to buy this tree", exception.getMessage());
        assertEquals(10L, user.getTotalCoins()); // Không bị trừ coin
    }

    @Test
    public void testBuyTree_Failed_TreeNotFound() {
        User user = new User(); user.setUsername("hungit");
        when(userRepository.findByUsername("hungit")).thenReturn(Optional.of(user));
        when(treeSpeciesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            forestService.buyTree("hungit", 99L);
        });
    }
}
