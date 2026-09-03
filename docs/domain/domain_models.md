# 🌾 KaamSetu (कामसेतू) — Domain Architecture & Business Modeling Specification
## Phase 2: Domain Layer Architecture (Modular Monolith Specification)

---

## 1. Domain Overview & Modular Boundaries

KaamSetu is engineered around **Domain-Driven Design (DDD)** principles within a clean **Modular Monolith** architecture. The domain is divided into distinct, cohesive bounded contexts:

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           KAAMSETU DOMAIN ARCHITECTURE                          │
├───────────────────┬───────────────────┬───────────────────┬─────────────────────┤
│ 1. Identity &     │ 2. Worker         │ 3. Provider       │ 4. Recruitment &    │
│    Auth Context   │    Profile Context│    Context        │    Jobs Context     │
│  - User Aggregate │  - Worker Profile │  - Provider Prof. │  - Job Aggregate    │
│  - Credentials    │  - Skills Matrix  │  - Institution    │  - Application Aggr.│
│  - Roles (RBAC)   │  - Availability   │    Classification │  - Capacity Limit   │
├───────────────────┼───────────────────┼───────────────────┼─────────────────────┤
│ 5. Engagement &   │ 6. Hyperlocal     │ 7. Rating &       │ 8. Trust, Safety &  │
│    Assignment     │    Matching       │    Settlement     │    Moderation       │
│  - Assignment Aggr│  - Proximity Geo  │  - 5D Review Aggr.│  - Trust Index      │
│  - Decision Timer │  - Match Scoring  │  - Off-Platform   │  - Report Engine    │
│  - Bilateral Comp │  - Feed Ranking   │    Receipt Ack    │  - Moderation Queue │
└───────────────────┴───────────────────┴───────────────────┴─────────────────────┘
```

---

## 2. Core Domain Aggregates, Entities & Value Objects

### 2.1 Identity & Authentication Aggregate
- **Root Entity**: `User`
- **Value Objects**:
  - `MobileNumber` (E.164 validated Indian mobile number)
  - `UserRole` (`WORKER`, `PROVIDER`, `ADMIN`)
  - `LanguagePreference` (`mr`, `hi`, `en`)
  - `VerificationBadges` (`is_mobile_verified`, `is_location_verified`, `is_identity_verified`)
- **Business Invariants**:
  - A mobile number must be unique across active accounts.
  - A user must have exactly one primary active role context (`WORKER`, `PROVIDER`, or `ADMIN`).
  - Account deletion requests initiate a 30-day grace period; PII is scrubbed while transactional financial records are preserved.

### 2.2 Worker Aggregate
- **Root Entity**: `Worker`
- **Associated Entities / Value Objects**:
  - `SkillEntry` (Category enum, skill title, verification badge)
  - `AvailabilityCalendar` (7-day schedule + daily time-slot enums)
  - `WageFloor` (Minimum acceptable daily wage in INR `₹/दिवस`)
  - `TravelRadius` (Selectable matching threshold: `5 km`, `10 km`, `20 km`)
- **Business Invariants**:
  - `min_daily_wage` must be $> 0$.
  - `travel_radius_km` must strictly be one of `5`, `10`, or `20`.
  - Worker must have at least one active work category.

### 2.3 Provider Aggregate
- **Root Entity**: `Provider`
- **Value Objects**:
  - `ProviderType` (`FARMER`, `HOUSEHOLD`, `CONTRACTOR`, `PANCHAYAT`, `BUSINESS`, `INDIVIDUAL`)
  - `PaymentReliabilityScore` (Weighted average of completed payment acknowledgments, $1.00 - 5.00$)
- **Business Invariants**:
  - Provider must specify an accurate village and district for geographic matching.

### 2.4 Job Aggregate (Recruitment State Machine)
- **Root Entity**: `Job`
- **Value Objects**:
  - `WageAmount` (`₹/दिवस`, mandatory $> 0$)
  - `WorkerCapacity` (`workers_required` $\ge 1$, `workers_confirmed` $\ge 0$)
  - `JobPriority` (`NORMAL`, `URGENT`)
  - `RecruitmentStatus` (`DRAFT`, `OPEN`, `FILLED`, `IN_PROGRESS`, `COMPLETED`, `EXPIRED`, `CANCELLED`, `CLOSED`)
- **Business Invariants & Automation Rules**:
  - **Server-Side Auto-FILLED Rule**: When `workers_confirmed == workers_required`, status auto-transitions to `FILLED`. Application submissions are immediately locked.
  - If a confirmed assignment is cancelled or declined, `workers_confirmed` decrements and the job automatically reopens (`OPEN`).
  - **Soft Expiration Rule**: When `start_date < CURRENT_DATE` and job is not filled, status transitions to `EXPIRED` without deleting historical records.

### 2.5 Assignment Aggregate (Worker Engagement State Machine)
- **Root Entity**: `Assignment`
- **Value Objects**:
  - `EngagementStatus` (`APPLIED`, `SELECTED`, `CONFIRMED`, `DECLINED`, `NO_RESPONSE`, `IN_PROGRESS`, `COMPLETION_REQUESTED`, `COMPLETED`, `CANCELLED`, `NO_SHOW`, `DISPUTED`)
  - `DecisionWindowTimer` (Default **24 Hours** for standard jobs, **2 Hours** for `URGENT` jobs)
  - `PaymentRecord` (`payment_type`: CASH/UPI, `payment_status`: PENDING/PAID_CONFIRMED, `payment_confirmed_by_worker`: boolean)
- **Business Invariants**:
  - A worker cannot have multiple active overlapping `CONFIRMED` jobs on the same calendar date.
  - Selection decision window auto-expires to `NO_RESPONSE` if the worker fails to accept/decline within the timeout window.
  - Bilateral completion requires mutual acknowledgment before unlocking reviews and payment receipts.

---

## 3. Hyperlocal Proximity & Matching Mathematical Formula

### 3.1 Proximity Calculation (Haversine Distance)
Given Worker Coordinates $(\phi_w, \lambda_w)$ and Job Coordinates $(\phi_j, \lambda_j)$:

$$d = 2R \arcsin \left( \sqrt{\sin^2\left(\frac{\Delta \phi}{2}\right) + \cos \phi_w \cos \phi_j \sin^2\left(\frac{\Delta \lambda}{2}\right)} \right)$$

where $R = 6371 \text{ km}$.

### 3.2 Multi-Signal Match Scoring Engine
For any worker $w$ evaluated against open job $j$:

$$\text{Match Score}(w, j) = (w_{\text{skill}} \cdot S) + (w_{\text{dist}} \cdot D) + (w_{\text{avail}} \cdot A) + (w_{\text{wage}} \cdot W) + (w_{\text{trust}} \cdot T)$$

Where:
- **Skill Match ($S \in \{0, 1\}$)**: $1$ if job category exists in worker's skill matrix, else $0$.
- **Distance Score ($D \in [0, 1]$)**:
  $$D = \begin{cases} 1 - \left(\frac{d}{\text{radius}_w}\right) & \text{if } d \le \text{radius}_w \\ 0 & \text{if } d > \text{radius}_w \end{cases}$$
- **Availability Score ($A \in \{0, 1\}$)**: $1$ if worker's calendar for `job.start_date` day-of-week is available.
- **Wage Compatibility ($W \in [0, 1]$)**:
  $$W = \begin{cases} 1.0 & \text{if } \text{wage}_j \ge \text{min\_wage}_w \\ \frac{\text{wage}_j}{\text{min\_wage}_w} & \text{if } \text{wage}_j < \text{min\_wage}_w \end{cases}$$
- **Trust & Rating Score ($T \in [0.2, 1.0]$)**: $\frac{\text{trust\_index}_w}{5.0}$

**Standard Weights**:
$$w_{\text{skill}} = 0.35, \quad w_{\text{dist}} = 0.25, \quad w_{\text{avail}} = 0.20, \quad w_{\text{wage}} = 0.10, \quad w_{\text{trust}} = 0.10$$

---

## 4. Multi-Dimensional Rating & Trust Ladder Engine

### 4.1 Rating Dimensions Breakdown

#### Worker Rating Dimensions (Evaluated by Provider):
1. **Work Quality ($Q$)** — $\text{Weight } = 35\%$
2. **Punctuality ($P$)** — $\text{Weight } = 25\%$
3. **Behavior & Respect ($B$)** — $\text{Weight } = 20\%$
4. **Reliability ($R$)** — $\text{Weight } = 20\%$

$$\text{Worker Rating} = 0.35Q + 0.25P + 0.20B + 0.20R$$

#### Provider Rating Dimensions (Evaluated by Worker):
1. **Payment Reliability ($PR$)** — $\text{Weight } = 40\%$
2. **Behavior & Safety ($BS$)** — $\text{Weight } = 25\%$
3. **Job Description Accuracy ($JA$)** — $\text{Weight } = 20\%$
4. **Clear Communication ($CC$)** — $\text{Weight } = 15\%$

$$\text{Provider Rating} = 0.40PR + 0.25BS + 0.20JA + 0.15CC$$

### 4.2 Holistic Trust Index Formula

$$\text{Trust Index} = (\overline{\text{Rating}} \times 0.50) + (\text{Completion Rate} \times 5.0 \times 0.25) - (\text{No-Show Rate} \times 5.0 \times 0.15) - (\text{Verified Reports Count} \times 0.50)$$

### 4.3 Progressive Enforcement Ladder

| Tier | Status | Criteria | Platform Action |
| :--- | :--- | :--- | :--- |
| 🟢 **Tier 1** | `HEALTHY` | Trust Index $\ge 4.2$, No active unresolved disputes | Full matching priority, Verified Trusted badge |
| 🟡 **Tier 2** | `WARNING` | Trust Index $< 4.0$ or 1 No-Show in 30 days | In-app corrective warning, SMS alert |
| 🟠 **Tier 3** | `RESTRICTED`| Trust Index $< 3.2$ or 2 No-Shows in 30 days | Matching feed throttled for 7 days, daily applications capped |
| 🔴 **Tier 4** | `SUSPENDED` | Trust Index $< 2.5$ or Wage theft / Harassment report | Account locked pending Admin review, P2P chat disabled |
| ⛔ **Tier 5** | `BANNED` | Confirmed fraud, severe safety violation, repeated abuse | Permanent IMEI/Phone ban, data preserved for law enforcement |

---

## 5. Strict Role-Based Access Control (RBAC) Matrix

| Domain Operation / Resource | 👷 WORKER | 👤 PROVIDER | 🛡️ ADMIN |
| :--- | :---: | :---: | :---: |
| View Recommended & Nearby Job Feed | ✅ (Within radius) | ❌ | ✅ (All) |
| Apply to Job | ✅ | ❌ | ❌ |
| Confirm / Decline Selection Alert | ✅ (Own) | ❌ | ❌ |
| Update Availability Calendar & Wages | ✅ (Own) | ❌ | ✅ (Admin override)|
| Post New Job Post | ❌ | ✅ | ✅ |
| View Applicants for Job | ❌ | ✅ (Own Jobs) | ✅ (All Jobs) |
| Select Worker for Assignment | ❌ | ✅ (Own Jobs) | ❌ |
| Bilateral Completion Request | ✅ | ✅ | ✅ |
| Acknowledge Payment Receipt (CASH/UPI) | ✅ | ❌ (Provider pays) | ✅ |
| Submit Review / Multi-Dimensional Rating | ✅ (Rate Provider)| ✅ (Rate Worker) | ❌ (Moderates only)|
| Submit Report (Report Everywhere) | ✅ | ✅ | ❌ (Processes queue)|
| Resolve Reports & Moderation Queue | ❌ | ❌ | ✅ |
| Access Platform Health KPI Metrics | ❌ | ❌ | ✅ |
| View Immutable Audit Security Logs | ❌ | ❌ | ✅ |
| Deactivate / Request Account Deletion | ✅ (Own) | ✅ (Own) | ✅ |
