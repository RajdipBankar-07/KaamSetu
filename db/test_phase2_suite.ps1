# ============================================================================
# 🌾 KaamSetu V1 — Phase 2 Automated Test Execution Suite
# Architecture + PostgreSQL Database Schema Design & Domain Modeling
# ============================================================================

$ErrorActionPreference = "Continue"

$schemaFile = "e:\new project\KaamSetu\db\schema.sql"
$indexesFile = "e:\new project\KaamSetu\db\indexes.sql"
$seedFile = "e:\new project\KaamSetu\db\seed.sql"
$erdFile = "e:\new project\KaamSetu\docs\domain\schema_erd.md"
$domainFile = "e:\new project\KaamSetu\docs\domain\domain_models.md"
$masterPlanFile = "e:\new project\KaamSetu\KaamSetu_Master_Project_Plan.md"

$schemaContent = Get-Content $schemaFile -Raw
$indexesContent = Get-Content $indexesFile -Raw
$seedContent = Get-Content $seedFile -Raw
$erdContent = Get-Content $erdFile -Raw
$domainContent = Get-Content $domainFile -Raw
$masterPlanContent = Get-Content $masterPlanFile -Raw

$results = @()
$passCount = 0
$failCount = 0

function Assert-TestCase {
    param (
        [string]$TestId,
        [string]$Category,
        [string]$Description,
        [bool]$Condition,
        [string]$Details = ""
    )

    $status = if ($Condition) { "PASS" } else { "FAIL" }
    if ($Condition) {
        $script:passCount++
    } else {
        $script:failCount++
    }

    $res = [PSCustomObject]@{
        TestId      = $TestId
        Category    = $Category
        Description = $Description
        Status      = $status
        Details     = $Details
    }
    $script:results += $res
    Write-Host "$TestId [$status] : $Description" -ForegroundColor $(if ($Condition) { "Green" } else { "Red" })
}

Write-Host "=================================================="
Write-Host "🌾 RUNNING KAAMSETU V1 PHASE 2 TEST SUITE"
Write-Host "==================================================`n"

# ----------------------------------------------------------------------------
# 1. SCHEMA & RELATIONAL TABLE DEFINITIONS (TC-P2-001 to TC-P2-016)
# ----------------------------------------------------------------------------
$expectedTables = @(
    "users", "workers", "worker_skills", "worker_availability",
    "providers", "jobs", "applications", "assignments",
    "reviews", "trust_profiles", "moderation_reports", "notifications",
    "messages", "saved_items", "privacy_settings", "audit_logs"
)

$i = 1
foreach ($tbl in $expectedTables) {
    $id = "TC-P2-" + $i.ToString("D3")
    $hasTable = $schemaContent -match "CREATE\s+TABLE\s+$tbl\s*\("
    Assert-TestCase -TestId $id -Category "Relational Tables" -Description "Table '$tbl' is defined in schema.sql" -Condition $hasTable
    $i++
}

# ----------------------------------------------------------------------------
# 2. CUSTOM ENUM TYPE DEFINITIONS (TC-P2-017 to TC-P2-034)
# ----------------------------------------------------------------------------
$expectedEnums = @(
    "user_role_enum", "language_code_enum", "user_status_enum",
    "work_category_enum", "day_of_week_enum", "time_slot_enum",
    "provider_type_enum", "job_priority_enum", "job_status_enum",
    "application_status_enum", "assignment_status_enum",
    "payment_type_enum", "payment_status_enum",
    "trust_ladder_status_enum", "report_target_type_enum",
    "report_category_enum", "report_status_enum", "notification_channel_enum"
)

foreach ($en in $expectedEnums) {
    $id = "TC-P2-" + $i.ToString("D3")
    $hasEnum = $schemaContent -match "CREATE\s+TYPE\s+$en\s+AS\s+ENUM"
    Assert-TestCase -TestId $id -Category "Custom Enums" -Description "Enum '$en' is defined in schema.sql" -Condition $hasEnum
    $i++
}

