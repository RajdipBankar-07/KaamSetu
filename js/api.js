/**
 * 🌾 KaamSetu (कामसेतू) - Centralized API Client & Networking Layer
 * Connects Frontend Shell strictly with Spring Boot 3 Backend API
 * Direct Database Authentication, Authorization & Resource Synchronization
 */

const ApiClient = (function () {
    const API_BASE_URL = window.API_BASE_URL || 
        (typeof process !== 'undefined' && process.env && process.env.NEXT_PUBLIC_API_BASE_URL) || 
        'http://localhost:8090/api/v1';

    let currentServerStatus = 'BACKEND_CHECKING'; // 'BACKEND_CHECKING' | 'BACKEND_ONLINE' | 'BACKEND_OFFLINE'
    let lastHealthCheck = null;

    // Helper to get JWT token from localStorage
    function getToken() {
        return localStorage.getItem('kaamsetu_jwt_token');
    }

    function setServerStatus(status, details = {}) {
        const prevStatus = currentServerStatus;
        currentServerStatus = status;
        lastHealthCheck = { status, timestamp: Date.now(), ...details };

        if (typeof window !== 'undefined') {
            window.dispatchEvent(new CustomEvent('backend:status', {
                detail: { status, prevStatus, ...details, timestamp: Date.now() }
            }));

            if (window.appState && typeof window.appState.setBackendStatus === 'function') {
                window.appState.setBackendStatus(status);
            }

            const banner = document.getElementById('backend-health-banner');
            if (banner) {
                banner.style.display = (status === 'BACKEND_ONLINE') ? 'none' : 'block';
            }
        }
    }

    /**
     * Dedicated Health Check Gate: GET /health
     * Distinguishes Backend Online vs Backend Offline vs Database Failure.
     */
    async function checkHealth(timeoutMs = 4000) {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

        try {
            const response = await fetch(`${API_BASE_URL}/health`, {
                method: 'GET',
                headers: { 'Accept': 'application/json' },
                signal: controller.signal
            });
            clearTimeout(timeoutId);

            let data = null;
            try {
                data = await response.json();
            } catch (jsonErr) {
                // Not JSON
            }

            if (response.ok) {
                const isHealthy = !data || data.status === 'UP' || data.database === 'UP' || response.status === 200;
                if (isHealthy) {
                    setServerStatus('BACKEND_ONLINE', data || { status: 'UP' });
                    return { ok: true, status: 'BACKEND_ONLINE', data };
                }
            }

            if (response.status === 503 || (data && (data.status === 'DOWN' || data.database === 'DOWN'))) {
                const dbErr = new Error("Database service is currently initializing or unavailable.");
                dbErr.isDatabaseDown = true;
                dbErr.status = 503;
                dbErr.data = data;
                setServerStatus('BACKEND_OFFLINE', { isDatabaseDown: true, error: dbErr.message, data });
                throw dbErr;
            }

            throw new Error(`Health check returned unexpected status HTTP ${response.status}`);
        } catch (error) {
            clearTimeout(timeoutId);
            const isTimeout = error.name === 'AbortError';
            const connError = new Error(isTimeout ? "Connection timed out. KaamSetu server did not respond." : (error.message || "KaamSetu server is currently unavailable."));
            connError.isNetworkError = true;
            connError.isTimeout = isTimeout;
            connError.isDatabaseDown = Boolean(error.isDatabaseDown);
            connError.status = error.status || 503;
            setServerStatus('BACKEND_OFFLINE', { isNetworkError: true, isTimeout, isDatabaseDown: connError.isDatabaseDown, error: connError.message });
            throw connError;
        }
    }

    // Generic fetch wrapper with automatic JWT Bearer token injection, AbortController timeout & status interception
    async function request(endpoint, options = {}, timeoutMs = 8000) {
        const url = `${API_BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        const token = getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

        try {
            const response = await fetch(url, {
                ...options,
                headers,
                signal: options.signal || controller.signal
            });
            clearTimeout(timeoutId);

            // Handle 401 Unauthorized (Expired or invalid token)
            if (response.status === 401) {
                console.warn('🔒 401 Unauthorized - Clearing expired session');
                if (typeof AuthManager !== 'undefined') {
                    AuthManager.clearSession();
                }
                const authErr = new Error('Authentication failed or session expired. Please login again.');
                authErr.status = 401;
                authErr.errorCode = 'UNAUTHORIZED';
                throw authErr;
            }

            // Handle 403 Forbidden (Insufficient role permissions)
            if (response.status === 403) {
                const forbidErr = new Error('You do not have permission to access this resource.');
                forbidErr.status = 403;
                forbidErr.errorCode = 'FORBIDDEN';
                throw forbidErr;
            }

            // Parse response JSON safely
            let data = null;
            try {
                data = await response.json();
            } catch (jsonErr) {
                if (!response.ok) {
                    throw new Error(`Server returned HTTP ${response.status}: ${response.statusText}`);
                }
                return null;
            }

            if (!response.ok || (data && data.success === false)) {
                const errMsg = (data && data.message) ? data.message : `Request failed with status ${response.status}`;
                const err = new Error(errMsg);
                if (data) {
                    err.errorCode = data.errorCode;
                    err.messageKey = data.messageKey;
                }
                err.status = response.status;
                throw err;
            }

            // Mark backend as online on successful API response
            if (currentServerStatus !== 'BACKEND_ONLINE') {
                setServerStatus('BACKEND_ONLINE');
            }

            return (data && data.data !== undefined) ? data.data : data;
        } catch (error) {
            clearTimeout(timeoutId);
            const isTimeout = error.name === 'AbortError';

            // Check if network error (backend offline / server stopped / connection refused / timeout)
            if (isTimeout || error.name === 'TypeError' || error.message.includes('Failed to fetch') || error.message.includes('NetworkError') || error.message.includes('ERR_CONNECTION_REFUSED') || error.message.includes('Network request failed') || error.status === 503) {
                const connError = new Error(isTimeout ? "Request timed out. Server connection lost." : "KaamSetu server is currently unavailable. Please try again when the server is online.");
                connError.isNetworkError = true;
                connError.isTimeout = isTimeout;
                connError.status = 503;
                setServerStatus('BACKEND_OFFLINE', { isNetworkError: true, isTimeout });
                throw connError;
            }
            throw error;
        }
    }

    return {
        // Backend Base URL & Status Gate
        getBaseUrl: () => API_BASE_URL,
        getServerStatus: () => currentServerStatus,
        getLastHealthCheck: () => lastHealthCheck,
        checkHealth,
        setServerStatus,

        // Auth Endpoints (Username + Password + Registration)
        register: (registerData) => request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(registerData)
        }),

        loginWithPassword: (usernameOrMobile, password) => request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username: usernameOrMobile, mobile: usernameOrMobile, password })
        }),

        changePassword: (currentPassword, newPassword, confirmNewPassword) => request('/auth/change-password', {
            method: 'POST',
            body: JSON.stringify({ currentPassword, newPassword, confirmNewPassword })
        }),

        sendOtp: (mobile) => request('/auth/otp/send', {
            method: 'POST',
            body: JSON.stringify({ mobile })
        }),

        verifyOtp: (mobile, otp, preferredRole, languagePreference) => request('/auth/otp/verify', {
            method: 'POST',
            body: JSON.stringify({ mobile, otp, preferredRole, languagePreference })
        }),

        sendEmailOtp: (email) => request('/auth/email/otp/send', {
            method: 'POST',
            body: JSON.stringify({ email })
        }),

        verifyEmailOtp: (email, otp) => request('/auth/email/otp/verify', {
            method: 'POST',
            body: JSON.stringify({ email, otp })
        }),

        verifyEmail: (token) => request(`/auth/verify-email?token=${encodeURIComponent(token)}`, {
            method: 'GET'
        }),

        resendEmailVerification: (email) => request(`/auth/email/resend?email=${encodeURIComponent(email)}`, {
            method: 'POST'
        }),

        getCurrentUser: () => request('/auth/me', {
            method: 'GET'
        }),

        checkUnique: (type, value) => request(`/auth/check-unique?type=${encodeURIComponent(type)}&value=${encodeURIComponent(value)}`, {
            method: 'GET'
        }),

        activateWorkerProfile: (data) => request('/auth/profiles/worker', {
            method: 'POST',
            body: JSON.stringify(data || {})
        }),

        activateProviderProfile: (data) => request('/auth/profiles/provider', {
            method: 'POST',
            body: JSON.stringify(data || {})
        }),

        switchActiveRole: (role) => request('/auth/profiles/switch-role', {
            method: 'POST',
            body: JSON.stringify({ role })
        }),

        getPublicStats: () => request('/public/stats', {
            method: 'GET'
        }),

        // Worker Endpoints (Strictly DB Authenticated)
        getWorkerProfile: () => request('/worker/profile', {
            method: 'GET'
        }),

        getWorkerDashboardStats: () => request('/worker/dashboard/stats', {
            method: 'GET'
        }),

        updateWorkerProfile: (profileData) => request('/worker/profile', {
            method: 'PUT',
            body: JSON.stringify(profileData)
        }),

        toggleWorkerAvailability: (isAvailable) => request(`/worker/availability/toggle?isAvailable=${isAvailable}`, {
            method: 'PATCH'
        }),

        // Job Provider Endpoints (Strictly DB Authenticated)
        getProviderProfile: () => request('/provider/profile', {
            method: 'GET'
        }),

        getProviderDashboardStats: () => request('/provider/dashboard/stats', {
            method: 'GET'
        }),

        updateProviderProfile: (profileData) => request('/provider/profile', {
            method: 'PUT',
            body: JSON.stringify(profileData)
        }),

        createJob: (jobData) => request('/jobs', {
            method: 'POST',
            body: JSON.stringify(jobData)
        }),

        getMyJobs: (status) => {
            const query = status ? `?status=${status}` : '';
            return request(`/jobs/my${query}`, { method: 'GET' });
        },

        getJobApplicants: (jobId) => request(`/provider/jobs/${jobId}/applications`, {
            method: 'GET'
        }),

        // Job Discovery & Applications (Worker)
        discoverJobs: (params = {}) => {
            const query = new URLSearchParams(params).toString();
            return request(`/jobs${query ? '?' + query : ''}`, { method: 'GET' });
        },

        getJobDetails: (jobId) => request(`/jobs/${jobId}`, {
            method: 'GET'
        }),

        applyForJob: (jobId) => request(`/jobs/${jobId}/apply`, {
            method: 'POST'
        }),

        getMyApplications: () => request('/applications/my', {
            method: 'GET'
        }),

        withdrawApplication: (applicationId) => request(`/applications/${applicationId}/withdraw`, {
            method: 'POST'
        }),

        // Assignment Endpoints
        selectWorker: (jobId, workerId) => request(`/assignments/jobs/${jobId}/select/${workerId}`, {
            method: 'POST'
        }),

        confirmAssignment: (id) => request(`/assignments/${id}/confirm`, {
            method: 'POST'
        }),

        declineAssignment: (id) => request(`/assignments/${id}/decline`, {
            method: 'POST'
        }),

        getWorkerAssignments: () => request('/assignments/my/worker', {
            method: 'GET'
        }),

        getProviderAssignments: () => request('/assignments/my/provider', {
            method: 'GET'
        }),

        // Notification Endpoints
        getNotifications: (category) => {
            const query = category && category !== 'all' ? `?category=${encodeURIComponent(category)}` : '';
            return request(`/notifications${query}`, { method: 'GET' });
        },

        getUnreadNotificationCount: () => request('/notifications/unread-count', {
            method: 'GET'
        }),

        markNotificationAsRead: (id) => request(`/notifications/${id}/read`, {
            method: 'PATCH'
        }),

        markAllNotificationsAsRead: () => request('/notifications/read-all', {
            method: 'POST'
        }),

        // Matching Engine Endpoints
        getRecommendedJobs: (maxRadiusKm) => {
            const query = maxRadiusKm ? `?maxRadiusKm=${maxRadiusKm}` : '';
            return request(`/matching/jobs/recommended${query}`, { method: 'GET' });
        },

        getRecommendedWorkers: (category, maxRadiusKm) => {
            let q = [];
            if (category) q.push(`category=${encodeURIComponent(category)}`);
            if (maxRadiusKm) q.push(`maxRadiusKm=${maxRadiusKm}`);
            const query = q.length ? `?${q.join('&')}` : '';
            return request(`/matching/workers/recommended${query}`, { method: 'GET' });
        },

        // Lifecycle & Settlement Endpoints
        startWork: (id) => request(`/assignments/${id}/start`, {
            method: 'POST'
        }),

        requestCompletion: (id) => request(`/assignments/${id}/request-completion`, {
            method: 'POST'
        }),

        confirmCompletion: (id) => request(`/assignments/${id}/confirm-completion`, {
            method: 'POST'
        }),

        confirmPayment: (id, { overtimeAmount, additionalAmount, paymentType } = {}) => {
            let q = [];
            if (overtimeAmount !== undefined && overtimeAmount !== null) q.push(`overtimeAmount=${overtimeAmount}`);
            if (additionalAmount !== undefined && additionalAmount !== null) q.push(`additionalAmount=${additionalAmount}`);
            if (paymentType) q.push(`paymentType=${encodeURIComponent(paymentType)}`);
            const query = q.length ? `?${q.join('&')}` : '';
            return request(`/assignments/${id}/confirm-payment${query}`, { method: 'POST' });
        },

        recordAttendance: (id, { workDate, startTime, endTime, status, remarks } = {}) => {
            let q = [];
            if (workDate) q.push(`workDate=${encodeURIComponent(workDate)}`);
            if (startTime) q.push(`startTime=${encodeURIComponent(startTime)}`);
            if (endTime) q.push(`endTime=${encodeURIComponent(endTime)}`);
            if (status) q.push(`status=${encodeURIComponent(status)}`);
            if (remarks) q.push(`remarks=${encodeURIComponent(remarks)}`);
            const query = q.length ? `?${q.join('&')}` : '';
            return request(`/assignments/${id}/attendance${query}`, { method: 'POST' });
        },

        getAttendanceHistory: (id) => request(`/assignments/${id}/attendance`, {
            method: 'GET'
        }),

        getWorkerCompletions: () => request('/assignments/my/worker/completions', {
            method: 'GET'
        }),

        getProviderCompletions: () => request('/assignments/my/provider/completions', {
            method: 'GET'
        }),

        getJobCompletions: (jobId) => request(`/assignments/job/${jobId}/completions`, {
            method: 'GET'
        }),

        acknowledgePayment: (id, paymentType) => request(`/assignments/${id}/acknowledge-payment?paymentType=${encodeURIComponent(paymentType || 'CASH')}`, {
            method: 'POST'
        }),

        cancelAssignment: (id, reason) => request(`/assignments/${id}/cancel?reason=${encodeURIComponent(reason)}`, {
            method: 'POST'
        }),

        reportNoShow: (id, notes) => request(`/assignments/${id}/no-show?notes=${encodeURIComponent(notes)}`, {
            method: 'POST'
        }),

        // Review & Rating Endpoints
        getPendingRatings: () => request('/reviews/pending', {
            method: 'GET'
        }),

        submitReview: (reviewData) => request('/reviews', {
            method: 'POST',
            body: typeof reviewData === 'string' ? reviewData : JSON.stringify(reviewData)
        }),

        submitRating: (reviewData) => request('/reviews', {
            method: 'POST',
            body: typeof reviewData === 'string' ? reviewData : JSON.stringify(reviewData)
        }),

        getUserReviews: (userId) => request(`/reviews/user/${userId}`, {
            method: 'GET'
        }),

        getAllJobs: () => request('/jobs/all', {
            method: 'GET'
        }),

        // Admin Endpoints
        getAdminKpis: () => request('/admin/health/kpis', {
            method: 'GET'
        }),

        getAdminUsers: () => request('/admin/users', {
            method: 'GET'
        }),

        getPendingUsers: () => request('/admin/users/pending', {
            method: 'GET'
        }),

        approveUser: (userId) => request(`/admin/users/${userId}/approve`, {
            method: 'PATCH'
        }),

        rejectUser: (userId, reason) => request(`/admin/users/${userId}/reject?reason=${encodeURIComponent(reason || 'Admin rejected')}`, {
            method: 'PATCH'
        }),

        suspendUser: (userId, reason) => request(`/admin/users/${userId}/suspend?reason=${encodeURIComponent(reason || 'Policy violation')}`, {
            method: 'PATCH'
        }),

        banUser: (userId, reason) => request(`/admin/users/${userId}/ban?reason=${encodeURIComponent(reason || 'Severe violation')}`, {
            method: 'PATCH'
        }),

        updateUserTrust: (userId, trustStatus, reason) => request(`/admin/users/${userId}/trust`, {
            method: 'PATCH',
            body: JSON.stringify({ trustStatus, reason })
        }),

        toggleUserVerification: (userId, verified) => request(`/admin/users/${userId}/verify?verified=${verified}`, {
            method: 'PATCH'
        }),

        getAdminReports: (status) => {
            const query = status && status !== 'all' ? `?status=${encodeURIComponent(status)}` : '';
            return request(`/admin/reports${query}`, { method: 'GET' });
        },

        resolveReport: (reportId, status, resolutionNotes, targetAction) => request(`/admin/reports/${reportId}/resolve`, {
            method: 'POST',
            body: JSON.stringify({ status, resolutionNotes, targetAction: targetAction || 'NONE' })
        }),

        moderateJob: (jobId, reason) => request(`/admin/jobs/${jobId}?reason=${encodeURIComponent(reason || 'Terms violation')}`, {
            method: 'DELETE'
        }),

        getAdminAuditLogs: () => request('/admin/audit-logs', {
            method: 'GET'
        }),

        // ----------------------------------------------------
        // Hierarchical Location APIs (Country -> State -> District -> Taluka -> Village)
        // ----------------------------------------------------
        getCountries: async () => {
            try {
                const res = await request('/locations/countries', { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                return window.locationMasterData?.countries || [];
            } catch (e) {
                return window.locationMasterData?.countries || [];
            }
        },

        getStates: async (countryId = 'IN') => {
            try {
                const res = await request(`/locations/states?countryId=${encodeURIComponent(countryId)}`, { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                const master = window.locationMasterData?.states || [];
                return master.filter(s => s.countryId === countryId || s.countryId === 'IN');
            } catch (e) {
                const master = window.locationMasterData?.states || [];
                return master.filter(s => s.countryId === countryId || s.countryId === 'IN');
            }
        },

        getDistricts: async (stateId = 'state-mh') => {
            try {
                const res = await request(`/locations/districts?stateId=${encodeURIComponent(stateId)}`, { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                const master = window.locationMasterData?.districts || [];
                return master.filter(d => d.stateId === stateId);
            } catch (e) {
                const master = window.locationMasterData?.districts || [];
                return master.filter(d => d.stateId === stateId);
            }
        },

        getSubDistricts: async (districtId = 'dist-pune') => {
            try {
                const res = await request(`/locations/sub-districts?districtId=${encodeURIComponent(districtId)}`, { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                const master = window.locationMasterData?.subDistricts || window.locationMasterData?.talukas || [];
                return master.filter(t => t.districtId === districtId);
            } catch (e) {
                const master = window.locationMasterData?.subDistricts || window.locationMasterData?.talukas || [];
                return master.filter(t => t.districtId === districtId);
            }
        },

        getTalukas: async (districtId = 'dist-pune') => {
            try {
                const res = await request(`/locations/talukas?districtId=${encodeURIComponent(districtId)}`, { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                const master = window.locationMasterData?.talukas || window.locationMasterData?.subDistricts || [];
                return master.filter(t => t.districtId === districtId);
            } catch (e) {
                const master = window.locationMasterData?.talukas || window.locationMasterData?.subDistricts || [];
                return master.filter(t => t.districtId === districtId);
            }
        },

        getVillages: async (subDistrictOrTalukaId = 'subdist-haveli', searchQuery = '') => {
            if (!subDistrictOrTalukaId) return [];
            try {
                const queryStr = searchQuery ? `&search=${encodeURIComponent(searchQuery)}` : '';
                const res = await request(`/locations/villages?subDistrictId=${encodeURIComponent(subDistrictOrTalukaId)}${queryStr}`, { method: 'GET' });
                if (Array.isArray(res) && res.length) return res;
                if (res && Array.isArray(res.data) && res.data.length) return res.data;
                const master = window.locationMasterData?.villages || [];
                let filtered = master.filter(v => (v.subDistrictId === subDistrictOrTalukaId || v.talukaId === subDistrictOrTalukaId));
                if (searchQuery) {
                    const q = searchQuery.toLowerCase();
                    filtered = filtered.filter(v => 
                        (v.name && v.name.toLowerCase().includes(q)) || 
                        (v.nameEn && v.nameEn.toLowerCase().includes(q)) || 
                        (v.nameMr && v.nameMr.toLowerCase().includes(q)) || 
                        (v.nameHi && v.nameHi.toLowerCase().includes(q))
                    );
                }
                return filtered;
            } catch (e) {
                const master = window.locationMasterData?.villages || [];
                let filtered = master.filter(v => (v.subDistrictId === subDistrictOrTalukaId || v.talukaId === subDistrictOrTalukaId));
                if (searchQuery) {
                    const q = searchQuery.toLowerCase();
                    filtered = filtered.filter(v => 
                        (v.name && v.name.toLowerCase().includes(q)) || 
                        (v.nameEn && v.nameEn.toLowerCase().includes(q)) || 
                        (v.nameMr && v.nameMr.toLowerCase().includes(q)) || 
                        (v.nameHi && v.nameHi.toLowerCase().includes(q))
                    );
                }
                return filtered;
            }
        },

        validateLocationHierarchy: (locationObj) => request('/locations/validate', {
            method: 'POST',
            body: JSON.stringify(locationObj)
        }),

        // -------------------------------------------------------------
        // Dedicated Messaging APIs (Admin <-> User Bidirectional Engine)
        // -------------------------------------------------------------
        getMyConversation: () => request('/messages/my-conversation', { method: 'GET' }),
        
        sendChatMessage: (receiverId, messageText) => request('/messages/send', {
            method: 'POST',
            body: JSON.stringify({
                receiverId: (receiverId && receiverId.length > 20) ? receiverId : null,
                messageText: messageText
            })
        }),

        getAdminConversations: (search, role, unreadOnly) => {
            const params = new URLSearchParams();
            if (search) params.append('search', search);
            if (role && role !== 'ALL') params.append('role', role);
            if (unreadOnly) params.append('unreadOnly', 'true');
            const qs = params.toString();
            return request(`/messages/admin/conversations${qs ? '?' + qs : ''}`, { method: 'GET' });
        },

        getAdminConversationForUser: (userId) => request(`/messages/admin/conversations/${encodeURIComponent(userId)}`, { method: 'GET' }),

        getUnreadMessageCount: () => request('/messages/unread-count', { method: 'GET' }),

        clearConversation: (targetId) => {
            if (targetId) {
                return request(`/messages/clear/${encodeURIComponent(targetId)}`, { method: 'DELETE' });
            }
            return request('/messages/clear', { method: 'DELETE' });
        }
    };
})();

if (typeof module !== 'undefined' && module.exports) {
    module.exports = ApiClient;
}

