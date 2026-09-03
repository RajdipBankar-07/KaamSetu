-- ============================================================================
-- KaamSetu (कामसेतू) — MySQL Database Schema
-- Version: V1.0 (MySQL 8.0+)
-- Converted from PostgreSQL to MySQL
-- Architecture: Modular Monolith Domain Relational Storage
-- ============================================================================

CREATE DATABASE IF NOT EXISTS kaamsetu_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kaamsetu_db;

-- ============================================================================
-- 1. DOMAIN TABLES
-- Note: MySQL does not support CREATE TYPE ENUM like PostgreSQL.
--       ENUMs are defined inline on each column.
--       UUID replaced with CHAR(36) and generated via UUID() or app layer.
--       TIMESTAMPTZ replaced with DATETIME(6).
--       BIGSERIAL replaced with BIGINT AUTO_INCREMENT.
--       JSONB replaced with JSON.
--       INET replaced with VARCHAR(45).
--       Arrays (time_slots[]) replaced with VARCHAR(200).
--       Triggers use MySQL syntax (DELIMITER is handled in IDE, not shown here).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Table 1: USERS (Core Authentication & Identity)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              CHAR(36)        NOT NULL DEFAULT (UUID()),
    username        VARCHAR(50)     NOT NULL,
    full_name       VARCHAR(150)    NOT NULL,
    mobile          VARCHAR(15)     NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    password_hash   VARCHAR(255),
    role            ENUM('WORKER','PROVIDER','ADMIN') NOT NULL DEFAULT 'WORKER',
    language_preference ENUM('mr','hi','en')          NOT NULL DEFAULT 'mr',
    status          ENUM('PENDING','APPROVED','ACTIVE','REJECTED','DEACTIVATED','SUSPENDED','BANNED') NOT NULL DEFAULT 'PENDING',
    country         VARCHAR(100)    NOT NULL DEFAULT 'India',
    state           VARCHAR(100)    NOT NULL DEFAULT 'Maharashtra',
    district        VARCHAR(100)    NOT NULL DEFAULT 'Pune Rural',
    village         VARCHAR(100),
    is_mobile_verified   TINYINT(1)  NOT NULL DEFAULT 0,
    is_email_verified    TINYINT(1)  NOT NULL DEFAULT 0,
    is_location_verified TINYINT(1)  NOT NULL DEFAULT 0,
    is_identity_verified TINYINT(1)  NOT NULL DEFAULT 0,
    last_login_at   DATETIME(6),
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at      DATETIME(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_mobile (mobile),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Core authentication, role authorization, and account lifecycle status';

-- ----------------------------------------------------------------------------
-- Table 1b: EMAIL_VERIFICATION_TOKENS (Single-Use Secure Verification)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id              CHAR(36)        NOT NULL DEFAULT (UUID()),
    user_id         CHAR(36)        NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    token           VARCHAR(120)    NOT NULL,
    expires_at      DATETIME(6)     NOT NULL,
    is_used         TINYINT(1)      NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_email_token (token),
    INDEX idx_email_tokens_user (user_id),
    INDEX idx_email_tokens_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Cryptographically secure single-use email verification tokens';

-- ----------------------------------------------------------------------------
-- Table 2: WORKERS (Worker Profile & Matching Preferences)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS workers (
    id                  CHAR(36)        NOT NULL DEFAULT (UUID()),
    user_id             CHAR(36)        NOT NULL,
    full_name           VARCHAR(150)    NOT NULL,
    avatar_url          VARCHAR(500),
    village             VARCHAR(100)    NOT NULL,
    taluka              VARCHAR(100)    NOT NULL,
    district            VARCHAR(100)    NOT NULL DEFAULT 'Pune Rural',
    state               VARCHAR(100)    NOT NULL DEFAULT 'Maharashtra',
    pincode             VARCHAR(10),
    latitude            DECIMAL(10, 7),
    longitude           DECIMAL(10, 7),
    travel_radius_km    INT             NOT NULL DEFAULT 10,
    min_daily_wage      DECIMAL(10, 2)  NOT NULL DEFAULT 500.00,
    experience_years    INT             NOT NULL DEFAULT 1,
    bio                 TEXT,
    rating_avg          DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    trust_index         DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    is_available_today  TINYINT(1)      NOT NULL DEFAULT 1,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_workers_user_id (user_id),
    CONSTRAINT fk_workers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_workers_travel_radius CHECK (travel_radius_km IN (5, 10, 20)),
    CONSTRAINT chk_workers_min_wage CHECK (min_daily_wage > 0),
    CONSTRAINT chk_workers_rating CHECK (rating_avg >= 1.00 AND rating_avg <= 5.00),
    CONSTRAINT chk_workers_trust_index CHECK (trust_index >= 1.00 AND trust_index <= 5.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Worker profile, geographic travel radius, wage floor, and rating';

-- ----------------------------------------------------------------------------
-- Table 3: WORKER_SKILLS (Normalized Skills & Categorization)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS worker_skills (
    id                      CHAR(36)    NOT NULL DEFAULT (UUID()),
    worker_id               CHAR(36)    NOT NULL,
    category                ENUM('AGRICULTURE','CONSTRUCTION','HOUSEHOLD','DRIVING',
                                 'PAINTING','PLUMBING_ELECTRICAL','VILLAGE_PUBLIC',
                                 'GARDENING','REPAIRS','OTHER') NOT NULL,
    skill_title             VARCHAR(100),
    is_primary              TINYINT(1)  NOT NULL DEFAULT 0,
    verified_by_endorsement TINYINT(1)  NOT NULL DEFAULT 0,
    created_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_worker_category (worker_id, category),
    CONSTRAINT fk_worker_skills_worker FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Multi-select skill categories associated with a worker profile';

-- ----------------------------------------------------------------------------
-- Table 4: WORKER_AVAILABILITY (Day-by-Day Granular Calendar)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS worker_availability (
    id              CHAR(36)    NOT NULL DEFAULT (UUID()),
    worker_id       CHAR(36)    NOT NULL,
    day_of_week     ENUM('MON','TUE','WED','THU','FRI','SAT','SUN') NOT NULL,
    is_available    TINYINT(1)  NOT NULL DEFAULT 1,
    time_slots      VARCHAR(200) NOT NULL DEFAULT 'FULL_DAY',
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_worker_day_availability (worker_id, day_of_week),
    CONSTRAINT fk_worker_availability_worker FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Granular 7-day availability calendar and time slot preferences';

-- ----------------------------------------------------------------------------
-- Table 5: PROVIDERS (Provider / Employer Classification & Profile)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS providers (
    id                          CHAR(36)        NOT NULL DEFAULT (UUID()),
    user_id                     CHAR(36)        NOT NULL,
    name                        VARCHAR(150)    NOT NULL,
    business_name               VARCHAR(200),
    provider_type               ENUM('FARMER','HOUSEHOLD','CONTRACTOR','PANCHAYAT','BUSINESS','INDIVIDUAL')
                                                NOT NULL DEFAULT 'FARMER',
    village                     VARCHAR(100)    NOT NULL,
    taluka                      VARCHAR(100)    NOT NULL,
    district                    VARCHAR(100)    NOT NULL DEFAULT 'Pune Rural',
    state                       VARCHAR(100)    NOT NULL DEFAULT 'Maharashtra',
    pincode                     VARCHAR(10),
    latitude                    DECIMAL(10, 7),
    longitude                   DECIMAL(10, 7),
    rating_avg                  DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    payment_reliability_score   DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    trust_index                 DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    is_verified                 TINYINT(1)      NOT NULL DEFAULT 0,
    created_at                  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_providers_user_id (user_id),
    CONSTRAINT fk_providers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_providers_rating CHECK (rating_avg >= 1.00 AND rating_avg <= 5.00),
    CONSTRAINT chk_providers_payment_score CHECK (payment_reliability_score >= 1.00 AND payment_reliability_score <= 5.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Employer profiles, institutional classification, and payment score';

-- ----------------------------------------------------------------------------
-- Table 6: JOBS (Job Recruitment Lifecycle & Post Specifications)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jobs (
    id                  CHAR(36)        NOT NULL DEFAULT (UUID()),
    provider_id         CHAR(36)        NOT NULL,
    title               VARCHAR(200)    NOT NULL,
    category            ENUM('AGRICULTURE','CONSTRUCTION','HOUSEHOLD','DRIVING',
                             'PAINTING','PLUMBING_ELECTRICAL','VILLAGE_PUBLIC',
                             'GARDENING','REPAIRS','OTHER') NOT NULL,
    description         TEXT,
    village             VARCHAR(100)    NOT NULL,
    taluka              VARCHAR(100)    NOT NULL,
    district            VARCHAR(100)    NOT NULL DEFAULT 'Pune Rural',
    latitude            DECIMAL(10, 7),
    longitude           DECIMAL(10, 7),
    daily_wage          DECIMAL(10, 2)  NOT NULL,
    workers_required    INT             NOT NULL DEFAULT 1,
    workers_confirmed   INT             NOT NULL DEFAULT 0,
    priority            ENUM('NORMAL','URGENT')                             NOT NULL DEFAULT 'NORMAL',
    status              ENUM('DRAFT','OPEN','FILLED','IN_PROGRESS','COMPLETED','EXPIRED','CANCELLED','CLOSED')
                                                                            NOT NULL DEFAULT 'OPEN',
    is_recurring        TINYINT(1)      NOT NULL DEFAULT 0,
    recurrence_schedule VARCHAR(200),
    start_date          DATE            NOT NULL,
    duration_days       INT             NOT NULL DEFAULT 1,
    expires_at          DATETIME(6),
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    closed_at           DATETIME(6),

    PRIMARY KEY (id),
    CONSTRAINT fk_jobs_provider FOREIGN KEY (provider_id) REFERENCES providers(id) ON DELETE RESTRICT,
    CONSTRAINT chk_jobs_wage_positive CHECK (daily_wage > 0),
    CONSTRAINT chk_jobs_workers_required CHECK (workers_required >= 1),
    CONSTRAINT chk_jobs_workers_confirmed CHECK (workers_confirmed >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Job recruitment post specifications, capacity limits, and recruitment lifecycle';

-- ----------------------------------------------------------------------------
-- Table 7: APPLICATIONS (Worker Expressions of Interest)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id              CHAR(36)        NOT NULL DEFAULT (UUID()),
    job_id          CHAR(36)        NOT NULL,
    worker_id       CHAR(36)        NOT NULL,
    status          ENUM('APPLIED','SHORTLISTED','REJECTED','WITHDRAWN') NOT NULL DEFAULT 'APPLIED',
    applied_wage    DECIMAL(10, 2),
    worker_notes    TEXT,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_job_worker_application (job_id, worker_id),
    CONSTRAINT fk_applications_job    FOREIGN KEY (job_id)    REFERENCES jobs(id)    ON DELETE CASCADE,
    CONSTRAINT fk_applications_worker FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Initial expressions of interest from workers for open jobs';

-- ----------------------------------------------------------------------------
-- Table 8: ASSIGNMENTS (Worker Engagement State Machine)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS assignments (
    id                              CHAR(36)        NOT NULL DEFAULT (UUID()),
    job_id                          CHAR(36)        NOT NULL,
    application_id                  CHAR(36),
    worker_id                       CHAR(36)        NOT NULL,
    provider_id                     CHAR(36)        NOT NULL,
    status                          ENUM('APPLIED','SELECTED','CONFIRMED','DECLINED','NO_RESPONSE',
                                         'IN_PROGRESS','COMPLETION_REQUESTED','COMPLETED',
                                         'CANCELLED','NO_SHOW','DISPUTED')
                                                    NOT NULL DEFAULT 'APPLIED',
    agreed_wage                     DECIMAL(10, 2)  NOT NULL,
    selection_window_expires_at     DATETIME(6),
    confirmed_at                    DATETIME(6),
    work_started_at                 DATETIME(6),
    completion_requested_by         ENUM('WORKER','PROVIDER','ADMIN'),
    completion_requested_at         DATETIME(6),
    completed_at                    DATETIME(6),
    payment_type                    ENUM('CASH','UPI','DIRECT_BANK') NOT NULL DEFAULT 'CASH',
    payment_status                  ENUM('PENDING','PAID_CONFIRMED','DISPUTED') NOT NULL DEFAULT 'PENDING',
    payment_confirmed_by_worker     TINYINT(1)      NOT NULL DEFAULT 0,
    payment_confirmed_at            DATETIME(6),
    cancelled_by                    ENUM('WORKER','PROVIDER','ADMIN'),
    cancel_reason                   TEXT,
    cancelled_at                    DATETIME(6),
    created_at                      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_job_worker_assignment (job_id, worker_id),
    CONSTRAINT fk_assignments_job         FOREIGN KEY (job_id)         REFERENCES jobs(id)         ON DELETE RESTRICT,
    CONSTRAINT fk_assignments_application FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL,
    CONSTRAINT fk_assignments_worker      FOREIGN KEY (worker_id)      REFERENCES workers(id)      ON DELETE RESTRICT,
    CONSTRAINT fk_assignments_provider    FOREIGN KEY (provider_id)    REFERENCES providers(id)    ON DELETE RESTRICT,
    CONSTRAINT chk_assignment_agreed_wage CHECK (agreed_wage > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Core dual state machine governing individual worker engagement';

-- ----------------------------------------------------------------------------
-- Table 9: REVIEWS (Multi-Dimensional Bilateral Ratings)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
    id                      CHAR(36)        NOT NULL DEFAULT (UUID()),
    job_id                  CHAR(36)        NOT NULL,
    reviewer_id             CHAR(36)        NOT NULL,
    reviewee_id             CHAR(36)        NOT NULL,
    rating                  DECIMAL(2, 1)   NOT NULL,
    punctuality_rating      DECIMAL(2, 1),
    quality_rating          DECIMAL(2, 1),
    behavior_rating         DECIMAL(2, 1),
    review_text             TEXT,
    created_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_job_reviewer_reviewee (job_id, reviewer_id, reviewee_id),
    CONSTRAINT fk_reviews_job      FOREIGN KEY (job_id)      REFERENCES jobs(id)  ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_review_rating CHECK (rating >= 1.0 AND rating <= 5.0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Multi-dimensional evaluation system for bilateral ratings';

-- ----------------------------------------------------------------------------
-- Table 10: TRUST_PROFILES (Computed Trust Indices & Enforcement Ladder)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trust_profiles (
    id                          CHAR(36)        NOT NULL DEFAULT (UUID()),
    user_id                     CHAR(36)        NOT NULL,
    trust_index                 DECIMAL(3, 2)   NOT NULL DEFAULT 5.00,
    status                      ENUM('HEALTHY','WARNING','RESTRICTED','SUSPENDED','BANNED')
                                                NOT NULL DEFAULT 'HEALTHY',
    completed_jobs_count        INT             NOT NULL DEFAULT 0,
    no_show_count               INT             NOT NULL DEFAULT 0,
    late_cancellation_count     INT             NOT NULL DEFAULT 0,
    verified_reports_count      INT             NOT NULL DEFAULT 0,
    warning_issued_at           DATETIME(6),
    restriction_expires_at      DATETIME(6),
    calculated_at               DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_trust_profiles_user_id (user_id),
    CONSTRAINT fk_trust_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_trust_profile_index CHECK (trust_index >= 1.00 AND trust_index <= 5.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Trust index and progressive enforcement ladder for platform integrity';

-- ----------------------------------------------------------------------------
-- Table 11: MODERATION_REPORTS (Safety & Abuse Reports)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS moderation_reports (
    id                      CHAR(36)        NOT NULL DEFAULT (UUID()),
    reporter_user_id        CHAR(36)        NOT NULL,
    reported_user_id        CHAR(36),
    job_id                  CHAR(36),
    assignment_id           CHAR(36),
    target_type             ENUM('WORKER','PROVIDER','JOB','MESSAGE','PROFILE') NOT NULL,
    category                ENUM('NO_SHOW','WAGE_DISPUTE','HARASSMENT','SAFETY_CONCERN','FRAUD','OTHER') NOT NULL,
    reason                  TEXT            NOT NULL,
    evidence_metadata       JSON,
    status                  ENUM('PENDING_REVIEW','UNDER_INVESTIGATION','RESOLVED','DISMISSED')
                                            NOT NULL DEFAULT 'PENDING_REVIEW',
    resolution_notes        TEXT,
    resolved_by_admin_id    CHAR(36),
    created_at              DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    resolved_at             DATETIME(6),

    PRIMARY KEY (id),
    CONSTRAINT fk_modreports_reporter  FOREIGN KEY (reporter_user_id)     REFERENCES users(id)       ON DELETE RESTRICT,
    CONSTRAINT fk_modreports_reported  FOREIGN KEY (reported_user_id)     REFERENCES users(id)       ON DELETE RESTRICT,
    CONSTRAINT fk_modreports_job       FOREIGN KEY (job_id)               REFERENCES jobs(id)        ON DELETE SET NULL,
    CONSTRAINT fk_modreports_assign    FOREIGN KEY (assignment_id)        REFERENCES assignments(id) ON DELETE SET NULL,
    CONSTRAINT fk_modreports_admin     FOREIGN KEY (resolved_by_admin_id) REFERENCES users(id)       ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Universal safety reporting queue routed to Admin Moderation Center';

-- ----------------------------------------------------------------------------
-- Table 12: NOTIFICATIONS (Multi-Category Notification Center)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id          CHAR(36)        NOT NULL DEFAULT (UUID()),
    user_id     CHAR(36)        NOT NULL,
    channel     ENUM('JOB','APPLICATION','SELECTION','MESSAGE','REMINDER',
                     'RATING_PAYMENT','SAFETY_ACCOUNT') NOT NULL,
    title_key   VARCHAR(150)    NOT NULL,
    message_key VARCHAR(255)    NOT NULL,
    params      JSON,
    action_url  VARCHAR(500),
    is_read     TINYINT(1)      NOT NULL DEFAULT 0,
    read_at     DATETIME(6),
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Structured notification logs across 7 organized channels';

-- ----------------------------------------------------------------------------
-- Table 13: MESSAGES (Privacy-Aware In-App Chat)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS messages (
    id                  CHAR(36)    NOT NULL DEFAULT (UUID()),
    job_id              CHAR(36),
    assignment_id       CHAR(36),
    sender_user_id      CHAR(36)    NOT NULL,
    recipient_user_id   CHAR(36)    NOT NULL,
    message_text        TEXT        NOT NULL,
    is_read             TINYINT(1)  NOT NULL DEFAULT 0,
    read_at             DATETIME(6),
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    CONSTRAINT fk_messages_job       FOREIGN KEY (job_id)            REFERENCES jobs(id)        ON DELETE SET NULL,
    CONSTRAINT fk_messages_assign    FOREIGN KEY (assignment_id)     REFERENCES assignments(id) ON DELETE SET NULL,
    CONSTRAINT fk_messages_sender    FOREIGN KEY (sender_user_id)    REFERENCES users(id)       ON DELETE RESTRICT,
    CONSTRAINT fk_messages_recipient FOREIGN KEY (recipient_user_id) REFERENCES users(id)       ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Direct in-app communication layer protecting mobile privacy';

-- ----------------------------------------------------------------------------
-- Table 14: SAVED_ITEMS (Bookmarked Jobs & Workers)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS saved_items (
    id          CHAR(36)    NOT NULL DEFAULT (UUID()),
    user_id     CHAR(36)    NOT NULL,
    target_type ENUM('JOB','WORKER') NOT NULL,
    target_id   CHAR(36)    NOT NULL,
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_user_saved_item (user_id, target_type, target_id),
    CONSTRAINT fk_saved_items_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Saved bookmarks for workers (jobs) and employers (workers)';

-- ----------------------------------------------------------------------------
-- Table 15: PRIVACY_SETTINGS (Granular Data & Contact Protection)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS privacy_settings (
    id                          CHAR(36)    NOT NULL DEFAULT (UUID()),
    user_id                     CHAR(36)    NOT NULL,
    phone_masking_enabled       TINYINT(1)  NOT NULL DEFAULT 1,
    location_granularity        ENUM('VILLAGE','TALUKA','EXACT_GPS') NOT NULL DEFAULT 'VILLAGE',
    allow_sms_alerts            TINYINT(1)  NOT NULL DEFAULT 1,
    allow_push_alerts           TINYINT(1)  NOT NULL DEFAULT 1,
    allow_whatsapp_alerts       TINYINT(1)  NOT NULL DEFAULT 1,
    deactivation_requested      TINYINT(1)  NOT NULL DEFAULT 0,
    deletion_requested          TINYINT(1)  NOT NULL DEFAULT 0,
    deletion_requested_at       DATETIME(6),
    created_at                  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at                  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    UNIQUE KEY uq_privacy_settings_user_id (user_id),
    CONSTRAINT fk_privacy_settings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='User privacy, contact masking, communication preferences, and data deletion requests';

-- ----------------------------------------------------------------------------
-- Table 16: AUDIT_LOGS (Immutable System Audit & Security Trail)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    actor_user_id   CHAR(36),
    action_type     VARCHAR(100)    NOT NULL,
    entity_name     VARCHAR(100)    NOT NULL,
    entity_id       CHAR(36),
    ip_address      VARCHAR(45),
    user_agent      TEXT,
    old_state       JSON,
    new_state       JSON,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (actor_user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Immutable compliance audit trail for security, status changes, and dispute investigation';

-- ============================================================================
-- 2. INDEXES FOR PERFORMANCE
-- ============================================================================

CREATE INDEX idx_workers_village_taluka   ON workers(village, taluka);
CREATE INDEX idx_workers_available_today  ON workers(is_available_today);
CREATE INDEX idx_jobs_status              ON jobs(status);
CREATE INDEX idx_jobs_village_taluka      ON jobs(village, taluka);
CREATE INDEX idx_jobs_start_date          ON jobs(start_date);
CREATE INDEX idx_applications_job_id      ON applications(job_id);
CREATE INDEX idx_applications_worker_id   ON applications(worker_id);
CREATE INDEX idx_assignments_job_id       ON assignments(job_id);
CREATE INDEX idx_assignments_worker_id    ON assignments(worker_id);
CREATE INDEX idx_assignments_status       ON assignments(status);
CREATE INDEX idx_notifications_user_id    ON notifications(user_id);
CREATE INDEX idx_notifications_is_read    ON notifications(is_read);
CREATE INDEX idx_audit_logs_entity        ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_users_deleted_at         ON users(deleted_at);