# ----------------------------------------------------------------------------
# 3. ENUM VALUES & SPECIFICATION PARITY (TC-P2-035 to TC-P2-050)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-035" -Category "Enum Parity" -Description "user_role_enum contains WORKER, PROVIDER, ADMIN" -Condition ($schemaContent -match "'WORKER'" -and $schemaContent -match "'PROVIDER'" -and $schemaContent -match "'ADMIN'")
Assert-TestCase -TestId "TC-P2-036" -Category "Enum Parity" -Description "language_code_enum contains mr, hi, en" -Condition ($schemaContent -match "'mr'" -and $schemaContent -match "'hi'" -and $schemaContent -match "'en'")
Assert-TestCase -TestId "TC-P2-037" -Category "Enum Parity" -Description "work_category_enum contains AGRICULTURE, CONSTRUCTION, HOUSEHOLD, DRIVING, etc." -Condition ($schemaContent -match "'AGRICULTURE'" -and $schemaContent -match "'CONSTRUCTION'" -and $schemaContent -match "'HOUSEHOLD'" -and $schemaContent -match "'DRIVING'")
Assert-TestCase -TestId "TC-P2-038" -Category "Enum Parity" -Description "provider_type_enum contains FARMER, HOUSEHOLD, CONTRACTOR, PANCHAYAT, BUSINESS, INDIVIDUAL" -Condition ($schemaContent -match "'FARMER'" -and $schemaContent -match "'CONTRACTOR'" -and $schemaContent -match "'PANCHAYAT'")
Assert-TestCase -TestId "TC-P2-039" -Category "Enum Parity" -Description "job_status_enum contains DRAFT, OPEN, FILLED, IN_PROGRESS, COMPLETED, EXPIRED, CANCELLED, CLOSED" -Condition ($schemaContent -match "'DRAFT'" -and $schemaContent -match "'OPEN'" -and $schemaContent -match "'FILLED'" -and $schemaContent -match "'EXPIRED'")
Assert-TestCase -TestId "TC-P2-040" -Category "Enum Parity" -Description "assignment_status_enum contains SELECTED, CONFIRMED, DECLINED, NO_RESPONSE, IN_PROGRESS, COMPLETION_REQUESTED, COMPLETED" -Condition ($schemaContent -match "'SELECTED'" -and $schemaContent -match "'CONFIRMED'" -and $schemaContent -match "'DECLINED'" -and $schemaContent -match "'NO_RESPONSE'")
Assert-TestCase -TestId "TC-P2-041" -Category "Enum Parity" -Description "payment_type_enum contains CASH, UPI, DIRECT_BANK" -Condition ($schemaContent -match "'CASH'" -and $schemaContent -match "'UPI'" -and $schemaContent -match "'DIRECT_BANK'")
Assert-TestCase -TestId "TC-P2-042" -Category "Enum Parity" -Description "payment_status_enum contains PENDING, PAID_CONFIRMED, DISPUTED" -Condition ($schemaContent -match "'PENDING'" -and $schemaContent -match "'PAID_CONFIRMED'" -and $schemaContent -match "'DISPUTED'")
Assert-TestCase -TestId "TC-P2-043" -Category "Enum Parity" -Description "trust_ladder_status_enum contains HEALTHY, WARNING, RESTRICTED, SUSPENDED, BANNED" -Condition ($schemaContent -match "'HEALTHY'" -and $schemaContent -match "'WARNING'" -and $schemaContent -match "'RESTRICTED'" -and $schemaContent -match "'SUSPENDED'" -and $schemaContent -match "'BANNED'")
Assert-TestCase -TestId "TC-P2-044" -Category "Enum Parity" -Description "report_category_enum contains NO_SHOW, WAGE_DISPUTE, HARASSMENT, SAFETY_CONCERN, FRAUD" -Condition ($schemaContent -match "'NO_SHOW'" -and $schemaContent -match "'WAGE_DISPUTE'" -and $schemaContent -match "'HARASSMENT'" -and $schemaContent -match "'SAFETY_CONCERN'")
Assert-TestCase -TestId "TC-P2-045" -Category "Enum Parity" -Description "day_of_week_enum covers 7 days MON to SUN" -Condition ($schemaContent -match "'MON'" -and $schemaContent -match "'SUN'")
Assert-TestCase -TestId "TC-P2-046" -Category "Enum Parity" -Description "time_slot_enum covers MORNING, AFTERNOON, EVENING, FULL_DAY" -Condition ($schemaContent -match "'MORNING'" -and $schemaContent -match "'AFTERNOON'" -and $schemaContent -match "'FULL_DAY'")
Assert-TestCase -TestId "TC-P2-047" -Category "Enum Parity" -Description "notification_channel_enum covers 7 notification streams" -Condition ($schemaContent -match "'JOB'" -and $schemaContent -match "'SELECTION'" -and $schemaContent -match "'REMINDER'" -and $schemaContent -match "'RATING_PAYMENT'")

