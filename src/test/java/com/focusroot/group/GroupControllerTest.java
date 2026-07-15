package com.focusroot.group;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.focusroot.auth.JwtService;
import com.focusroot.auth.UserPrincipal;
import com.focusroot.user.User;

@WebMvcTest(
        controllers = GroupController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class
)
class GroupControllerTest {

    @Autowired MockMvc mvc;

    @MockBean GroupService groupService;
    @MockBean JwtService jwtService;
    @MockBean UserDetailsService userDetailsService;

    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@test.com")
                .passwordHash("hash")
                .build();
        principal = UserPrincipal.from(user);
    }

    @Test
    @DisplayName("GET /groups/{id}/members trả 200 với trạng thái focusing của từng member")
    void getGroupMembers_shouldReturn200_withFocusingStatus() throws Exception {
        when(groupService.getGroupMembers("alice", 7L)).thenReturn(List.of(
                GroupMemberResponse.builder()
                        .id(1L)
                        .groupId(7L)
                        .groupName("Deep Work")
                        .username("alice")
                        .status(GroupMember.Status.ACTIVE)
                        .focusing(true)
                        .joinedAt(LocalDateTime.now())
                        .build()
        ));

        mvc.perform(get("/groups/7/members").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].groupId").value(7))
                .andExpect(jsonPath("$.data[0].username").value("alice"))
                .andExpect(jsonPath("$.data[0].focusing").value(true));
    }

    @Test
    @DisplayName("GET /groups/{id}/members không có auth trả 401")
    void getGroupMembers_shouldReturn401_withoutAuth() throws Exception {
        mvc.perform(get("/groups/7/members"))
                .andExpect(status().isUnauthorized());
    }
}
