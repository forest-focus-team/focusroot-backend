package com.focusroot.common;

import java.time.Instant;

/**
 * Format lỗi chuẩn toàn hệ thống: { code, message, timestamp }.
 * Dùng trong GlobalExceptionHandler thay cho ApiResponse.error().
 *
 * BREAKING CHANGE (so với ApiResponse cũ):
 *   Trước: { "success": false, "message": "..." }
 *   Sau:   { "code": "NOT_FOUND", "message": "...", "timestamp": "2026-..." }
 * FE cần cập nhật: thay vì check response.success === false, hãy check HTTP status code.
 */
public record ErrorResponse(String code, String message, String timestamp) {

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, Instant.now().toString());
    }
}
