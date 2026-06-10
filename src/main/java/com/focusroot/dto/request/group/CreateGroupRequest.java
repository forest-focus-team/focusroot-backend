// File: CreateGroupRequest.java
package com.focusroot.dto.request.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGroupRequest {
    @NotBlank(message = "Tên phòng không được để trống")
    private String groupName;

    @NotNull(message = "Cần chỉ định thời gian mục tiêu")
    private Integer targetDurationMinutes;
}
