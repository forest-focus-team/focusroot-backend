// File: JoinGroupRequest.java
package com.focusroot.dto.request.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinGroupRequest {
    @NotBlank(message = "Mã tham gia (passcode) không được để trống")
    private String passcode;
}
