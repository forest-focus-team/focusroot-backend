package com.focusroot.forest;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForestServiceTest {

    @Mock
    private ForestRepository forestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TreeSpeciesRepository treeSpeciesRepository;

    @InjectMocks
    private ForestService forestService;

    @Test
    public void testHandleSessionEnd_Success() {
        User user = new User();
        user.setCoin(100);

        TreeSpecies species = new TreeSpecies();
        species.setCoinCost(50);

        MyForest saved = new MyForest();
        saved.setIsAlive(true);

        when(forestRepository.save(any(MyForest.class))).thenReturn(saved);

        MyForest result = forestService.handleSessionEnd(user, species, true);

        assertTrue(result.getIsAlive());
        assertEquals(150, user.getCoin());
        verify(forestRepository, times(1)).save(any(MyForest.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testHandleSessionEnd_Failed() {
        User user = new User();
        user.setCoin(100);

        TreeSpecies species = new TreeSpecies();
        species.setCoinCost(50);

        MyForest saved = new MyForest();
        saved.setIsAlive(false);

        when(forestRepository.save(any(MyForest.class))).thenReturn(saved);

        MyForest result = forestService.handleSessionEnd(user, species, false);

        assertFalse(result.getIsAlive());
        assertEquals(100, user.getCoin());
        verify(forestRepository, times(1)).save(any(MyForest.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
