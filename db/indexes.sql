-- ============================================================================
-- 🌾 KaamSetu (कामसेतू) — Database Indexes & Query Optimization
-- Version: V1.0 (Phase 2 DDL)
-- Focus: Hyperlocal Proximity, Dual State Queries & Low-Latency Lookups
-- ============================================================================

-- ============================================================================
-- 1. HYPERLOCAL & PROXIMITY GEOGRAPHIC INDEXES
-- ============================================================================

-- Fast B-Tree lookup for Workers in District/Taluka/Village
CREATE INDEX idx_workers_location ON workers(district, taluka, village);
CREATE INDEX idx_workers_coordinates ON workers(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;
CREATE INDEX idx_workers_min_wage ON workers(min_daily_wage);
CREATE INDEX idx_workers_rating ON workers(rating DESC, trust_index DESC);

-- Fast B-Tree lookup for Providers in District/Taluka/Village
CREATE INDEX idx_providers_location ON providers(district, taluka, village);
CREATE INDEX idx_providers_type ON providers(provider_type);
CREATE INDEX idx_providers_rating ON providers(rating DESC, payment_reliability_score DESC);

-- Fast B-Tree lookup for Jobs in District/Taluka/Village & Category
CREATE INDEX idx_jobs_location_category ON jobs(district, taluka, village, category);
CREATE INDEX idx_jobs_coordinates ON jobs(latitude, longitude) WHERE latitude IS NOT NULL AND longitude IS NOT NULL;
CREATE INDEX idx_jobs_start_date ON jobs(start_date ASC);
CREATE INDEX idx_jobs_created_at ON jobs(created_at DESC);

-- ============================================================================
-- 2. DUAL STATE MACHINE & PARTIAL QUERY INDEXES
-- ============================================================================

-- Partial index for Open & Urgent Jobs (Active Matching Feed)
CREATE INDEX idx_jobs_active_feed ON jobs(category, daily_wage DESC, priority, start_date)
WHERE status = 'OPEN';

-- Partial index for Urgent Jobs push broadcast
CREATE INDEX idx_jobs_urgent_broadcast ON jobs(village, category, daily_wage)
WHERE priority = 'URGENT' AND status = 'OPEN';

-- Partial index for Worker Decision Window (Pending Confirmations)
CREATE INDEX idx_assignments_pending_confirmation ON assignments(worker_id, selection_window_expires_at)
WHERE status = 'SELECTED';

-- Partial index for In-Progress and Active Work
CREATE INDEX idx_assignments_active_work ON assignments(worker_id, provider_id, job_id)
WHERE status IN ('CONFIRMED', 'IN_PROGRESS', 'COMPLETION_REQUESTED');

-- Status-specific indexes for worker application history
CREATE INDEX idx_assignments_worker_history ON assignments(worker_id, status, created_at DESC);
CREATE INDEX idx_assignments_provider_history ON assignments(provider_id, status, created_at DESC);
CREATE INDEX idx_assignments_job_status ON assignments(job_id, status);

-- ============================================================================
-- 3. WORKER AVAILABILITY & SKILLS INDEXES
-- ============================================================================

-- Normalized skill category lookup
CREATE INDEX idx_worker_skills_category ON worker_skills(category, worker_id);
CREATE INDEX idx_worker_skills_worker ON worker_skills(worker_id);

-- Day-of-week active availability lookup
CREATE INDEX idx_worker_availability_day ON worker_availability(day_of_week, is_available)
WHERE is_available = TRUE;

-- GIN Index on time_slots array
CREATE INDEX idx_worker_availability_time_slots ON worker_availability USING GIN (time_slots);

-- ============================================================================
-- 4. NOTIFICATIONS, MESSAGES & RATINGS
-- ============================================================================

-- Partial index for Unread Notifications (Header Count Badge)
CREATE INDEX idx_notifications_unread ON notifications(user_id, channel, created_at DESC)
WHERE is_read = FALSE;

-- Notification user feed pagination
CREATE INDEX idx_notifications_user_feed ON notifications(user_id, created_at DESC);

-- Unread messages in direct chat
CREATE INDEX idx_messages_unread ON messages(recipient_user_id, is_read, created_at DESC)
WHERE is_read = FALSE;

-- Conversation history lookup
CREATE INDEX idx_messages_conversation ON messages(job_id, sender_user_id, recipient_user_id, created_at ASC);

-- Multi-dimensional review lookups
CREATE INDEX idx_reviews_reviewee ON reviews(reviewee_user_id, overall_score);
CREATE INDEX idx_reviews_assignment ON reviews(assignment_id);

-- ============================================================================
-- 5. TRUST, SAFETY & AUDIT LOGS
-- ============================================================================

-- Moderation Queue Triage
CREATE INDEX idx_moderation_reports_status ON moderation_reports(status, created_at ASC)
WHERE status IN ('PENDING_REVIEW', 'UNDER_INVESTIGATION');

-- Target Entity Reports count
CREATE INDEX idx_moderation_reports_target ON moderation_reports(reported_user_id, category);

-- User Trust Status
CREATE INDEX idx_trust_profiles_status ON trust_profiles(status, trust_index ASC);

-- Saved Items fast lookup
CREATE INDEX idx_saved_items_user ON saved_items(user_id, target_type, target_id);

-- Audit Logs Time Series Index
CREATE INDEX idx_audit_logs_timeline ON audit_logs(created_at DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_user_id, action_type);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
