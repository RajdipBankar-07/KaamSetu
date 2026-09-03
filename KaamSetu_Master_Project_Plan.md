# 🌾 KaamSetu (कामसेतू) — Rural & Village Local Jobs SaaS Platform
## Master Project Plan & Single Source of Truth (V1 Specification)

---

### 1. Executive Summary & Product Vision

**KaamSetu (कामसेतू)** is a dedicated, **multilingual, hyper-local two-sided marketplace SaaS** engineered to seamlessly connect rural and semi-urban workers with local employers (farmers, households, rural contractors, Gram Panchayats, and local businesses).

The platform transforms informal, word-of-mouth village hiring into a structured, reliable, and transparent digital ecosystem with a **"Village-First (गाव पातळीवर सोपे)"** philosophy:
- **Zero Technical Friction**: High-contrast intuitive icons, large touch targets, minimal typing, voice/audio friendliness.
- **Multilingual Foundation from Day 1**: Complete parity and 1-tap switching between **मराठी (Marathi)**, **हिंदी (Hindi)**, and **English**.
- **Complete Marketplace Lifecycle**: Transitioning beyond basic job posting into an end-to-end marketplace flow: **Discover → Apply → Select → Confirm → Work → Complete → Rate → Trust & Safety**.
- **Direct & Fair Economics**: Transparent daily wages (`₹/दिवस`), direct settlement between worker and employer, with **no hidden commission during the V1 pilot**.

---

### 2. Hybrid Development Strategy (Phases 0 → 12)

To resolve any sequencing conflicts between frontend prototyping and domain modeling, KaamSetu adopts a **Hybrid Development Methodology**. Visual UI/UX is designed early to validate village ergonomics, while backend domain models, state machines, and RBAC security are established before building dependent functional integrations.

```
PHASE 0: Product Discovery & Village Use-Case Validation
   ↓
PHASE 1: UX/UI Design System & Mobile-First Component Library
   ↓
PHASE 2: Domain Architecture + PostgreSQL Database Schema Design
   ↓
PHASE 3: Backend Core + JWT/OTP Authentication + Strict RBAC
   ↓
PHASE 4: Frontend Shell & Multilingual (i18n) Key Framework
   ↓
PHASE 5: Worker & Provider Complete Core Flows
   ↓
PHASE 6: Matching Engine + Notification Center
   ↓
PHASE 7: Job Lifecycle Automation (Auto-FILLED, Auto-EXPIRED, Soft Preservation)
   ↓
PHASE 8: In-App Chat & Privacy-Aware Contact Layer
   ↓
PHASE 9: Multi-Dimensional Ratings + Trust & Safety Engine
   ↓
PHASE 10: Admin Center + Live Marketplace Health Dashboard
   ↓
PHASE 11: Security Hardening, Audit Logging & End-to-End Testing
   ↓
PHASE 12: Hyperlocal Pilot Launch (1 District, 5–10 Villages)
```

> [!IMPORTANT]
> **Implementation Governance Rule for Antigravity**: This Master Project Plan is the frozen Single Source of Truth. Development must proceed strictly **one phase at a time** (e.g., execute Phase 2 domain models and PostgreSQL schema before advancing to Phase 3). No out-of-scope features are to be introduced during implementation.

---

### 3. Onboarding & Two-Sided Role Picker

The onboarding system ensures rapid, low-friction entry tailored to varying digital literacy levels.

```
             Welcome to KaamSetu
             कामसेतू मध्ये आपले स्वागत आहे

        तुम्हाला काय करायचे आहे? / What do you want to do?

        👷 मला काम पाहिजे             👤 मला कामगार पाहिजे
           Find Work                     Find Workers
```

#### Onboarding Workflow:
```
Language Selection (मराठी / हिंदी / English)
   ↓
Mobile Number Input
   ↓
OTP Verification (6-digit fast verification)
   ↓
Choose Primary Role (Worker vs. Provider)
   ↓
Location Selection (District → Taluka → Village / GPS Pinpoint)
   ↓
Basic Profile Setup (Name, Category / Business Type)
   ↓
Role-Specific Dashboard Entry
```

