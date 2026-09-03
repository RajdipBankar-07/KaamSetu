-- ============================================================================
-- 🌾 KaamSetu (कामसेतू) — Realistic Pilot Seed Data (Pune Rural District)
-- Version: V1.0 (Phase 2 DDL Seed)
-- Target Geography: Shirur, Saswad, Chakan, Alephata, Bhor, Baramati
-- ============================================================================

-- Clean existing data (for clean migration test)
TRUNCATE TABLE audit_logs, messages, notifications, moderation_reports, reviews,
               assignments, applications, jobs, worker_availability, worker_skills,
               providers, workers, trust_profiles, privacy_settings, saved_items, users
CASCADE;

-- ============================================================================
-- 1. PILOT USERS
-- ============================================================================

-- Admin User
INSERT INTO users (id, mobile, password_hash, role, language_preference, status, is_mobile_verified, is_location_verified, is_identity_verified)
VALUES
('00000000-0000-0000-0000-000000000001', '+919900000001', crypt('Admin@KaamSetu2026', gen_salt('bf')), 'ADMIN', 'mr', 'ACTIVE', TRUE, TRUE, TRUE);

-- Worker Users
INSERT INTO users (id, mobile, password_hash, role, language_preference, status, is_mobile_verified, is_location_verified, is_identity_verified)
VALUES
('10000000-0000-0000-0000-000000000001', '+919822012345', crypt('Worker@123', gen_salt('bf')), 'WORKER', 'mr', 'ACTIVE', TRUE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000002', '+919823344556', crypt('Worker@123', gen_salt('bf')), 'WORKER', 'mr', 'ACTIVE', TRUE, TRUE, FALSE),
('10000000-0000-0000-0000-000000000003', '+919850066778', crypt('Worker@123', gen_salt('bf')), 'WORKER', 'hi', 'ACTIVE', TRUE, TRUE, TRUE),
('10000000-0000-0000-0000-000000000004', '+919766088990', crypt('Worker@123', gen_salt('bf')), 'WORKER', 'mr', 'ACTIVE', TRUE, FALSE, FALSE);

-- Provider Users
INSERT INTO users (id, mobile, password_hash, role, language_preference, status, is_mobile_verified, is_location_verified, is_identity_verified)
VALUES
('20000000-0000-0000-0000-000000000001', '+919423054321', crypt('Provider@123', gen_salt('bf')), 'PROVIDER', 'mr', 'ACTIVE', TRUE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000002', '+919890088776', crypt('Provider@123', gen_salt('bf')), 'PROVIDER', 'mr', 'ACTIVE', TRUE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000003', '+919765432109', crypt('Provider@123', gen_salt('bf')), 'PROVIDER', 'mr', 'ACTIVE', TRUE, TRUE, FALSE),
('20000000-0000-0000-0000-000000000004', '+919922111223', crypt('Provider@123', gen_salt('bf')), 'PROVIDER', 'mr', 'ACTIVE', TRUE, TRUE, TRUE);

-- ============================================================================
-- 2. WORKER PROFILES, SKILLS & AVAILABILITY
-- ============================================================================

INSERT INTO workers (id, user_id, full_name, avatar_url, village, taluka, district, latitude, longitude, travel_radius_km, min_daily_wage, experience_years, bio, rating, trust_index, is_available_today)
VALUES
('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'राहुल शिंदे (Rahul Shinde)', '👷', 'शिरूर (Shirur)', 'Shirur', 'Pune Rural', 18.8256, 74.3789, 10, 600.00, 6, 'अनुभवी शेतमजूर, ट्रॅक्टर चालक व कांदा लागवड तज्ज्ञ.', 4.85, 4.90, TRUE),
('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'सुरेश गायकवाड (Suresh Gaikwad)', '👷', 'सासवड (Saswad)', 'Purandar', 'Pune Rural', 18.3444, 74.0294, 10, 650.00, 8, 'कुशल गवंडी, बांधकाम व प्लास्टर कामाचा प्रदीर्घ अनुभव.', 4.70, 4.80, TRUE),
('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'संगीता मोरे (Sangeeta More)', '👩‍🌾', 'चाकण (Chakan)', 'Khed', 'Pune Rural', 18.7611, 73.8597, 5, 500.00, 4, 'घरकाम, स्वयंपाक मदत व भाजीपाला काढणी काम.', 4.90, 4.95, TRUE),
('30000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 'अमित जाधव (Amit Jadhav)', '👷', 'आळेफाटा (Alephata)', 'Junnar', 'Pune Rural', 19.1678, 74.1123, 20, 700.00, 5, 'प्लंबिंग, मोटार दुरुस्ती व शेती वायरिंग काम.', 4.60, 4.70, FALSE);

-- Worker Skills
INSERT INTO worker_skills (worker_id, category, skill_title, is_primary, verified_by_endorsement)
VALUES
('30000000-0000-0000-0000-000000000001', 'AGRICULTURE', 'कांदा लागवड व फवारणी', TRUE, TRUE),
('30000000-0000-0000-0000-000000000001', 'DRIVING', 'ट्रॅक्टर व मळणी यंत्र चालक', FALSE, TRUE),
('30000000-0000-0000-0000-000000000002', 'CONSTRUCTION', 'गवंडी काम व वीट बांधकाम', TRUE, TRUE),
('30000000-0000-0000-0000-000000000002', 'PLUMBING_ELECTRICAL', 'पाणी पाईपलाईन व टाकी फिटिंग', FALSE, FALSE),
('30000000-0000-0000-0000-000000000003', 'HOUSEHOLD', 'घरगुती स्वयंपाक व स्वच्छता', TRUE, TRUE),
('30000000-0000-0000-0000-000000000003', 'AGRICULTURE', 'शेततळे स्वच्छता व खुरपणी', FALSE, FALSE),
('30000000-0000-0000-0000-000000000004', 'PLUMBING_ELECTRICAL', 'विहीर मोटार व स्टार्टर दुरुस्ती', TRUE, TRUE);

-- Worker Weekly Availability Calendar (Rahul Shinde)
INSERT INTO worker_availability (worker_id, day_of_week, is_available, time_slots)
VALUES
('30000000-0000-0000-0000-000000000001', 'MON', TRUE, ARRAY['FULL_DAY']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'TUE', TRUE, ARRAY['FULL_DAY']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'WED', TRUE, ARRAY['FULL_DAY']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'THU', TRUE, ARRAY['FULL_DAY']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'FRI', TRUE, ARRAY['FULL_DAY']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'SAT', TRUE, ARRAY['MORNING', 'AFTERNOON']::time_slot_enum[]),
('30000000-0000-0000-0000-000000000001', 'SUN', FALSE, ARRAY[]::time_slot_enum[]);

-- ============================================================================
-- 3. PROVIDER PROFILES
-- ============================================================================

INSERT INTO providers (id, user_id, name, business_name, provider_type, village, taluka, district, latitude, longitude, rating, payment_reliability_score, is_verified)
VALUES
('40000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'बाळासाहेब पाटील (Balasaheb Patil)', 'पाटील ॲग्रो फार्म्स', 'FARMER', 'सासवड (Saswad)', 'Purandar', 'Pune Rural', 18.3444, 74.0294, 4.90, 5.00, TRUE),
('40000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002', 'राजेश कन्स्ट्रक्शन (Rajesh Contractors)', 'राजेश बिल्डर्स व कंत्राटदार', 'CONTRACTOR', 'चाकण (Chakan)', 'Khed', 'Pune Rural', 18.7611, 73.8597, 4.75, 4.85, TRUE),
('40000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003', 'श्रीमती सुनीता कुलकर्णी (Mrs. Kulkarni)', NULL, 'HOUSEHOLD', 'शिरूर (Shirur)', 'Shirur', 'Pune Rural', 18.8256, 74.3789, 4.95, 5.00, TRUE),
('40000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004', 'ग्रामपंचायत सासवड (Gram Panchayat Saswad)', 'सासवड स्थानिक स्वराज्य संस्था', 'PANCHAYAT', 'सासवड (Saswad)', 'Purandar', 'Pune Rural', 18.3444, 74.0294, 4.60, 4.50, TRUE);

-- ============================================================================
-- 4. JOBS (Open, Filled, In-Progress, Completed)
-- ============================================================================

INSERT INTO jobs (id, provider_id, title, category, description, village, taluka, district, latitude, longitude, daily_wage, workers_required, workers_confirmed, priority, status, is_recurring, start_date, duration_days, expires_at)
VALUES
-- Job 1: Open Urgent Farm Labor Job
('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'कांदा लागवड व शेती काम (Onion Sowing & Farm Labor)', 'AGRICULTURE', 'तात्काळ कांदा रोपे लागवड व खत घालण्यासाठी अनुभवी शेतमजूर हवेत. दुपारचे जेवण व चहा मिळेल.', 'सासवड (Saswad)', 'Purandar', 'Pune Rural', 18.3444, 74.0294, 650.00, 4, 3, 'URGENT', 'OPEN', FALSE, CURRENT_DATE, 3, NOW() + INTERVAL '24 hours'),

-- Job 2: Filled Construction Job
('50000000-0000-0000-0000-000000000002', '40000000-0000-0000-0000-000000000002', 'गवंडी काम व प्लास्टर मदतनीस (Masonry & Plaster Helper)', 'CONSTRUCTION', 'नवीन घराच्या बांधकामासाठी कुशल गवंडी व मदतनीस. रोज संध्याकाळी रोख मोबदला.', 'चाकण (Chakan)', 'Khed', 'Pune Rural', 18.7611, 73.8597, 750.00, 3, 3, 'NORMAL', 'FILLED', FALSE, CURRENT_DATE + 1, 7, NOW() + INTERVAL '48 hours'),

-- Job 3: In-Progress Recurring Domestic Help
('50000000-0000-0000-0000-000000000003', '40000000-0000-0000-0000-000000000003', 'घरकाम व स्वयंपाक मदत (Daily Domestic Help)', 'HOUSEHOLD', 'सकाळचे भांडी, कपडे व स्वयंपाकात मदत. शिरूर गावातील कुटुंब.', 'शिरूर (Shirur)', 'Shirur', 'Pune Rural', 18.8256, 74.3789, 500.00, 1, 1, 'NORMAL', 'IN_PROGRESS', TRUE, CURRENT_DATE - 5, 30, NULL),

-- Job 4: Completed Threshing Work
('50000000-0000-0000-0000-000000000004', '40000000-0000-0000-0000-000000000001', 'सोयाबीन मळणी व ट्रॅक्टर चालक (Tractor Driver & Threshing)', 'DRIVING', 'सोयाबीन मळणी यंत्र चालवण्यासाठी अनुभवी ट्रॅक्टर चालक.', 'सासवड (Saswad)', 'Purandar', 'Pune Rural', 18.3444, 74.0294, 800.00, 2, 2, 'URGENT', 'COMPLETED', FALSE, CURRENT_DATE - 3, 2, NULL);

-- ============================================================================
-- 5. APPLICATIONS & ASSIGNMENTS (Dual State Machine Simulation)
-- ============================================================================

-- Applications for Job 1 (Onion Sowing)
INSERT INTO applications (id, job_id, worker_id, status, applied_wage, worker_notes)
VALUES
('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'APPLIED', 650.00, 'मी 3 दिवस पूर्ण वेळेत हजर राहीन.'),
('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000002', 'SHORTLISTED', 650.00, 'मी आणि माझा सहकारी सोबत येऊ शकतो.');

-- Assignments for Job 1 (Selected worker in 24h decision window)
INSERT INTO assignments (id, job_id, application_id, worker_id, provider_id, status, agreed_wage, selection_window_expires_at, payment_type, payment_status)
VALUES
('70000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'SELECTED', 650.00, NOW() + INTERVAL '20 hours', 'CASH', 'PENDING');

-- Assignment for Job 4 (Completed work with acknowledged payment)
INSERT INTO assignments (id, job_id, worker_id, provider_id, status, agreed_wage, confirmed_at, work_started_at, completed_at, payment_type, payment_status, payment_confirmed_by_worker, payment_confirmed_at)
VALUES
('70000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'COMPLETED', 800.00, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 day', 'UPI', 'PAID_CONFIRMED', TRUE, NOW() - INTERVAL '1 day');

-- ============================================================================
-- 6. MULTI-DIMENSIONAL RATINGS & REVIEWS
-- ============================================================================

-- Provider rates Rahul Shinde (Worker) for Job 4
INSERT INTO reviews (id, assignment_id, reviewer_user_id, reviewee_user_id, reviewer_role, overall_score, work_quality_score, punctuality_score, behavior_score, reliability_score, comment)
VALUES
('80000000-0000-0000-0000-000000000001', '70000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'PROVIDER', 4.90, 5.00, 4.80, 5.00, 4.80, 'वेळेवर आले आणि ट्रॅक्टर व मळणीचे काम अतिशय चोख पार पाडले.');

-- Worker rates Balasaheb Patil (Provider) for Job 4
INSERT INTO reviews (id, assignment_id, reviewer_user_id, reviewee_user_id, reviewer_role, overall_score, payment_reliability_score, safety_score, job_accuracy_score, communication_score, comment)
VALUES
('80000000-0000-0000-0000-000000000002', '70000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'WORKER', 5.00, 5.00, 5.00, 5.00, 5.00, 'काम संपताच संध्याकाळी UPI द्वारे पूर्ण ₹800 दिले. आदरातिथ्य चांगले होते.');

-- ============================================================================
-- 7. TRUST PROFILES & PRIVACY SETTINGS
-- ============================================================================

INSERT INTO trust_profiles (user_id, trust_index, status, completed_jobs_count, no_show_count, late_cancellation_count, verified_reports_count)
VALUES
('10000000-0000-0000-0000-000000000001', 4.90, 'HEALTHY', 12, 0, 0, 0),
('10000000-0000-0000-0000-000000000002', 4.80, 'HEALTHY', 8, 0, 1, 0),
('20000000-0000-0000-0000-000000000001', 4.95, 'HEALTHY', 15, 0, 0, 0),
('20000000-0000-0000-0000-000000000002', 4.75, 'HEALTHY', 6, 0, 0, 0);

INSERT INTO privacy_settings (user_id, phone_masking_enabled, location_granularity, allow_sms_alerts, allow_push_alerts, allow_whatsapp_alerts)
VALUES
('10000000-0000-0000-0000-000000000001', TRUE, 'VILLAGE', TRUE, TRUE, TRUE),
('20000000-0000-0000-0000-000000000001', TRUE, 'VILLAGE', TRUE, TRUE, TRUE);

-- ============================================================================
-- 8. MODERATION REPORTS, NOTIFICATIONS & MESSAGES
-- ============================================================================

INSERT INTO moderation_reports (reporter_user_id, reported_user_id, target_type, category, reason, status)
VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000004', 'WORKER', 'NO_SHOW', 'Confirmed job acceptance but failed to show up without notification.', 'PENDING_REVIEW');

INSERT INTO notifications (user_id, channel, title_key, message_key, params, action_url, is_read)
VALUES
('10000000-0000-0000-0000-000000000001', 'SELECTION', 'notification.selection', 'worker.confirmModal.title', '{"jobTitle": "कांदा लागवड व शेती काम", "providerName": "बाळासाहेब पाटील"}'::jsonb, '/jobs/50000000-0000-0000-0000-000000000001', FALSE),
('10000000-0000-0000-0000-000000000001', 'RATING_PAYMENT', 'notification.ratings', 'payment.status.received', '{"amount": "800", "providerName": "बाळासाहेब पाटील"}'::jsonb, '/assignments/70000000-0000-0000-0000-000000000002', TRUE);

INSERT INTO messages (job_id, sender_user_id, recipient_user_id, message_text)
VALUES
('50000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'नमस्कार राहुल, उद्या सकाळी 8 वाजता सासवड शेतात पोहोचू शकता का?'),
('50000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'हो पाटील साहेब, मी सकाळी 7:45 ला शेतात हजर राहीन.');

-- ============================================================================
-- 9. AUDIT LOGS
-- ============================================================================

INSERT INTO audit_logs (actor_user_id, action_type, entity_name, entity_id, new_state)
VALUES
('10000000-0000-0000-0000-000000000001', 'USER_REGISTERED', 'users', '10000000-0000-0000-0000-000000000001', '{"role": "WORKER", "mobile": "+919822012345"}'::jsonb),
('20000000-0000-0000-0000-000000000001', 'JOB_CREATED', 'jobs', '50000000-0000-0000-0000-000000000001', '{"title": "कांदा लागवड", "wage": 650, "priority": "URGENT"}'::jsonb);
