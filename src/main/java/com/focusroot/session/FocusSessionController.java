package com.focusroot.session;

import com.focusroot.dto.request.session.StartSessionRequest;
import com.focusroot.dto.request.session.EndSessionRequest;
import com.focusroot.dto.response.session.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.focusroot.auth.UserDetailsImpl;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "3. Focus Session Module", description = "Quản lý các phiên tập trung tính giờ của người dùng")
public class FocusSessionController {
    
    private final FocusSessionService sessionService;

    @PostMapping("/start")
    @Operation(summary = "Bắt đầu phiên tập trung mới", description = "Tạo phiên ở trạng thái ACTIVE, yêu cầu kiểm tra validation trùng lặp và loại cây hợp lệ.")
    public ResponseEntity<SessionResponse> start(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @Valid @RequestBody StartSessionRequest req) {
        return ResponseEntity.ok(sessionService.startSession(user.getId(), req));
    }

    @PostMapping("/end")
    @Operation(summary = "Kết thúc phiên tập trung hiện tại", description = "Tính toán thời gian tập trung thực tế, cộng tiền vàng (Coin) nếu thành công hoặc gán FAILED nếu thoát app giữa chừng.")
    public ResponseEntity<SessionResponse> end(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @Valid @RequestBody EndSessionRequest req) {
        return ResponseEntity.ok(sessionService.endSession(user.getId(), req));
    }

    @GetMapping("/history")
    @Operation(summary = "Lấy lịch sử phiên tập trung (Phân trang)", description = "Trả về danh sách lịch sử phiên tập trung của chính User đăng nhập, sắp xếp giảm dần theo thời gian tạo.")
    public ResponseEntity<Page<SessionResponse>> history(
            @AuthenticationPrincipal UserDetailsImpl user, 
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(sessionService.getHistory(user.getId(), pageable));
    }
}