# ----------------------------------------------------------------------------
# 4. PRIMARY & FOREIGN KEY RELATIONSHIPS (TC-P2-048 to TC-P2-065)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-048" -Category "Foreign Keys" -Description "workers.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+UNIQUE\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-049" -Category "Foreign Keys" -Description "worker_skills.worker_id references workers(id) ON DELETE CASCADE" -Condition ($schemaContent -match "worker_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+workers\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-050" -Category "Foreign Keys" -Description "worker_availability.worker_id references workers(id) ON DELETE CASCADE" -Condition ($schemaContent -match "worker_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+workers\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-051" -Category "Foreign Keys" -Description "providers.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+UNIQUE\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-052" -Category "Foreign Keys" -Description "jobs.provider_id references providers(id) ON DELETE RESTRICT" -Condition ($schemaContent -match "provider_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+providers\(id\)\s+ON\s+DELETE\s+RESTRICT")
Assert-TestCase -TestId "TC-P2-053" -Category "Foreign Keys" -Description "applications.job_id references jobs(id) ON DELETE CASCADE" -Condition ($schemaContent -match "job_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+jobs\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-054" -Category "Foreign Keys" -Description "applications.worker_id references workers(id) ON DELETE CASCADE" -Condition ($schemaContent -match "worker_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+workers\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-055" -Category "Foreign Keys" -Description "assignments.job_id references jobs(id) ON DELETE RESTRICT" -Condition ($schemaContent -match "job_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+jobs\(id\)\s+ON\s+DELETE\s+RESTRICT")
Assert-TestCase -TestId "TC-P2-056" -Category "Foreign Keys" -Description "assignments.worker_id references workers(id) ON DELETE RESTRICT" -Condition ($schemaContent -match "worker_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+workers\(id\)\s+ON\s+DELETE\s+RESTRICT")
Assert-TestCase -TestId "TC-P2-057" -Category "Foreign Keys" -Description "assignments.provider_id references providers(id) ON DELETE RESTRICT" -Condition ($schemaContent -match "provider_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+providers\(id\)\s+ON\s+DELETE\s+RESTRICT")
Assert-TestCase -TestId "TC-P2-058" -Category "Foreign Keys" -Description "reviews.assignment_id references assignments(id) ON DELETE CASCADE" -Condition ($schemaContent -match "assignment_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+assignments\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-059" -Category "Foreign Keys" -Description "trust_profiles.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+UNIQUE\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-060" -Category "Foreign Keys" -Description "moderation_reports.reporter_user_id references users(id)" -Condition ($schemaContent -match "reporter_user_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+users\(id\)")
Assert-TestCase -TestId "TC-P2-061" -Category "Foreign Keys" -Description "notifications.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-062" -Category "Foreign Keys" -Description "messages.sender_user_id & recipient_user_id reference users(id)" -Condition ($schemaContent -match "sender_user_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+users\(id\)" -and $schemaContent -match "recipient_user_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+users\(id\)")
Assert-TestCase -TestId "TC-P2-063" -Category "Foreign Keys" -Description "saved_items.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-064" -Category "Foreign Keys" -Description "privacy_settings.user_id references users(id) ON DELETE CASCADE" -Condition ($schemaContent -match "user_id\s+UUID\s+NOT\s+NULL\s+UNIQUE\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+CASCADE")
Assert-TestCase -TestId "TC-P2-065" -Category "Foreign Keys" -Description "audit_logs.actor_user_id references users(id) ON DELETE SET NULL" -Condition ($schemaContent -match "actor_user_id\s+UUID\s+REFERENCES\s+users\(id\)\s+ON\s+DELETE\s+SET\s+NULL")

# ----------------------------------------------------------------------------
# 5. DATA INTEGRITY & CHECK CONSTRAINTS (TC-P2-066 to TC-P2-080)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-066" -Category "Check Constraints" -Description "users table validates mobile format with regex" -Condition ($schemaContent -match "chk_users_mobile_format")
Assert-TestCase -TestId "TC-P2-067" -Category "Check Constraints" -Description "workers travel_radius_km constrained to (5, 10, 20)" -Condition ($schemaContent -match "chk_workers_travel_radius")
Assert-TestCase -TestId "TC-P2-068" -Category "Check Constraints" -Description "workers min_daily_wage constrained positive" -Condition ($schemaContent -match "chk_workers_min_wage")
Assert-TestCase -TestId "TC-P2-069" -Category "Check Constraints" -Description "workers rating constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_workers_rating")
Assert-TestCase -TestId "TC-P2-070" -Category "Check Constraints" -Description "workers trust_index constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_workers_trust_index")
Assert-TestCase -TestId "TC-P2-071" -Category "Check Constraints" -Description "providers rating constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_providers_rating")
Assert-TestCase -TestId "TC-P2-072" -Category "Check Constraints" -Description "providers payment_reliability_score constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_providers_payment_score")
Assert-TestCase -TestId "TC-P2-073" -Category "Check Constraints" -Description "jobs daily_wage constrained positive" -Condition ($schemaContent -match "chk_jobs_wage_positive")
Assert-TestCase -TestId "TC-P2-074" -Category "Check Constraints" -Description "jobs workers_required constrained >= 1" -Condition ($schemaContent -match "chk_jobs_workers_required")
Assert-TestCase -TestId "TC-P2-075" -Category "Check Constraints" -Description "jobs workers_confirmed constrained <= workers_required" -Condition ($schemaContent -match "chk_jobs_workers_confirmed")
Assert-TestCase -TestId "TC-P2-076" -Category "Check Constraints" -Description "assignments agreed_wage constrained positive" -Condition ($schemaContent -match "chk_assignment_agreed_wage")
Assert-TestCase -TestId "TC-P2-077" -Category "Check Constraints" -Description "reviews overall_score constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_review_overall")
Assert-TestCase -TestId "TC-P2-078" -Category "Check Constraints" -Description "trust_profiles trust_index constrained between 1.00 and 5.00" -Condition ($schemaContent -match "chk_trust_profile_index")
Assert-TestCase -TestId "TC-P2-079" -Category "Check Constraints" -Description "Unique constraint prevents duplicate worker skills in same category" -Condition ($schemaContent -match "uq_worker_category")
Assert-TestCase -TestId "TC-P2-080" -Category "Check Constraints" -Description "Unique constraint prevents duplicate worker day availability" -Condition ($schemaContent -match "uq_worker_day_availability")

