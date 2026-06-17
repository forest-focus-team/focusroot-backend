-- =============================================================
-- FocusRoot — 02-triggers.sql
-- =============================================================
-- ⚠️  CẢNH BÁO DOUBLE-COUNT — ĐỌC TRƯỚC KHI DÙNG
-- =============================================================
-- SessionService.endSession() ĐÃ xử lý:
--   user.coin += coinsEarned          (dòng: user.setCoin(...))
--   user.total_focus_minutes += mins  (dòng: user.setTotalFocusMinutes(...))
-- rồi gọi userRepository.save(user) trong @Transactional.
--
-- Nếu trigger CŨNG cộng hai cột này, mỗi session SUCCESS sẽ bị
-- tính GẤP ĐÔI. Ví dụ: 30 phút focus → coin +60 thay vì +30.
--
-- ✅ QUYẾT ĐỊNH: Dùng Java (SessionService) làm nguồn DUY NHẤT
--    cho users.coin và users.total_focus_minutes.
--    Trigger chỉ phụ trách user_activity_logs (SessionService
--    không cập nhật bảng này).
--
-- Nếu sau này muốn chuyển sang trigger, hãy XÓA hai dòng
-- setCoin/setTotalFocusMinutes trong SessionService.endSession()
-- rồi bỏ comment phần "OPTION B" bên dưới.
-- =============================================================

DELIMITER $$

-- -------------------------------------------------------------
-- Trigger: upsert user_activity_logs sau mỗi session kết thúc
-- -------------------------------------------------------------
CREATE TRIGGER trg_fs_after_update
AFTER UPDATE ON focus_sessions
FOR EACH ROW
BEGIN
    -- Chỉ xử lý khi status vừa chuyển từ IN_PROGRESS → SUCCESS/FAILED
    IF NEW.status IN ('SUCCESS', 'FAILED') AND OLD.status = 'IN_PROGRESS' THEN

        -- Upsert bản ghi ngày hôm đó
        INSERT INTO user_activity_logs
            (user_id, log_date, total_minutes, session_count, success_count, created_at)
        VALUES (
            NEW.user_id,
            COALESCE(DATE(NEW.end_time), CURDATE()),
            IFNULL(NEW.actual_duration, 0),
            1,
            IF(NEW.status = 'SUCCESS', 1, 0),
            NOW(6)
        )
        ON DUPLICATE KEY UPDATE
            total_minutes = total_minutes + IFNULL(NEW.actual_duration, 0),
            session_count = session_count + 1,
            success_count = success_count + IF(NEW.status = 'SUCCESS', 1, 0);

        -- -------------------------------------------------------
        -- OPTION B (hiện TẮT): Nếu muốn trigger quản lý coin/mins
        -- thay vì Java, bỏ comment 4 dòng này VÀ xóa setCoin /
        -- setTotalFocusMinutes trong SessionService.endSession().
        -- -------------------------------------------------------
        -- IF NEW.status = 'SUCCESS' THEN
        --     UPDATE users
        --     SET coin                = coin + IFNULL(NEW.coin_earned, 0),
        --         total_focus_minutes = total_focus_minutes + IFNULL(NEW.actual_duration, 0)
        --     WHERE id = NEW.user_id;
        -- END IF;

    END IF;
END $$

DELIMITER ;
