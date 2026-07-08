package com.focusroot.integration;

import com.focusroot.auth.UserPrincipal;
import com.focusroot.forest.ForestRepository;
import com.focusroot.forest.MyForest;
import com.focusroot.forest.TreeSpecies;
import com.focusroot.forest.TreeSpeciesRepository;
import com.focusroot.session.FocusSession;
import com.focusroot.session.SessionRepository;
import com.focusroot.user.User;
import com.focusroot.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test bảo vệ chống lỗi serialize entity JPA có quan hệ LAZY qua HTTP.
 *
 * <p>Bối cảnh (phát hiện demo Tuần 5, ngoài phạm vi #43): {@code SessionController} và
 * {@code ForestController} trả thẳng entity ({@link FocusSession}, {@link MyForest}) có quan hệ
 * LAZY ({@code user}, {@code treeSpecies}, {@code focusSession}). Với {@code spring.jpa.open-in-view: false},
 * transaction của service đã đóng trước khi Jackson serialize → {@code LazyInitializationException}
 * → {@code HttpMessageNotWritableException} → HTTP 500. Business logic vẫn đúng (cây đã lưu),
 * chỉ chết ở khâu serialize.
 *
 * <p>Các test tầng service/{@code @WebMvcTest} (mock service) KHÔNG bắt được lỗi này vì không có
 * ranh giới transaction JPA thật. Test này cố tình dùng {@code @SpringBootTest} full-context +
 * MockMvc và <b>không</b> {@code @Transactional} ở tầng test, để tx của service đóng trước khi
 * serialize — tái hiện đúng luồng runtime.
 *
 * <p>Đỏ khi {@code open-in-view: false}; xanh sau khi bật {@code open-in-view: true}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=never")
class EntitySerializationIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired UserRepository userRepository;
    @Autowired TreeSpeciesRepository treeSpeciesRepository;
    @Autowired SessionRepository sessionRepository;
    @Autowired ForestRepository forestRepository;

    private UserPrincipal principal;
    private TreeSpecies species;

    @BeforeEach
    void setUp() {
        // Dọn theo thứ tự an toàn khoá ngoại (không @Transactional nên dữ liệu tồn giữa các test)
        forestRepository.deleteAll();
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        treeSpeciesRepository.deleteAll();

        User user = userRepository.save(User.builder()
                .username("alice")
                .email("alice@test.com")
                .passwordHash("$2a$10$fakehashfakehashfakehash")
                .coin(0)
                .totalFocusMinutes(0)
                .build());
        principal = UserPrincipal.from(user);

        species = treeSpeciesRepository.save(TreeSpecies.builder()
                .name("Mầm xanh")
                .description("Cây khởi đầu")
                .imageUrl("https://example.com/mam-xanh.png")
                .requiredMinutes(1)
                .coinCost(0)
                .build());
    }

    @Test
    @DisplayName("POST /sessions/{id}/end → 200, serialize được quan hệ LAZY, không lộ passwordHash")
    void endSession_serializesLazyRelations_withoutLeakingPasswordHash() throws Exception {
        User user = userRepository.findByUsername("alice").orElseThrow();
        FocusSession session = sessionRepository.save(FocusSession.builder()
                .user(user)
                .treeSpecies(species)
                .startTime(LocalDateTime.now().minusMinutes(5))
                .plannedDuration(1)          // actual (~5') ≥ planned (1') → SUCCESS → trồng cây sống
                .status(FocusSession.Status.IN_PROGRESS)
                .coinEarned(0)
                .build());

        mvc.perform(post("/sessions/{id}/end", session.getId())
                        .param("giveUp", "false")
                        .with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SUCCESS"))
                // Quan hệ LAZY phải serialize được (đây là chỗ trước đây ném LazyInitializationException)
                .andExpect(jsonPath("$.data.treeSpecies.name").value("Mầm xanh"))
                .andExpect(jsonPath("$.data.user.username").value("alice"))
                // Bảo mật: passwordHash phải bị @JsonIgnore
                .andExpect(jsonPath("$.data.user.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("GET /forest → 200, serialize được cây đã trồng kèm quan hệ LAZY")
    void getForest_serializesLazyRelations_withoutLeakingPasswordHash() throws Exception {
        User user = userRepository.findByUsername("alice").orElseThrow();
        FocusSession session = sessionRepository.save(FocusSession.builder()
                .user(user)
                .treeSpecies(species)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now())
                .plannedDuration(1)
                .actualDuration(10)
                .status(FocusSession.Status.SUCCESS)
                .coinEarned(10)
                .build());
        forestRepository.save(MyForest.builder()
                .user(user)
                .focusSession(session)
                .treeSpecies(species)
                .plantedAt(LocalDateTime.now())
                .isAlive(true)
                .build());

        mvc.perform(get("/forest").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].treeSpecies.name").value("Mầm xanh"))
                .andExpect(jsonPath("$.data[0].user.username").value("alice"))
                .andExpect(jsonPath("$.data[0].isAlive").value(true))
                .andExpect(jsonPath("$.data[0].user.passwordHash").doesNotExist());
    }

    @Test
    @DisplayName("GET /sessions/history → 200, danh sách phiên serialize được quan hệ LAZY")
    void getHistory_serializesLazyRelations_asList() throws Exception {
        User user = userRepository.findByUsername("alice").orElseThrow();
        // Một phiên đã kết thúc để history có phần tử; user + treeSpecies là quan hệ LAZY
        sessionRepository.save(FocusSession.builder()
                .user(user)
                .treeSpecies(species)
                .startTime(LocalDateTime.now().minusMinutes(30))
                .endTime(LocalDateTime.now().minusMinutes(5))
                .plannedDuration(25)
                .actualDuration(25)
                .status(FocusSession.Status.SUCCESS)
                .coinEarned(25)
                .build());

        mvc.perform(get("/sessions/history").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                // Endpoint trả List<FocusSession> — mỗi phần tử có quan hệ LAZY phải serialize được
                .andExpect(jsonPath("$.data[0].treeSpecies.name").value("Mầm xanh"))
                .andExpect(jsonPath("$.data[0].user.username").value("alice"))
                .andExpect(jsonPath("$.data[0].status").value("SUCCESS"))
                // Bảo mật: passwordHash không được lộ trong danh sách
                .andExpect(jsonPath("$.data[0].user.passwordHash").doesNotExist());
    }
}
