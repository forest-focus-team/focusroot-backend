package com.focusroot.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Bean chung cho toàn ứng dụng.
 * PasswordEncoder được tách riêng ra đây để tránh circular dependency:
 *   SecurityConfig -> JwtFilter -> AuthService -> PasswordEncoder -> SecurityConfig
 */
@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
