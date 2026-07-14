package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.focusroot.auth.UserDetailsImpl; // Đảm bảo import đúng class UserDetails hiện tại của nhóm

@RestController
@RequestMapping("/api/sessions") // Sử dụng đường dẫn chuẩn duy nhất của hệ thống
@RequiredArgsConstructor
@Tag(name = "Focus Session", description = "API quản lý các phiên tập trung tính giờ của người dùng")
public class FocusSessionController {
    
    private final FocusSessionService sessionService;

    @PostMapping("/start")
    @Operation(summary = "Bắt đầu phiên tập trung mới", description = "Khởi tạo một phiên tập trung mới ở trạng thái ACTIVE cho người dùng hiện tại.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Khởi tạo phiên tập trung thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ hoặc loại cây không tồn tại"),
        @ApiResponse(responseCode = "400", description = "Đang có một phiên tập trung khác đang ACTIVE")
    })
    public ResponseEntity<SessionResponse> start(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @Valid @RequestBody StartSessionRequest req) {
        return ResponseEntity.ok(sessionService.startSession(user.getId(), req));
    }

    @PostMapping("/end")
    @Operation(summary = "Kết thúc phiên tập trung", description = "Tính toán thời gian tập trung thực tế, cộng xu (coin) nếu hoàn thành hoặc đánh dấu FAILED.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Kết thúc phiên thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy phiên tập trung nào đang ACTIVE để kết thúc")
    })
    public ResponseEntity<SessionResponse> end(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @Valid @RequestBody EndSessionRequest req) {
        return ResponseEntity.ok(sessionService.endSession(user.getId(), req));
    }

    @GetMapping("/history")
    @Operation(summary = "Lấy lịch sử phiên tập trung", description = "Trả về danh sách lịch sử phiên tập trung của chính User đăng nhập (hỗ trợ phân trang).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lấy danh sách lịch sử thành công")
    })
    public ResponseEntity<Page<SessionResponse>> history(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(sessionService.getHistory(user.getId(), pageable));
    }
}
