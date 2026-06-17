package com.focusroot.auth;

import com.focusroot.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET =
            "test-secret-key-for-unit-tests-minimum-32-chars!!";

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L, 86_400_000L);
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash("hashed")
                .build();
    }

    @Test
    void generateAccessToken_isValid_andNotRefresh() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.isRefreshToken(token)).isFalse();
    }

    @Test
    void generateRefreshToken_isValid_andIsRefresh() {
        String token = jwtService.generateRefreshToken(testUser);

        assertThat(jwtService.isValid(token)).isTrue();
        assertThat(jwtService.isRefreshToken(token)).isTrue();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void extractUserId_returnsCorrectId() {
        String token = jwtService.generateAccessToken(testUser);

        assertThat(jwtService.extractUserId(token)).isEqualTo(1L);
    }

    @Test
    void isValid_expiredToken_returnsFalse() {
        JwtService shortLived = new JwtService(SECRET, -1000L, -1000L);
        String token = shortLived.generateAccessToken(testUser);

        assertThat(shortLived.isValid(token)).isFalse();
    }

    @Test
    void isValid_tamperedToken_returnsFalse() {
        String token = jwtService.generateAccessToken(testUser) + "tampered";

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void isValid_randomString_returnsFalse() {
        assertThat(jwtService.isValid("not.a.jwt")).isFalse();
    }
}