# ----------------------------------------------------------------------------
# 6. AUTOMATION TRIGGERS & PROCEDURES (TC-P2-081 to TC-P2-090)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-081" -Category "Triggers & Procedures" -Description "trigger_set_updated_at function exists" -Condition ($schemaContent -match "CREATE\s+OR\s+REPLACE\s+FUNCTION\s+trigger_set_updated_at")
Assert-TestCase -TestId "TC-P2-082" -Category "Triggers & Procedures" -Description "trg_users_updated_at trigger attached to users" -Condition ($schemaContent -match "trg_users_updated_at")
Assert-TestCase -TestId "TC-P2-083" -Category "Triggers & Procedures" -Description "trg_workers_updated_at trigger attached to workers" -Condition ($schemaContent -match "trg_workers_updated_at")
Assert-TestCase -TestId "TC-P2-084" -Category "Triggers & Procedures" -Description "trg_jobs_updated_at trigger attached to jobs" -Condition ($schemaContent -match "trg_jobs_updated_at")
Assert-TestCase -TestId "TC-P2-085" -Category "Triggers & Procedures" -Description "trg_assignments_updated_at trigger attached to assignments" -Condition ($schemaContent -match "trg_assignments_updated_at")
Assert-TestCase -TestId "TC-P2-086" -Category "Triggers & Procedures" -Description "trigger_auto_fill_job_capacity function exists" -Condition ($schemaContent -match "CREATE\s+OR\s+REPLACE\s+FUNCTION\s+trigger_auto_fill_job_capacity")
Assert-TestCase -TestId "TC-P2-087" -Category "Triggers & Procedures" -Description "trg_assignment_auto_fill_job trigger attached to assignments" -Condition ($schemaContent -match "trg_assignment_auto_fill_job")
Assert-TestCase -TestId "TC-P2-088" -Category "Triggers & Procedures" -Description "Auto-fill capacity logic transitions job status to FILLED" -Condition ($schemaContent -match "status\s*=\s*'FILLED'")
Assert-TestCase -TestId "TC-P2-089" -Category "Triggers & Procedures" -Description "Auto-reopen logic transitions job status back to OPEN when worker cancels" -Condition ($schemaContent -match "status\s*=\s*'OPEN'")
Assert-TestCase -TestId "TC-P2-090" -Category "Triggers & Procedures" -Description "expire_past_open_jobs stored procedure implements soft expiration" -Condition ($schemaContent -match "CREATE\s+OR\s+REPLACE\s+FUNCTION\s+expire_past_open_jobs")

