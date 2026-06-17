package com.focusroot.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Email
    private String email;
}
