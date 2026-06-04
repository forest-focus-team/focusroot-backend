// File: EndSessionRequest.java
package com.focusroot.dto.request.session;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EndSessionRequest {
    @NotBlank(message = "Trạng thái kết thúc không được để trống")
    private String status; // "COMPLETED" hoặc "FAILED"
}