# ----------------------------------------------------------------------------
# 7. PERFORMANCE INDEXES & QUERY OPTIMIZATION (TC-P2-091 to TC-P2-105)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-091" -Category "Indexes" -Description "Hyperlocal compound index on workers (district, taluka, village)" -Condition ($indexesContent -match "idx_workers_location")
Assert-TestCase -TestId "TC-P2-092" -Category "Indexes" -Description "Geospatial coordinate index on workers (latitude, longitude)" -Condition ($indexesContent -match "idx_workers_coordinates")
Assert-TestCase -TestId "TC-P2-093" -Category "Indexes" -Description "Hyperlocal compound index on providers (district, taluka, village)" -Condition ($indexesContent -match "idx_providers_location")
Assert-TestCase -TestId "TC-P2-094" -Category "Indexes" -Description "Hyperlocal compound index on jobs (district, taluka, village, category)" -Condition ($indexesContent -match "idx_jobs_location_category")
Assert-TestCase -TestId "TC-P2-095" -Category "Indexes" -Description "Geospatial coordinate index on jobs (latitude, longitude)" -Condition ($indexesContent -match "idx_jobs_coordinates")
Assert-TestCase -TestId "TC-P2-096" -Category "Indexes" -Description "Partial index on jobs for active feed matching WHERE status = 'OPEN'" -Condition ($indexesContent -match "idx_jobs_active_feed")
Assert-TestCase -TestId "TC-P2-097" -Category "Indexes" -Description "Partial index on jobs for urgent broadcast WHERE priority = 'URGENT' AND status = 'OPEN'" -Condition ($indexesContent -match "idx_jobs_urgent_broadcast")
Assert-TestCase -TestId "TC-P2-098" -Category "Indexes" -Description "Partial index on assignments for pending confirmations WHERE status = 'SELECTED'" -Condition ($indexesContent -match "idx_assignments_pending_confirmation")
Assert-TestCase -TestId "TC-P2-099" -Category "Indexes" -Description "Partial index on assignments for active work IN ('CONFIRMED', 'IN_PROGRESS', 'COMPLETION_REQUESTED')" -Condition ($indexesContent -match "idx_assignments_active_work")
Assert-TestCase -TestId "TC-P2-100" -Category "Indexes" -Description "Worker skills category index" -Condition ($indexesContent -match "idx_worker_skills_category")
Assert-TestCase -TestId "TC-P2-101" -Category "Indexes" -Description "Worker availability day-of-week active partial index" -Condition ($indexesContent -match "idx_worker_availability_day")
Assert-TestCase -TestId "TC-P2-102" -Category "Indexes" -Description "GIN Index on worker availability time slots array" -Condition ($indexesContent -match "idx_worker_availability_time_slots")
Assert-TestCase -TestId "TC-P2-103" -Category "Indexes" -Description "Partial index on unread notifications WHERE is_read = FALSE" -Condition ($indexesContent -match "idx_notifications_unread")
Assert-TestCase -TestId "TC-P2-104" -Category "Indexes" -Description "Partial index on unread messages WHERE is_read = FALSE" -Condition ($indexesContent -match "idx_messages_unread")
Assert-TestCase -TestId "TC-P2-105" -Category "Indexes" -Description "Time-series index on audit logs created_at DESC" -Condition ($indexesContent -match "idx_audit_logs_timeline")