---

### 4. Worker Profiles: Preferences & Granular Availability

Worker profiles capture precise matching attributes to avoid irrelevant job alerts and travel mismatches.

#### A. Job Preferences
- **Preferred Work Categories**: Multi-select checklist (🌾 Agriculture, 🧱 Construction, 🧹 Household, 🎨 Painting, 🚗 Driving, 🌿 Gardening, 🔧 Plumbing/Electrical, 🏛️ Village Community Work).
- **Travel Willingness / Radius**: Selectable distance (`5 km`, `10 km`, `20 km`).
- **Minimum Acceptable Daily Wage**: `₹____ / day`.

#### B. Granular Availability Calendar
- **Day-by-Day Availability Toggle**:
  - Monday: 🟢 Available
  - Tuesday: 🟢 Available
  - Wednesday: 🔴 Not Available
  - Thursday: 🟢 Available
  - Friday: 🟢 Available
  - Saturday: 🟢 Available
  - Sunday: 🔴 Not Available
- **Time Slot Selection**:
  - ☑ सकाळ (Morning)
  - ☑ दुपार (Afternoon)
  - ☑ संध्याकाळ (Evening)
  - ☑ संपूर्ण दिवस (Full Day)

---

### 5. Hyperlocal Location & Distance Matching Engine

Location is a first-class citizen in KaamSetu matching.

- **Worker View**: Displays registered home village with active travel radius filter.
- **Provider View**: When posting a job in Village XYZ, the system computes and ranks nearby eligible workers in real time (e.g., *Rahul — 2.4 km*, *Suresh — 4.1 km*, *Amit — 6.2 km*).
- **Matching Signals Matrix**:
  $$\text{Match Score} = f(\text{Skill Match}, \text{Distance} \le \text{Radius}, \text{Day/Time Availability}, \text{Wage Expectation}, \text{Trust/Rating})$$

---

### 6. Provider Classification & Profiles

Providers are categorized during registration to customize job templates, matching algorithms, and trust analytics:
- 👨‍🌾 **शेतकरी (Farmer)**: Seasonal harvesting, sowing, spraying, tractor operation.
- 🏠 **घरगुती (Household)**: Domestic help, cleaning, cooking, elder care, garden maintenance.
- 🧱 **कंत्राटदार (Contractor)**: Construction masonry, plastering, tiling, painting, helper teams.
- 🏛️ **ग्रामपंचायत (Gram Panchayat / Local Body)**: Sanitation, water supply repairs, public works.
- 🏪 **स्थानिक व्यवसाय (Local Business / Shop)**: Loading/unloading, store helpers, deliveries.
- 👤 **वैयक्तिक (Individual)**: Ad-hoc single-day assistance.

---

### 7. End-to-End Job Lifecycle & Dual State Machine

To maintain absolute database clarity and operational separation, KaamSetu models state at two distinct levels:
1. **Job Level (`job.status`)**: Governs overall recruitment and post lifecycle.
2. **Assignment Level (`assignment.status`)**: Governs individual worker engagement and work progress.

#### A. Dual State Machine Architecture

