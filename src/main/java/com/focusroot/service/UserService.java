package com.focusroot.service;

import com.focusroot.dto.response.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public UserResponse getMyProfile() {
        // Logic mock tạm thời, sẽ lấy từ Security Context ở Giai đoạn 2
        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setEmail("user@focusroot.com");
        response.setCoinBalance(100);
        return response;
    }
}