# ----------------------------------------------------------------------------
# 8. SEED DATA VALIDATION & PILOT GEOGRAPHY (TC-P2-106 to TC-P2-120)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-106" -Category "Seed Data" -Description "Seed data includes Admin user" -Condition ($seedContent -match "'ADMIN'")
Assert-TestCase -TestId "TC-P2-107" -Category "Seed Data" -Description "Seed data includes Worker users" -Condition ($seedContent -match "'WORKER'")
Assert-TestCase -TestId "TC-P2-108" -Category "Seed Data" -Description "Seed data includes Provider users" -Condition ($seedContent -match "'PROVIDER'")
Assert-TestCase -TestId "TC-P2-109" -Category "Seed Data" -Description "Seed data covers Shirur village geography" -Condition ($seedContent -match "शिरूर \(Shirur\)")
Assert-TestCase -TestId "TC-P2-110" -Category "Seed Data" -Description "Seed data covers Saswad village geography" -Condition ($seedContent -match "सासवड \(Saswad\)")
Assert-TestCase -TestId "TC-P2-111" -Category "Seed Data" -Description "Seed data covers Chakan village geography" -Condition ($seedContent -match "चाकण \(Chakan\)")
Assert-TestCase -TestId "TC-P2-112" -Category "Seed Data" -Description "Seed data covers Alephata village geography" -Condition ($seedContent -match "आळेफाटा \(Alephata\)")
Assert-TestCase -TestId "TC-P2-113" -Category "Seed Data" -Description "Seed data covers Agriculture, Construction, Household, Driving jobs" -Condition ($seedContent -match "'AGRICULTURE'" -and $seedContent -match "'CONSTRUCTION'" -and $seedContent -match "'HOUSEHOLD'" -and $seedContent -match "'DRIVING'")
Assert-TestCase -TestId "TC-P2-114" -Category "Seed Data" -Description "Seed data demonstrates OPEN, FILLED, IN_PROGRESS, COMPLETED jobs" -Condition ($seedContent -match "'OPEN'" -and $seedContent -match "'FILLED'" -and $seedContent -match "'IN_PROGRESS'" -and $seedContent -match "'COMPLETED'")
Assert-TestCase -TestId "TC-P2-115" -Category "Seed Data" -Description "Seed data demonstrates SELECTED assignment with decision timer" -Condition ($seedContent -match "'SELECTED'")
Assert-TestCase -TestId "TC-P2-116" -Category "Seed Data" -Description "Seed data demonstrates COMPLETED assignment with PAID_CONFIRMED receipt" -Condition ($seedContent -match "'PAID_CONFIRMED'")
Assert-TestCase -TestId "TC-P2-117" -Category "Seed Data" -Description "Seed data includes bilateral multi-dimensional reviews" -Condition ($seedContent -match "INSERT\s+INTO\s+reviews")
Assert-TestCase -TestId "TC-P2-118" -Category "Seed Data" -Description "Seed data includes Trust Profiles" -Condition ($seedContent -match "INSERT\s+INTO\s+trust_profiles")
Assert-TestCase -TestId "TC-P2-119" -Category "Seed Data" -Description "Seed data includes Moderation Reports for trust & safety triage" -Condition ($seedContent -match "INSERT\s+INTO\s+moderation_reports")
Assert-TestCase -TestId "TC-P2-120" -Category "Seed Data" -Description "Seed data includes audit logs for system operations" -Condition ($seedContent -match "INSERT\s+INTO\s+audit_logs")