```
========================================================================================
1. JOB RECRUITMENT STATUS (job.status)
========================================================================================

   [ DRAFT ] ──► [ OPEN ] ──────────────────────┬────────► [ EXPIRED ] (Deadline Reached)
                    │                           │
                    │ All Slots Confirmed       ├────────► [ CANCELLED ] (Provider Aborts)
                    ▼ (Confirmed Count == Cap)  │
                [ FILLED ]                      │
                    │                           │
                    ▼ Work Commences            │
             [ IN_PROGRESS ]                    │
                    │                           │
                    ▼ All Workers Finished      │
               [ COMPLETED ] ───────────────────┘
                    │
                    ▼ Final Archival
                [ CLOSED ]

========================================================================================
2. WORKER ENGAGEMENT STATUS (assignment.status)
========================================================================================

   Worker Applies
        │
        ▼
   [ APPLIED ] ──► Provider Selects ──► [ SELECTED ]
                                              │
                      ┌───────────────────────┼───────────────────────┐
                      │ Worker Confirms       │ Worker Declines       │ Window Timeout (e.g. 24h)
                      ▼                       ▼                       ▼
                [ CONFIRMED ]           [ DECLINED ]           [ NO_RESPONSE ]
                      │                       │                       │
                      │                       └───────────┬───────────┘
                      │                                   ▼
                      │                         Slot Reopens for Other
                      │                           Eligible Workers
                      ▼
               [ IN_PROGRESS ] ◄── Work Starts
                      │
                      │ Worker / Employer Marks Finished
                      ▼
           [ COMPLETION_REQUESTED ]
                      │
                      │ Mutual Bilateral Confirmation
                      ▼
               [ COMPLETED ] ──► Unlocks Bilateral Multi-Dimensional Rating
                                  & Payment Receipt Acknowledgment

   [Exception States for Assignments]:
   - CANCELLED (Worker or Provider cancels with logged reason)
   - NO_SHOW (Reported by Provider if worker fails to arrive, or vice-versa)
   - DISPUTED (Disputed work or wage, routes to Admin queue)
```

#### B. Core Automation Rules:
1. **Server-Side Auto-FILLED Rule**: When $\sum(\text{assignment.status} == \text{CONFIRMED}) == \text{job.workers\_required}$, `job.status` transitions automatically to `FILLED`.
   - New applications are immediately disabled.
   - Normal broadcast matching notifications stop.
   - Provider cannot select additional candidates unless a confirmed worker cancels.
2. **Server-Side Auto-EXPIRED Rule (Soft Expiration)**: When a job's start date/time expires without reaching capacity:
   - Status transitions to `EXPIRED`.
   - **Zero Hard Deletions**: Historical records remain permanently queryable for user dashboards, dispute audits, and platform analytics.

---

### 8. Worker Confirmation, Decline & Timeout Protocols

#### A. Worker Decision Window
To eliminate ghosting and stale selections:
1. Provider reviews applicants and clicks **Select Worker**.
2. Assignment moves to `SELECTED`.
3. Worker receives an instant high-priority alert: *"You have been selected for [Job Title] by [Provider Name] — Please Confirm"*.
4. A **Confirmation Window** (Default: **24 Hours** for standard jobs, **2 Hours** for Urgent jobs) is initiated.

```
                    [ SELECTED ]
                         │
        ┌────────────────┼────────────────┐
        ▼                ▼                ▼
  [ CONFIRMED ]    [ DECLINED ]    [ NO_RESPONSE ]
  Worker accepts   Worker taps     Window expires;
  job terms. Slot  'Decline'. Slot Slot auto-reopens.
  secured.         auto-reopens.   Worker alerted.
```

#### B. Bilateral Completion (`COMPLETION_REQUESTED` → `COMPLETED`)
1. Employer taps **Mark Completed** or Worker taps **Work Finished**.
2. Counterparty receives a prompt: *"Please confirm completion of [Job Title]"*.
3. Once confirmed, status becomes `COMPLETED`, immediately unlocking:
   - Bilateral multi-dimensional rating.
   - Off-platform payment receipt acknowledgment.

---

### 9. Cancellation & No-Show Tracking System

#### A. Cancellation Protocol
- Both parties can cancel with mandatory reason logging (`cancelled_by`, `cancel_reason`, `cancelled_at`).
- System tracks:
  - **Late Cancellations** (< 2 hours before job start).
  - **Repeated Cancellations** (frequency across 30-day sliding window).

