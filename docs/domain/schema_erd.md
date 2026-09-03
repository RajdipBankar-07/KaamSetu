# 🌾 KaamSetu (कामसेतू) — Entity-Relationship Diagram (ERD) & Schema Architecture
## Phase 2: Domain Modeling & Relational Schema Specification

---

## 1. High-Level Entity-Relationship Diagram (Mermaid)

```mermaid
erDiagram
    USERS ||--o| WORKERS : "has profile (1:1)"
    USERS ||--o| PROVIDERS : "has profile (1:1)"
    USERS ||--o| TRUST_PROFILES : "evaluated by (1:1)"
    USERS ||--o| PRIVACY_SETTINGS : "configures (1:1)"
    USERS ||--o{ NOTIFICATIONS : "receives (1:N)"
    USERS ||--o{ SAVED_ITEMS : "bookmarks (1:N)"
    USERS ||--o{ AUDIT_LOGS : "acts in (1:N)"
    
    WORKERS ||--o{ WORKER_SKILLS : "possesses (1:N)"
    WORKERS ||--o{ WORKER_AVAILABILITY : "declares (1:N)"
    WORKERS ||--o{ APPLICATIONS : "submits (1:N)"
    WORKERS ||--o{ ASSIGNMENTS : "engaged in (1:N)"
    
    PROVIDERS ||--o{ JOBS : "posts (1:N)"
    PROVIDERS ||--o{ ASSIGNMENTS : "manages (1:N)"
    
    JOBS ||--o{ APPLICATIONS : "receives (1:N)"
    JOBS ||--o{ ASSIGNMENTS : "fulfills via (1:N)"
    JOBS ||--o{ MESSAGES : "discusses (1:N)"
    
    APPLICATIONS ||--o| ASSIGNMENTS : "promoted to (0..1:1)"
    
    ASSIGNMENTS ||--o{ REVIEWS : "evaluated by (0..2:1)"
    ASSIGNMENTS ||--o{ MESSAGES : "coordinates (1:N)"
    ASSIGNMENTS ||--o{ MODERATION_REPORTS : "disputed via (0..N:1)"

    USERS {
        uuid id PK
        string mobile UK
        string password_hash
        user_role_enum role
        language_code_enum language_preference
        user_status_enum status
        boolean is_mobile_verified
        boolean is_location_verified
        boolean is_identity_verified
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    WORKERS {
        uuid id PK
        uuid user_id FK,UK
        string full_name
        string village
        string taluka
        string district
        numeric latitude
        numeric longitude
        int travel_radius_km
        numeric min_daily_wage
        int experience_years
        numeric rating
        numeric trust_index
        boolean is_available_today
        timestamp created_at
    }

    WORKER_SKILLS {
        uuid id PK
        uuid worker_id FK
        work_category_enum category
        string skill_title
        boolean is_primary
        boolean verified_by_endorsement
    }

    WORKER_AVAILABILITY {
        uuid id PK
        uuid worker_id FK
        day_of_week_enum day_of_week
        boolean is_available
        time_slot_enum[] time_slots
    }

    PROVIDERS {
        uuid id PK
        uuid user_id FK,UK
        string name
        string business_name
        provider_type_enum provider_type
        string village
        string taluka
        string district
        numeric latitude
        numeric longitude
        numeric rating
        numeric payment_reliability_score
        boolean is_verified
    }

    JOBS {
        uuid id PK
        uuid provider_id FK
        string title
        work_category_enum category
        string description
        string village
        string taluka
        string district
        numeric latitude
        numeric longitude
        numeric daily_wage
        int workers_required
        int workers_confirmed
        job_priority_enum priority
        job_status_enum status
        boolean is_recurring
        date start_date
        int duration_days
        timestamp expires_at
        timestamp created_at
    }

    APPLICATIONS {
        uuid id PK
        uuid job_id FK
        uuid worker_id FK
        application_status_enum status
        numeric applied_wage
        string worker_notes
        timestamp created_at
    }

    ASSIGNMENTS {
        uuid id PK
        uuid job_id FK
        uuid application_id FK
        uuid worker_id FK
        uuid provider_id FK
        assignment_status_enum status
        numeric agreed_wage
        timestamp selection_window_expires_at
        timestamp confirmed_at
        payment_type_enum payment_type
        payment_status_enum payment_status
        boolean payment_confirmed_by_worker
        timestamp payment_confirmed_at
        timestamp completed_at
    }

    REVIEWS {
        uuid id PK
        uuid assignment_id FK
        uuid reviewer_user_id FK
        uuid reviewee_user_id FK
        user_role_enum reviewer_role
        numeric overall_score
        numeric work_quality_score
        numeric punctuality_score
        numeric behavior_score
        numeric reliability_score
        numeric payment_reliability_score
        numeric safety_score
        numeric job_accuracy_score
        numeric communication_score
        string comment
    }

    TRUST_PROFILES {
        uuid id PK
        uuid user_id FK,UK
        numeric trust_index
        trust_ladder_status_enum status
        int completed_jobs_count
        int no_show_count
        int late_cancellation_count
        int verified_reports_count
    }

    MODERATION_REPORTS {
        uuid id PK
        uuid reporter_user_id FK
        uuid reported_user_id FK
        uuid job_id FK
        uuid assignment_id FK
        report_target_type_enum target_type
        report_category_enum category
        string reason
        jsonb evidence_metadata
        report_status_enum status
        string resolution_notes
        uuid resolved_by_admin_id FK
    }

    NOTIFICATIONS {
        uuid id PK
        uuid user_id FK
        notification_channel_enum channel
        string title_key
        string message_key
        jsonb params
        string action_url
        boolean is_read
        timestamp created_at
    }

    MESSAGES {
        uuid id PK
        uuid job_id FK
        uuid assignment_id FK
        uuid sender_user_id FK
        uuid recipient_user_id FK
        string message_text
        boolean is_read
        timestamp created_at
    }

    SAVED_ITEMS {
        uuid id PK
        uuid user_id FK
        string target_type
        uuid target_id
        timestamp created_at
    }

    PRIVACY_SETTINGS {
        uuid id PK
        uuid user_id FK,UK
        boolean phone_masking_enabled
        string location_granularity
        boolean allow_sms_alerts
        boolean allow_push_alerts
        boolean allow_whatsapp_alerts
        boolean deactivation_requested
        boolean deletion_requested
    }

    AUDIT_LOGS {
        bigint id PK
        uuid actor_user_id FK
        string action_type
        string entity_name
        uuid entity_id
        inet ip_address
        text user_agent
        jsonb old_state
        jsonb new_state
        timestamp created_at
    }
```

