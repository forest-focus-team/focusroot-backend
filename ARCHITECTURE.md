# Architecture — FocusRoot Backend

## Tổng quan kiến trúc

FocusRoot Backend tuân theo mô hình **Layered Architecture 3 tầng** kết hợp
**Package-by-Feature** để dễ phân chia công việc trong nhóm.

```
┌─────────────────────────────────────────────────────┐
│                   CLIENT (Mobile/Web)               │
└────────────────────┬────────────────────────────────┘
                     │ HTTP / WebSocket
┌────────────────────▼────────────────────────────────┐
│              PRESENTATION LAYER                     │
│  Controllers (REST)  │  WebSocket Handlers (STOMP)  │
│  AuthController      │  GroupSessionHandler         │
│  UserController      │                              │
│  SessionController   │  Swagger UI (/swagger-ui)    │
│  ForestController    │                              │
│  GroupController     │                              │
│  PredictionController│                              │
└────────────────────┬────────────────────────────────┘
                     │ calls
┌────────────────────▼────────────────────────────────┐
│               BUSINESS LOGIC LAYER                  │
│  AuthService  │ UserService  │ SessionService        │
│  ForestService│ GroupService │ PredictionService     │
│                                                     │
│  Cross-cutting: JwtFilter, GlobalExceptionHandler   │
└────────────────────┬────────────────────────────────┘
                     │ JPA
┌────────────────────▼────────────────────────────────┐
│                DATA ACCESS LAYER                    │
│  UserRepository       │ SessionRepository           │
│  ForestRepository     │ GroupRepository             │
│  TreeSpeciesRepository│ GroupMemberRepository       │
│  GroupSessionRepository│ UserActivityLogRepository  │
└────────────────────┬────────────────────────────────┘
                     │ JDBC
┌────────────────────▼────────────────────────────────┐
│                  MySQL 8.0 Database                 │
│  users │ focus_sessions │ my_forest │ tree_species  │
│  focus_groups │ group_members │ group_sessions      │
│  user_activity_logs                                 │
└─────────────────────────────────────────────────────┘
```

## Luồng xử lý một request HTTP

```
Request → JwtFilter → Controller → Service → Repository → DB
                ↓                      ↓
          (401 nếu         GlobalExceptionHandler
          token invalid)   (xử lý lỗi tập trung)
```

**Chi tiết:**

1. Request tới `JwtFilter` — extract và validate JWT token
2. Nếu hợp lệ → set `SecurityContext` → request đi tiếp
3. `@RestController` nhận request, validate input (`@Valid`)
4. Controller gọi `Service` — đây là nơi chứa business logic
5. Service gọi `Repository` (interface JPA) để truy vấn DB
6. Kết quả trả về được wrap trong `ApiResponse<T>`
7. Nếu có exception → `GlobalExceptionHandler` bắt và trả HTTP error phù hợp

## Luồng WebSocket (STOMP)

```
Client → /ws (SockJS) → WebSocketConfig → STOMP Broker
                                              ↓
                                    GroupSessionHandler
                                    @MessageMapping("/app/groups/{id}/...")
                                              ↓
                                    Broadcast → /topic/groups/{id}/...
                                              ↓
                                    Tất cả client subscribe cùng topic
```

## Mô tả từng package

| Package | Trách nhiệm |
|---|---|
| `auth` | JWT generation/validation, Spring Security config, login/register |
| `user` | User entity, profile management |
| `session` | Focus session lifecycle (start → in_progress → success/failed) |
| `forest` | MyForest (cây đã trồng), TreeSpecies (loại cây có thể trồng) |
| `group` | FocusGroup, GroupMember, GroupSession (focus cùng nhau) |
| `prediction` | UserActivityLog, thuật toán dự đoán tỷ lệ thành công |
| `websocket` | STOMP endpoint config, realtime event handlers |
| `common` | ApiResponse wrapper, GlobalExceptionHandler, AppConstants |

## Database Schema (ERD text)

```
users
  id PK | username UNIQUE | email UNIQUE | password_hash
  coin | total_focus_minutes | is_active | created_at | updated_at

tree_species
  id PK | name | description | image_url | required_minutes | coin_cost

focus_sessions
  id PK | user_id FK→users | tree_species_id FK→tree_species
  start_time | end_time | planned_duration | actual_duration
  status ENUM(IN_PROGRESS,SUCCESS,FAILED) | coin_earned

my_forest
  id PK | user_id FK→users | focus_session_id FK→focus_sessions (UNIQUE)
  tree_species_id FK→tree_species | planted_at | is_alive

focus_groups
  id PK | name | owner_id FK→users | created_at | is_active | penalty_coins

group_members
  id PK | group_id FK→focus_groups | user_id FK→users
  joined_at | status ENUM(ACTIVE,LEFT,KICKED)
  UNIQUE(group_id, user_id)

group_sessions
  id PK | group_id FK→focus_groups | start_time | end_time
  status ENUM(WAITING,IN_PROGRESS,COMPLETED,FAILED)

user_activity_logs
  id PK | user_id FK→users | log_date | total_minutes
  session_count | success_count | created_at
  UNIQUE(user_id, log_date)
```