#### B. No-Show Protocol
- If a confirmed worker fails to arrive, the provider reports **Worker No-Show**.
- If a provider is unreachable after confirmed arrival, the worker reports **Provider No-Show**.
- Reports feed directly into the **Trust & Safety Engine** for automated warnings and potential restrictions.

---

### 10. Multi-Dimensional Rating & Trust & Safety System

Rather than a single arbitrary score or binary "Bad Rating Ban", KaamSetu implements a weighted, multi-dimensional trust evaluation.

#### A. Multi-Dimensional Rating Breakdown
- **Worker Evaluation (by Provider)**:
  - Overall Score: ⭐ 1.0 – 5.0
  - Work Quality (कामाचा दर्जा)
  - Punctuality (वेळेवर येणे)
  - Behavior & Respect (वागणूक)
  - Reliability (विश्वासार्हता)
- **Provider Evaluation (by Worker)**:
  - Overall Score: ⭐ 1.0 – 5.0
  - Payment Reliability (पैसे वेळेवर दिले)
  - Behavior & Safety (वागणूक व सुरक्षितता)
  - Job Description Accuracy (कामाचे योग्य वर्णन)
  - Clear Communication (स्पष्ट संवाद)

#### B. Trust & Safety Enforcement Ladder
A single poor rating **never** triggers an automatic ban. Enforcement is computed holistically:

$$\text{Trust Index} = f(\text{Avg Rating}, \text{Completion Rate}, \text{No-Show Frequency}, \text{Late Cancellation Rate}, \text{Verified Reports})$$

```
                   Trust Index Evaluation
                             ↓
    ┌────────────────────────┼────────────────────────┐
    ▼                        ▼                        ▼
[🟢 Healthy / Trusted]  [🟡 Warning Issued]     [🟠 Account Restricted]
- Full matching priority - Push/SMS alert        - Job posting/applying
- Verified Badge          - Corrective guidelines   throttled for 7 days
                             ↓                        ↓
                        [🔴 Suspended]           [⛔ Permanent Ban]
                        - Under Admin review     - Severe fraud, harassment,
                        - P2P contact disabled     or repeated safety abuses
```

---

### 11. Safety Architecture: "Report Everywhere" & Privacy Controls

#### A. Universal Report Action
A dedicated **🚩 Report (तक्रार करा)** action is embedded across all user touchpoints:
- `Report Worker` (on worker profile & application cards)
- `Report Provider` (on job detail & employer profile)
- `Report Job` (on public job feed)
- `Report Message` (inside in-app chat)
- `Report Profile` (on public user cards)

All reports are routed directly into the **Admin Moderation Queue** with full audit metadata (user ID, job ID, chat transcripts, timestamps).

#### B. Contact Privacy & Calling
- Phone numbers are masked or gated behind mutually confirmed job states (`CONFIRMED`).
- Direct in-app chat handles early coordination, location pin sharing, and schedule alignment without exposing personal contact details prematurely.

---

### 12. Account & Data Privacy Framework

Given that KaamSetu handles sensitive rural user data (mobile numbers, GPS/village locations, identity documents, and chat records), robust privacy controls are embedded from day one:

#### A. User Privacy Controls
- **Account Deactivation**: User can temporarily deactivate their account; profile is hidden from search and active matching halts immediately.
- **Account Deletion Request**: 1-tap request to delete personal data, profiles, and unconfirmed applications.
- **Contact & Phone Visibility**: Phone numbers remain masked until mutual `CONFIRMED` status is achieved.
- **Location Privacy**: Workers can choose to display only Village/Taluka level location rather than precise GPS coordinates.
- **Notification Permissions**: Granular toggles to enable/disable specific alert channels (SMS, Push, WhatsApp).

#### B. Admin & Legal Data Retention
- When an account is deleted, Personally Identifiable Information (PII) is anonymized/scrubbed.
- Essential operational records (completed job transactions, dispute records, financial agreed rates, and audit logs) are preserved for legal and platform trust integrity.

