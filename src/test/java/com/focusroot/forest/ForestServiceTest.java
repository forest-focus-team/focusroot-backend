package com.focusroot.forest;

import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForestServiceTest {

    @Mock
    private ForestRepository forestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ForestService forestService;

    @Test
    public void testHandleSessionEnd_Success() {
        User user = new User();
        user.setTotalPoints(100L);

        TreeSpecies species = new TreeSpecies();
        species.setCostPoints(50);

        MyForest expectedForest = new MyForest();
        expectedForest.setStatus("ALIVE");

        when(forestRepository.save(any(MyForest.class))).thenReturn(expectedForest);

        MyForest result = forestService.handleSessionEnd(user, species, "COMPLETED");

        assertEquals("ALIVE", result.getStatus());
        assertEquals(150L, user.getTotalPoints());
        verify(forestRepository, times(1)).save(any(MyForest.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testHandleSessionEnd_Failed() {
        User user = new User();
        user.setTotalPoints(100L);

        TreeSpecies species = new TreeSpecies();
        species.setCostPoints(50);

        MyForest expectedForest = new MyForest();
        expectedForest.setStatus("WITHERED");

        when(forestRepository.save(any(MyForest.class))).thenReturn(expectedForest);

        MyForest result = forestService.handleSessionEnd(user, species, "FAILED");

        assertEquals("WITHERED", result.getStatus());
        assertEquals(100L, user.getTotalPoints()); // Không được cộng điểm
        verify(forestRepository, times(1)).save(any(MyForest.class));
        verify(userRepository, never()).save(any(User.class));
    }
}