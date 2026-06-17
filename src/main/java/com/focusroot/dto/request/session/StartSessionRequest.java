package com.focusroot.dto.request.session;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartSessionRequest {

    @NotNull(message = "ID loài cây không được để trống")
    private Long treeId;

    @NotNull(message = "Thời gian tập trung không được để trống")
    @Min(value = 5, message = "Thời gian tối thiểu là 5 phút")
    @Max(value = 180, message = "Thời gian tối đa là 180 phút")
    private Integer durationMinutes;
}