---

### 13. Transparent V1 Financial Model & Payment Acknowledgment

KaamSetu V1 does not process in-app payments (no wallet or escrow holding), but it provides full financial transparency and payment record tracking:

#### A. Financial Architecture & Off-Platform Settlement
- **Agreed Daily Wage**: Explicitly documented on job posting and assignment (`₹/दिवस`).
- **Direct Settlement**: Employer pays worker directly (Cash / UPI) upon job completion.
- **Zero Hidden Commission**: **"No hidden commission during the V1 pilot"**.

#### B. Payment Record & Worker Acknowledgment
To validate employer credibility and feed the **Payment Reliability** rating dimension, the system records:
- `agreed_wage`: Numeric daily/monthly rate (`e.g., ₹500`).
- `payment_type`: `CASH` / `UPI` / `DIRECT_BANK`.
- `payment_status`: `PENDING` → `PAID_CONFIRMED` → `DISPUTED`.
- `payment_confirmed_by_worker`: Boolean check by worker (*"Payment Received / पैसे मिळाले ✓"*).
- `payment_confirmed_at`: Timestamp of acknowledgment.

---

### 14. Job Prioritization & Recurring Work Models

#### A. Priority Tiers
- 🟢 **Normal (सामान्य)**: Standard local matching broadcast.
- 🔴 **Urgent (तात्काळ / Urgent)**: Highlighted with urgent badges in feed, instant push priority to nearby available workers within 5 km.

#### B. Recurring Work Schedules
Enables long-term hiring (e.g., domestic help, farm caretakers, monthly drivers):
- **Frequency**: Every Monday–Saturday, Weekly on Sundays, or Custom Days.
- **Pay Model**: Daily rate (`₹/दिवस`) or Monthly agreed amount (`₹/महिना`).

---

### 15. Progressive Verification Badges

Verification builds trust gradually without creating onboarding barriers for village users:
1. 📱 **Mobile Verified**: Automated SMS OTP confirmation on signup (Mandatory for all).
2. 📍 **Location Verified**: GPS/Village Panchayat match confirmation.
3. 🪪 **Identity Verified**: Aadhaar / Voter ID verification via Admin OCR/Manual review (Optional for V1).
4. 🛠 **Skill Verified**: Community endorsement or verified contractor reference.
5. ⭐ **Trusted Worker / Employer**: Earned after 5+ completed jobs with $\ge 4.5$ Trust Index.

---

### 16. Discovery, Recommendations & Saved Items

- **Personalized Worker Feed**: Real-time matching based on active category preferences, distance, and daily availability.
- **Saved Items**:
  - Worker: `♡ Save Job (काम सेव्ह करा)` for later viewing.
  - Provider: `♡ Save Worker (कामगार सेव्ह करा)` for quick re-hiring.

---

### 17. The Three Dedicated Dashboards (Strict RBAC)

```
                         KAAMSETU PLATFORM
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
        ▼                        ▼                        ▼
    👷 WORKER                👤 PROVIDER              🛡️ ADMIN
 ├─ Home / Recommended    ├─ Overview Dashboard    ├─ Platform Health
 ├─ Nearby Jobs Feed      ├─ 3-Step Post Job       ├─ User Management
 ├─ My Applied / Active   ├─ My Jobs (Active/Past) ├─ Job Moderation
 ├─ Availability Calendar ├─ Applicant Selection   ├─ Report / Dispute Queue
 ├─ Messages & Chat       ├─ Messages & Chat       ├─ Verification Center
 ├─ Ratings & Trust       ├─ Bilateral Completion  ├─ Audit Security Logs
 └─ Digital Profile       └─ Provider Profile      └─ System Configuration
```

