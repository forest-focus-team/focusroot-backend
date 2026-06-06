package com.focusroot.service;

import com.focusroot.dto.request.auth.RegisterRequest;
import com.focusroot.dto.request.auth.LoginRequest;
import com.focusroot.repository.UserRepository;
import com.focusroot.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống!");
        }
        
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword()); // Sẽ thêm mã hóa BCrypt ở Giai đoạn 2
        userRepository.save(newUser);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Sai email hoặc mật khẩu!"));
        
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Sai email hoặc mật khẩu!");
        }
        return "mock-jwt-token-for-" + user.getEmail();
    }
}