---

## 2. Dual State Machine Architectural Diagrams

### 2.1 Job Recruitment State Machine (`job.status`)
Governs the public posting and capacity availability of the job post.

```mermaid
stateDiagram-v2
    [*] --> DRAFT : Provider creates post
    DRAFT --> OPEN : Provider publishes
    OPEN --> FILLED : Confirmed Workers == Workers Required
    FILLED --> OPEN : Confirmed worker cancels (Slot reopens)
    OPEN --> EXPIRED : Start date passes unfilled
    OPEN --> CANCELLED : Provider cancels post
    FILLED --> IN_PROGRESS : Work starts
    IN_PROGRESS --> COMPLETED : All assignments finished
    COMPLETED --> CLOSED : Archival
    EXPIRED --> CLOSED : Archival
    CANCELLED --> CLOSED : Archival
```

### 2.2 Worker Engagement State Machine (`assignment.status`)
Governs individual worker recruitment, confirmation timer, bilateral progress, and completion.

```mermaid
stateDiagram-v2
    [*] --> APPLIED : Worker applies
    APPLIED --> SELECTED : Provider selects worker
    
    state "Decision Window (24h standard / 2h urgent)" as DecisionWindow {
        SELECTED --> CONFIRMED : Worker accepts terms
        SELECTED --> DECLINED : Worker taps decline
        SELECTED --> NO_RESPONSE : Window timer expires
    }
    
    DECLINED --> [*] : Slot auto-reopens
    NO_RESPONSE --> [*] : Slot auto-reopens
    
    CONFIRMED --> IN_PROGRESS : Work starts
    CONFIRMED --> CANCELLED : Cancel with reason
    CONFIRMED --> NO_SHOW : Reported no-show
    
    IN_PROGRESS --> COMPLETION_REQUESTED : Either party marks done
    COMPLETION_REQUESTED --> COMPLETED : Mutual bilateral confirmation
    
    COMPLETED --> Rated_and_Paid : Unlocks 5-dimension rating & Payment Receipt Ack
    
    IN_PROGRESS --> DISPUTED : Wage / quality dispute
    COMPLETION_REQUESTED --> DISPUTED : Dispute filed
```

---

## 3. Relational Foreign Key Integrity & Cascade Rules

| Child Table | Foreign Key Column | Parent Table | On Delete Rule | Rationale |
| :--- | :--- | :--- | :--- | :--- |
| `workers` | `user_id` | `users(id)` | **CASCADE** | Worker profile cannot exist without a core user account. |
| `worker_skills` | `worker_id` | `workers(id)` | **CASCADE** | Skills belong directly to the worker entity. |
| `worker_availability` | `worker_id` | `workers(id)` | **CASCADE** | Availability calendar belongs directly to the worker entity. |
| `providers` | `user_id` | `users(id)` | **CASCADE** | Provider profile cannot exist without a core user account. |
| `jobs` | `provider_id` | `providers(id)` | **RESTRICT** | Provider accounts with job history cannot be deleted without audit preservation. |
| `applications` | `job_id` | `jobs(id)` | **CASCADE** | Unprocessed applications are cleaned if a draft job is purged. |
| `applications` | `worker_id` | `workers(id)` | **CASCADE** | Applications belong to the applicant worker. |
| `assignments` | `job_id` | `jobs(id)` | **RESTRICT** | Immutable work records cannot be orphaned. |
| `assignments` | `worker_id` | `workers(id)` | **RESTRICT** | Protects active/completed work engagement history. |
| `assignments` | `provider_id` | `providers(id)` | **RESTRICT** | Protects active/completed work engagement history. |
| `reviews` | `assignment_id` | `assignments(id)`| **CASCADE** | Ratings are attached directly to verified completed assignments. |
| `reviews` | `reviewer_user_id` | `users(id)` | **RESTRICT** | Reviewer attribution is preserved for trust score integrity. |
| `trust_profiles` | `user_id` | `users(id)` | **CASCADE** | Trust profile is a direct extension of user identity. |
| `moderation_reports`| `reporter_user_id` | `users(id)` | **RESTRICT** | Compliance records require audit integrity. |
| `notifications` | `user_id` | `users(id)` | **CASCADE** | User inbox is cleaned on account purging. |
| `messages` | `sender_user_id` | `users(id)` | **RESTRICT** | Chat transcript integrity is preserved for safety investigations. |
| `privacy_settings` | `user_id` | `users(id)` | **CASCADE** | Privacy settings belong to user account. |
| `audit_logs` | `actor_user_id` | `users(id)` | **SET NULL** | Immutable audit logs persist even if actor is anonymized. |