#### Admin Marketplace Health KPIs:
The Admin dashboard tracks real-time marketplace vitality metrics:
- **Total & Active Liquidity**: Active Workers, Active Providers, Open vs. Filled Jobs.
- **Operational Health**:
  - Average Time to First Application.
  - Average Time to Fill a Job.
  - Job Completion Rate ($\%$).
  - Worker & Provider No-Show Rates.
  - 30-Day User Retention & Repeat Hiring Rate.

---

### 18. Multi-Category Notification Center

Notifications are organized into dedicated channels with user preference controls:
1. 🔔 **Jobs (नवीन कामे)**: Matching urgent or recommended jobs.
2. 📋 **Applications (अर्जांची स्थिती)**: Worker applied, shortlisted.
3. 👷 **Selection & Confirmation (निवड व पुष्टी)**: Employer selected you, Worker confirmed/declined.
4. 💬 **Messages (संदेश)**: In-app chat messages.
5. ⏰ **Reminders (स्मरणपत्रे)**: Upcoming job starts in 2 hours.
6. ⭐ **Ratings & Payments (अभिप्राय व पावती)**: Counterparty rated work or confirmed payment receipt.
7. 🛡️ **Safety & Account (खाते व सुरक्षा)**: Verification badges, trust score updates.

---

### 19. Key-Based Day-1 Multilingual (i18n) Architecture

All UI text, system messages, and notifications are structured strictly through localization keys:

```json
{
  "job.apply": {
    "en": "Apply Now",
    "mr": "आता अर्ज करा",
    "hi": "अभी आवेदन करें"
  },
  "worker.action.confirm": {
    "en": "Confirm Job",
    "mr": "कामाची पुष्टी करा",
    "hi": "काम की पुष्टि करें"
  },
  "worker.action.decline": {
    "en": "Decline",
    "mr": "नकार द्या",
    "hi": "अस्वीकार करें"
  },
  "payment.status.received": {
    "en": "Payment Received",
    "mr": "पैसे मिळाले",
    "hi": "भुगतान प्राप्त हुआ"
  },
  "job.status.filled": {
    "en": "Filled",
    "mr": "जागा भरल्या",
    "hi": "पद भर गए"
  }
}
```

---

### 20. Modular Monolith Technical Architecture

To ensure rapid iteration and maintainability, KaamSetu V1 uses a clean **Modular Monolith** architecture.

```
                          Frontend (Next.js + Tailwind CSS)
                                         │
                                         ▼ (REST / WebSocket)
                       Spring Boot Modular Monolith API
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────────┐ │
 │ │  Auth Module  │ │  User Module  │ │ Worker Module │ │  Provider Module  │ │
 │ └───────────────┘ └───────────────┘ └───────────────┘ └───────────────────┘ │
 │ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────────┐ │
 │ │  Job Module   │ │Matching Module│ │  Chat Module  │ │Notification Module│ │
 │ └───────────────┘ └───────────────┘ └───────────────┘ └───────────────────┘ │
 │ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────────┐ │
 │ │ Rating Module │ │ Trust Module  │ │Payment Rec Mod│ │   Admin Module    │ │
 │ └───────────────┘ └───────────────┘ └───────────────┘ └───────────────────┘ │
 └──────────────────────────────────────┬──────────────────────────────────────┘
                                        │
                         PostgreSQL Relational Storage
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │ • users           • workers         • providers       • jobs                │
 │ • job_preferences • availability    • applications    • assignments         │
 │ • payment_records • reviews         • trust_profiles  • reports             │
 │ • privacy_settings• audit_logs      • notifications                         │
 └─────────────────────────────────────────────────────────────────────────────┘
```

---

### 21. Hyperlocal Pilot Launch Strategy

Rather than a broad rollout, V1 will launch with an intentional, localized pilot:
- **Scope**: 1 Target District (e.g., Pune Rural / Shirur–Saswad belt) across **5–10 Villages**.
- **Core Focus Categories**:
  1. 🌾 Agriculture (शेती काम)
  2. 🧱 Construction & Masonry (बांधकाम व गवंडी)
  3. 🧹 Household Help (घरकाम)
  4. 🚗 Transport & Driving (वाहतूक व ड्रायव्हर)
  5. 🔧 Local Trade & Repairs (स्थानिक दुरुस्ती)
