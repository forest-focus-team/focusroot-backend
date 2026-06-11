package com.focusroot.dto.request.session;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {
    @NotNull(message = "Thời gian mục tiêu không được để trống")
    @Min(value = 5, message = "Thời gian tập trung tối thiểu là 5 phút")
    private Integer targetDuration;
}
