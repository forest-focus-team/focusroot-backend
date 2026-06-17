# FocusRoot — ERD & Quan hệ bảng

> Sinh từ 8 JPA entities, tuần 2 — 03/06/2026

---

## Sơ đồ ERD (Mermaid)

```mermaid
erDiagram
    users {
        bigint      id              PK
        varchar50   username        UK "NOT NULL"
        varchar100  email           UK "NOT NULL"
        varchar255  password_hash      "NOT NULL"
        int         coin               "DEFAULT 0"
        int         total_focus_minutes "DEFAULT 0"
        datetime6   created_at         "NOT NULL"
        datetime6   updated_at
        bit1        is_active          "DEFAULT 1"
    }
    tree_species {
        bigint      id              PK
        varchar100  name               "NOT NULL"
        text        description
        varchar500  image_url
        int         required_minutes   "NOT NULL"
        int         coin_cost          "DEFAULT 0"
    }
    focus_sessions {
        bigint      id              PK
        bigint      user_id         FK "NOT NULL"
        bigint      tree_species_id FK
        datetime6   start_time         "NOT NULL"
        datetime6   end_time
        int         planned_duration   "NOT NULL"
        int         actual_duration
        varchar20   status             "IN_PROGRESS|SUCCESS|FAILED"
        int         coin_earned        "DEFAULT 0"
    }
    my_forest {
        bigint      id              PK
        bigint      user_id         FK "NOT NULL"
        bigint      focus_session_id FK UK
        bigint      tree_species_id FK
        datetime6   planted_at         "NOT NULL"
        bit1        is_alive           "DEFAULT 1"
    }
    focus_groups {
        bigint      id              PK
        varchar100  name               "NOT NULL"
        bigint      owner_id        FK "NOT NULL"
        datetime6   created_at         "NOT NULL"
        bit1        is_active          "DEFAULT 1"
        int         penalty_coins      "DEFAULT 0"
    }
    group_members {
        bigint      id              PK
        bigint      group_id        FK "NOT NULL"
        bigint      user_id         FK "NOT NULL"
        datetime6   joined_at          "NOT NULL"
        varchar10   status             "ACTIVE|LEFT|KICKED"
    }
    group_sessions {
        bigint      id              PK
        bigint      group_id        FK "NOT NULL"
        datetime6   start_time
        datetime6   end_time
        varchar20   status             "WAITING|IN_PROGRESS|COMPLETED|FAILED"
    }
    user_activity_logs {
        bigint      id              PK
        bigint      user_id         FK "NOT NULL"
        date        log_date           "NOT NULL"
        int         total_minutes      "DEFAULT 0"
        int         session_count      "DEFAULT 0"
        int         success_count      "DEFAULT 0"
        datetime6   created_at         "NOT NULL"
    }

    users           ||--o{ focus_sessions      : "thực hiện"
    users           ||--o{ my_forest           : "sở hữu"
    users           ||--o{ focus_groups        : "tạo/sở hữu"
    users           ||--o{ group_members       : "tham gia qua"
    users           ||--o{ user_activity_logs  : "được tổng hợp vào"
    tree_species    ||--o{ focus_sessions      : "được dùng trong"
    tree_species    ||--o{ my_forest           : "được trồng dạng"
    focus_sessions  ||--o| my_forest           : "tạo ra 1 cây"
    focus_groups    ||--o{ group_members       : "có thành viên"
    focus_groups    ||--o{ group_sessions      : "tiến hành"
```

---

## Bảng quan hệ chi tiết

| Bảng con | Cột FK | Bảng cha | Cardinality | Ghi chú |
|---|---|---|---|---|
| focus_sessions | user_id | users | N:1 | Mỗi session thuộc 1 user |
| focus_sessions | tree_species_id | tree_species | N:1 | Nullable — session không bắt buộc chọn cây |
| my_forest | user_id | users | N:1 | Mỗi cây thuộc 1 user |
| my_forest | focus_session_id | focus_sessions | 1:1 UK | 1 session → tối đa 1 cây (UNIQUE) |
| my_forest | tree_species_id | tree_species | N:1 | Nullable — có thể không chọn loài |
| focus_groups | owner_id | users | N:1 | Người tạo nhóm |
| group_members | group_id | focus_groups | N:1 | |
| group_members | user_id | users | N:1 | UNIQUE(group_id, user_id) |
| group_sessions | group_id | focus_groups | N:1 | |
| user_activity_logs | user_id | users | N:1 | UNIQUE(user_id, log_date) |

---

## Ghi chú Denormalization

| Cột | Bảng | Lý do giữ (không chuẩn hóa thêm) |
|---|---|---|
| `users.coin` | users | Denorm — có thể tính từ `SUM(focus_sessions.coin_earned)`. Giữ để truy vấn O(1) cho hiển thị realtime. |
| `users.total_focus_minutes` | users | Denorm — có thể tính từ `SUM(focus_sessions.actual_duration) WHERE status='SUCCESS'`. Giữ cho dashboard nhanh. |
| `my_forest.tree_species_id` | my_forest | Redundant với `focus_sessions.tree_species_id`. Giữ để query rừng mà không JOIN focus_sessions. |
| `focus_sessions.coin_earned` | focus_sessions | Có thể tính lại từ `actual_duration`, nhưng giữ lịch sử kiếm coin chính xác (nếu công thức thưởng thay đổi sau). |

---

## Indexes tổng hợp

| Index | Bảng | Cột | Mục đích |
|---|---|---|---|
| idx_fs_user_id | focus_sessions | user_id | Lấy lịch sử session của user |
| idx_fs_user_status | focus_sessions | (user_id, status) | Check session IN_PROGRESS của user |
| idx_mf_user_id | my_forest | user_id | Render rừng cây của user |
| idx_fg_owner_id | focus_groups | owner_id | Danh sách nhóm do user tạo |
| idx_gm_group_id | group_members | group_id | Danh sách thành viên nhóm |
| idx_gm_user_id | group_members | user_id | Các nhóm user đang tham gia |
| idx_gses_group_id | group_sessions | group_id | Sessions của nhóm |
| idx_ual_user_id | user_activity_logs | user_id | Lịch sử hoạt động user |
| idx_ual_log_date | user_activity_logs | log_date | Lọc theo ngày (prediction) |