- **Validation Milestones**:
  - Time to first application < 30 minutes for urgent jobs.
  - Job fill rate > 75%.
  - No-show rate < 5%.
  - Repeat usage rate > 40% within 30 days.

---

### 22. Explicit V1 Scope Exclusions (Post-V1 Backlog)

The following capabilities are deliberately planned for V2/V3 to maintain simplicity for the V1 pilot:
- ❌ AI neural matching & AI conversational chatbots.
- ❌ In-app wallet, escrow holding, automated payouts.
- ❌ Complex corporate multi-tier payroll.
- ❌ Pan-India multi-state rollout.
- ❌ Languages beyond Marathi, Hindi, English.
- ❌ Complex worker sub-contractor team management.

---

### 23. Implementation Priority Matrix

| Priority | Feature / Module | Architectural Impact |
| :--- | :--- | :--- |
| 🔴 **P0** | **Onboarding & Role Picker** | 2-path entry ("Find Work" vs "Find Workers"), Mobile OTP, Village selection |
| 🔴 **P0** | **Worker Availability & Preferences** | Day-by-day availability toggles, preferred categories, travel radius, wage floor |
| 🔴 **P0** | **Worker Confirmation (`CONFIRMED`/`DECLINED`)** | Explicit `CONFIRMED`, `DECLINED`, and `NO_RESPONSE` timeout handling |
| 🔴 **P0** | **Dual State Machine (`job` vs `assignment`)** | Clean database separation: `job.status` (recruitment) vs `assignment.status` (work) |
| 🔴 **P0** | **Server-Side Auto-FILLED & Auto-EXPIRED** | Hard capacity lock on `CONFIRMED` cap + Soft expiration preserving audits |
| 🔴 **P0** | **Cancellation & No-Show Tracking** | Mandatory reason codes, late cancellation & no-show incident logging |
| 🔴 **P0** | **Off-Platform Payment Recording** | Agreed wage record + worker payment receipt acknowledgment (`PAID_CONFIRMED`) |
| 🔴 **P0** | **Multi-Dimensional Ratings & Trust Ladder**| Weighted evaluation (ratings, no-shows, reports) replacing binary bans |
| 🔴 **P0** | **Universal "Report Everywhere" System** | Report triggers on Workers, Providers, Jobs, Chat, Profiles → Admin Queue |
| 🔴 **P0** | **Account & Data Privacy Controls** | Deactivation, deletion request, masked contact privacy, notification toggles |
| 🔴 **P0** | **Strict 3-Role RBAC (Worker/Provider/Admin)**| Zero cross-role data leakage, authenticated route guards |
| 🔴 **P0** | **Key-Based Multilingual Engine (i18n)** | Marathi + Hindi + English from Day 1 via clean key mapping |
| 🟠 **P1** | **Recommended Jobs & Proximity Ranking** | Distance calculation + skill match feed for workers |
| 🟠 **P1** | **Notification Center & Channel Routing** | 8 event categories + user alert preference toggles |
| 🟠 **P1** | **Bilateral Job Completion Confirmation** | Mutual acknowledgment before ratings and payment receipt unlock |
| 🟠 **P1** | **Progressive Verification Badges** | Mobile Verified, Location Verified, Trusted Worker badge display |
| 🟠 **P1** | **Urgent Job Priority Tiers** | Normal vs Urgent tagging with expedited notifications |
| 🟡 **P2** | **Recurring Job Schedules** | Structured recurring weekly/monthly domestic & farm assignments |
| 🟡 **P2** | **Saved Jobs & Saved Workers** | 1-tap bookmarking for workers and employers |
| 🟡 **P2** | **In-App Direct Chat & Masked Contact** | Privacy-aware messaging layer before phone disclosure |
