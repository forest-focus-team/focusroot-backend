-- =============================================================
-- FocusRoot — 01-schema.sql
-- MySQL 8.0 | utf8mb4_unicode_ci
-- Khớp 100% với 8 JPA entities, ddl-auto: validate pass
-- Hibernate 6 (Spring Boot 3.2) map @Enumerated(STRING) → MySQL ENUM type (uppercase)
-- DATETIME(6) cho LocalDateTime, BIT(1) cho Boolean
-- =============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -------------------------------------------------------------
-- 1. tree_species  (không có FK → tạo trước)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tree_species (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100)    NOT NULL,
    description      TEXT,
    image_url        VARCHAR(500),
    required_minutes INT             NOT NULL,
    coin_cost        INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_tree_species          PRIMARY KEY (id),
    CONSTRAINT chk_ts_required_minutes  CHECK (required_minutes > 0),
    CONSTRAINT chk_ts_coin_cost         CHECK (coin_cost >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 2. users
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id                  BIGINT          NOT NULL AUTO_INCREMENT,
    username            VARCHAR(50)     NOT NULL,
    email               VARCHAR(100)    NOT NULL,
    password_hash       VARCHAR(255)    NOT NULL,
    coin                INT             NOT NULL DEFAULT 0,
    total_focus_minutes INT             NOT NULL DEFAULT 0,
    created_at          DATETIME(6)     NOT NULL,
    updated_at          DATETIME(6),
    is_active           BIT(1)          NOT NULL DEFAULT b'1',
    CONSTRAINT pk_users             PRIMARY KEY (id),
    CONSTRAINT uq_users_username    UNIQUE (username),
    CONSTRAINT uq_users_email       UNIQUE (email),
    CONSTRAINT chk_users_coin       CHECK (coin >= 0),
    CONSTRAINT chk_users_tfm        CHECK (total_focus_minutes >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 3. focus_sessions
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS focus_sessions (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    user_id          BIGINT          NOT NULL,
    tree_species_id  BIGINT,
    start_time       DATETIME(6)     NOT NULL,
    end_time         DATETIME(6),
    planned_duration INT             NOT NULL,
    actual_duration  INT,
    status           ENUM('IN_PROGRESS','SUCCESS','FAILED') NOT NULL DEFAULT 'IN_PROGRESS',
    coin_earned      INT,
    CONSTRAINT pk_focus_sessions    PRIMARY KEY (id),
    CONSTRAINT fk_fs_user           FOREIGN KEY (user_id)         REFERENCES users       (id),
    CONSTRAINT fk_fs_tree_species   FOREIGN KEY (tree_species_id) REFERENCES tree_species(id),
    CONSTRAINT chk_fs_planned_dur   CHECK (planned_duration > 0),
    INDEX idx_fs_user_id   (user_id),
    INDEX idx_fs_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 4. my_forest
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS my_forest (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    user_id          BIGINT      NOT NULL,
    focus_session_id BIGINT,
    tree_species_id  BIGINT,
    planted_at       DATETIME(6) NOT NULL,
    is_alive         BIT(1)      NOT NULL DEFAULT b'1',
    CONSTRAINT pk_my_forest         PRIMARY KEY (id),
    CONSTRAINT uq_mf_session        UNIQUE (focus_session_id),
    CONSTRAINT fk_mf_user           FOREIGN KEY (user_id)          REFERENCES users        (id),
    CONSTRAINT fk_mf_session        FOREIGN KEY (focus_session_id) REFERENCES focus_sessions(id),
    CONSTRAINT fk_mf_tree_species   FOREIGN KEY (tree_species_id)  REFERENCES tree_species (id),
    INDEX idx_mf_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 5. focus_groups
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS focus_groups (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    owner_id      BIGINT       NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    is_active     BIT(1)       NOT NULL DEFAULT b'1',
    penalty_coins INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_focus_groups      PRIMARY KEY (id),
    CONSTRAINT fk_fg_owner          FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT chk_fg_penalty_coins CHECK (penalty_coins >= 0),
    INDEX idx_fg_owner_id (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 6. group_members
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS group_members (
    id        BIGINT      NOT NULL AUTO_INCREMENT,
    group_id  BIGINT      NOT NULL,
    user_id   BIGINT      NOT NULL,
    joined_at DATETIME(6) NOT NULL,
    status    ENUM('ACTIVE','LEFT','KICKED') NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT pk_group_members         PRIMARY KEY (id),
    CONSTRAINT uq_gm_group_user         UNIQUE (group_id, user_id),
    CONSTRAINT fk_gm_group              FOREIGN KEY (group_id) REFERENCES focus_groups(id),
    CONSTRAINT fk_gm_user               FOREIGN KEY (user_id)  REFERENCES users       (id),
    INDEX idx_gm_group_id (group_id),
    INDEX idx_gm_user_id  (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 7. group_sessions
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS group_sessions (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    group_id   BIGINT      NOT NULL,
    start_time DATETIME(6),
    end_time   DATETIME(6),
    status     ENUM('WAITING','IN_PROGRESS','COMPLETED','FAILED') NOT NULL DEFAULT 'WAITING',
    CONSTRAINT pk_group_sessions    PRIMARY KEY (id),
    CONSTRAINT fk_gs_group          FOREIGN KEY (group_id) REFERENCES focus_groups(id),
    INDEX idx_gses_group_id (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- 8. user_activity_logs
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_activity_logs (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    user_id       BIGINT      NOT NULL,
    log_date      DATE        NOT NULL,
    total_minutes INT         NOT NULL DEFAULT 0,
    session_count INT         NOT NULL DEFAULT 0,
    success_count INT         NOT NULL DEFAULT 0,
    created_at    DATETIME(6) NOT NULL,
    CONSTRAINT pk_user_activity_logs    PRIMARY KEY (id),
    CONSTRAINT uq_ual_user_date         UNIQUE (user_id, log_date),
    CONSTRAINT fk_ual_user              FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT chk_ual_total_minutes    CHECK (total_minutes >= 0),
    CONSTRAINT chk_ual_session_count    CHECK (session_count >= 0),
    CONSTRAINT chk_ual_success_count    CHECK (success_count >= 0),
    INDEX idx_ual_user_id  (user_id),
    INDEX idx_ual_log_date (log_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