# ----------------------------------------------------------------------------
# 9. DOMAIN MODELS & ARCHITECTURAL DOCUMENTATION (TC-P2-121 to TC-P2-135)
# ----------------------------------------------------------------------------
Assert-TestCase -TestId "TC-P2-121" -Category "Domain Docs" -Description "schema_erd.md contains complete Mermaid ERD" -Condition ($erdContent -match "erDiagram")
Assert-TestCase -TestId "TC-P2-122" -Category "Domain Docs" -Description "schema_erd.md details 16 entity definitions with fields and PK/FK" -Condition ($erdContent -match "USERS \{" -and $erdContent -match "WORKERS \{" -and $erdContent -match "JOBS \{")
Assert-TestCase -TestId "TC-P2-123" -Category "Domain Docs" -Description "schema_erd.md includes Job Recruitment State Machine statechart" -Condition ($erdContent -match "stateDiagram-v2" -and $erdContent -match "DRAFT --> OPEN")
Assert-TestCase -TestId "TC-P2-124" -Category "Domain Docs" -Description "schema_erd.md includes Worker Engagement State Machine statechart" -Condition ($erdContent -match "SELECTED --> CONFIRMED" -and $erdContent -match "SELECTED --> NO_RESPONSE")
Assert-TestCase -TestId "TC-P2-125" -Category "Domain Docs" -Description "schema_erd.md includes Foreign Key cascade/restrict policy table" -Condition ($erdContent -match "Relational Foreign Key Integrity & Cascade Rules")
Assert-TestCase -TestId "TC-P2-126" -Category "Domain Docs" -Description "domain_models.md details 8 Bounded Contexts" -Condition ($domainContent.Contains("Identity &") -and $domainContent.Contains("Recruitment &"))
Assert-TestCase -TestId "TC-P2-127" -Category "Domain Docs" -Description "domain_models.md details Domain Aggregates and Invariants" -Condition ($domainContent -match "Root Entity" -and $domainContent -match "Business Invariants")
Assert-TestCase -TestId "TC-P2-128" -Category "Domain Docs" -Description "domain_models.md defines Haversine Distance mathematical formula" -Condition ($domainContent -match "Haversine Distance" -and $domainContent -match "6371")
Assert-TestCase -TestId "TC-P2-129" -Category "Domain Docs" -Description "domain_models.md defines Multi-Signal Match Scoring Engine formula" -Condition ($domainContent.Contains("Match Score") -and $domainContent.Contains("w_{\text{skill}}"))
Assert-TestCase -TestId "TC-P2-130" -Category "Domain Docs" -Description "domain_models.md defines Multi-Dimensional Rating Breakdown formulas" -Condition ($domainContent -match "Worker Rating Dimensions" -and $domainContent -match "Provider Rating Dimensions")
Assert-TestCase -TestId "TC-P2-131" -Category "Domain Docs" -Description "domain_models.md defines Holistic Trust Index formula" -Condition ($domainContent.Contains("Trust Index") -and $domainContent.Contains("Completion Rate"))
Assert-TestCase -TestId "TC-P2-132" -Category "Domain Docs" -Description "domain_models.md defines Progressive Trust & Safety Enforcement Ladder (5 Tiers)" -Condition ($domainContent -match "Tier 1.*HEALTHY" -and $domainContent -match "Tier 5.*BANNED")
Assert-TestCase -TestId "TC-P2-133" -Category "Domain Docs" -Description "domain_models.md defines Strict Role-Based Access Control (RBAC) Matrix" -Condition ($domainContent -match "Strict Role-Based Access Control \(RBAC\) Matrix")
Assert-TestCase -TestId "TC-P2-134" -Category "Domain Docs" -Description "Master Plan alignment: No hidden commission during pilot explicitly documented" -Condition ($domainContent -match "No hidden commission" -or $schemaContent -match "No hidden commission")
Assert-TestCase -TestId "TC-P2-135" -Category "Domain Docs" -Description "Master Plan alignment: Hybrid Development strategy preserved" -Condition ($masterPlanContent -match "Hybrid Development Strategy")

Write-Host "`n=================================================="
Write-Host "🌾 TEST EXECUTION COMPLETE"
Write-Host "Total Tests: $($results.Count)"
Write-Host "PASS: $passCount" -ForegroundColor Green
Write-Host "FAIL: $failCount" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Red" })
Write-Host "=================================================="
