/**
 * 🌾 KaamSetu (कामसेतू) - Authentication & Session State Manager
 * Handles Username + Password Authentication, Registration, Mobile OTP, Email Verification, Admin Approval Status, JWT Lifecycle, and Session Persistence
 */

const AuthManager = (function () {
    const STORAGE_KEY_TOKEN = 'kaamsetu_jwt_token';
    const STORAGE_KEY_USER = 'kaamsetu_user_profile';
    const STORAGE_KEY_ROLE = 'kaamsetu_active_role';
    const STORAGE_KEY_LANG = 'kaamsetu_lang';

    let state = {
        token: null,
        user: null,
        activeRole: 'WORKER',
        language: 'mr',
        isAuthenticated: false
    };

    // Initialize session state from localStorage
    function init() {
        const storage = window.SafeStorage || {
            getItem: (k) => { try { return localStorage.getItem(k); } catch (e) { return null; } },
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} },
            removeItem: (k) => { try { localStorage.removeItem(k); } catch (e) {} }
        };

        state.token = storage.getItem(STORAGE_KEY_TOKEN);
        const storedUser = storage.getItem(STORAGE_KEY_USER);
        state.user = storedUser ? JSON.parse(storedUser) : null;
        state.activeRole = storage.getItem(STORAGE_KEY_ROLE) || 'WORKER';
        state.language = storage.getItem(STORAGE_KEY_LANG) || 'mr';
        state.isAuthenticated = Boolean(state.token && state.user);

        // Sync with i18n
        if (typeof i18n !== 'undefined' && i18n.setLanguage) {
            i18n.setLanguage(state.language);
        }

        console.log(`🔐 [AuthManager] Initialized. Logged in: ${state.isAuthenticated} | Role: ${state.activeRole}`);
        return state;
    }

    // Register new user (Account created in PENDING status awaiting Admin approval)
    async function register(registerData) {
        if (!registerData.username || registerData.username.trim().length < 3) {
            throw new Error('Username must be at least 3 characters');
        }
        if (!registerData.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerData.email)) {
            throw new Error('Valid email address is required');
        }
        if (!registerData.password || registerData.password.length < 6) {
            throw new Error('Password must be at least 6 characters');
        }
        if (registerData.password !== registerData.confirmPassword) {
            throw new Error('Password and confirm password do not match');
        }
        if (!registerData.village || !registerData.village.trim()) {
            throw new Error('Village is required');
        }

        // Assign default location IDs if omitted
        registerData.countryId = registerData.countryId || 'IN';
        registerData.stateId = registerData.stateId || 'state-mh';
        registerData.districtId = registerData.districtId || 'dist-pune';
        registerData.talukaId = registerData.talukaId || 'tal-shirur';
        registerData.villageId = registerData.villageId || 'vil-ranjangaon';

        return await ApiClient.register(registerData);
    }

    // Login with Username / Mobile + Password (Strictly Backend Authenticated via Spring Boot)
    async function loginWithPassword(usernameOrMobile, password) {
        if (!usernameOrMobile || !usernameOrMobile.trim()) {
            throw new Error('Username or mobile number is required');
        }
        if (!password) {
            throw new Error('Password is required');
        }

        let authData = null;
        try {
            authData = await ApiClient.loginWithPassword(usernameOrMobile.trim(), password);
        } catch (apiErr) {
            if (apiErr.isNetworkError || apiErr.status === 503 || (apiErr.message && (apiErr.message.includes('unavailable') || apiErr.message.includes('Failed to fetch') || apiErr.message.includes('timed out')))) {
                const connErr = new Error("Unable to connect to KaamSetu server. Please try again when the server is available.");
                connErr.isNetworkError = true;
                connErr.status = 503;
                throw connErr;
            }
            throw apiErr;
        }

        if (!authData || !authData.accessToken || !authData.user) {
            throw new Error('Invalid response received from authentication server.');
        }

        // Verify account is approved
        if (authData.user && authData.user.status === 'PENDING') {
            throw new Error('Your account is waiting for administrator approval.');
        }

        state.token = authData.accessToken;
        state.user = authData.user;
        state.activeRole = authData.user.activeRole || authData.user.role || 'WORKER';
        state.language = authData.user.languagePreference || 'mr';
        state.isAuthenticated = true;

        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };

        storage.setItem(STORAGE_KEY_TOKEN, state.token);
        storage.setItem(STORAGE_KEY_ROLE, state.activeRole);
        storage.setItem(STORAGE_KEY_LANG, state.language);
        storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));

        if (window.appState && window.appState.data) {
            window.appState.data.currentUser = state.user;
            window.appState.data.activeRole = state.activeRole;
            window.appState.currentRole = state.activeRole;
            window.appState.notify();
        }

        dispatchAuthEvent('auth:login', state);
        return authData;
    }

    // Verify email with token
    async function verifyEmail(token) {
        if (!token || !token.trim()) {
            throw new Error('Verification token cannot be empty');
        }
        return await ApiClient.verifyEmail(token.trim());
    }

    // Resend email verification link
    async function resendEmailVerification(email) {
        if (!email || !email.trim()) {
            throw new Error('Email address is required');
        }
        return await ApiClient.resendEmailVerification(email.trim());
    }

    // Change password
    async function changePassword(currentPassword, newPassword, confirmNewPassword) {
        return await ApiClient.changePassword(currentPassword, newPassword, confirmNewPassword);
    }

    // Mobile OTP
    async function sendOtp(mobile) {
        if (!mobile || !/^\+?[0-9]{10,15}$/.test(mobile.replace(/\s+/g, ''))) {
            throw new Error('Invalid mobile number format');
        }
        return await ApiClient.sendOtp(mobile);
    }

    async function verifyOtp(mobile, otp, preferredRole = 'WORKER', languagePreference = 'mr') {
        const authData = await ApiClient.verifyOtp(mobile, otp, preferredRole, languagePreference);

        state.token = authData.accessToken;
        state.user = authData.user;
        state.activeRole = authData.user.role || preferredRole;
        state.language = authData.user.languagePreference || languagePreference;
        state.isAuthenticated = true;

        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };

        storage.setItem(STORAGE_KEY_TOKEN, state.token);
        storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));
        storage.setItem(STORAGE_KEY_ROLE, state.activeRole);
        storage.setItem(STORAGE_KEY_LANG, state.language);

        if (typeof i18n !== 'undefined' && i18n.setLanguage) {
            i18n.setLanguage(state.language);
        }

        dispatchAuthEvent('auth:login', state);
        return authData;
    }

    // Email OTP
    async function sendEmailOtp(email) {
        if (!email || !email.includes('@')) {
            throw new Error('Invalid email address format');
        }
        return await ApiClient.sendEmailOtp(email);
    }

    async function verifyEmailOtp(email, otp) {
        if (!otp || otp.trim().length !== 6) {
            throw new Error('OTP must be exactly 6 digits');
        }
        return await ApiClient.verifyEmailOtp(email, otp.trim());
    }

    // Logout and clear credentials
    function logout() {
        clearSession();
        dispatchAuthEvent('auth:logout', null);
    }

    function clearSession() {
        state.token = null;
        state.user = null;
        state.isAuthenticated = false;
        const storage = window.SafeStorage || {
            removeItem: (k) => { try { localStorage.removeItem(k); } catch (e) {} }
        };
        storage.removeItem(STORAGE_KEY_TOKEN);
        storage.removeItem(STORAGE_KEY_USER);
    }

    // Switch view role (WORKER <-> PROVIDER <-> ADMIN)
    async function switchRole(newRole) {
        const norm = (newRole || 'WORKER').toUpperCase();
        state.activeRole = norm;
        if (state.user) {
            state.user.activeRole = norm;
        }
        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };
        storage.setItem(STORAGE_KEY_ROLE, norm);
        if (state.user) {
            storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));
        }

        // Inform backend if authenticated
        if (state.token && typeof ApiClient !== 'undefined' && ApiClient.switchActiveRole) {
            try {
                const updatedProfile = await ApiClient.switchActiveRole(norm);
                if (updatedProfile) {
                    state.user = Object.assign({}, state.user, updatedProfile);
                    storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));
                }
            } catch (e) {
                console.info('Backend role switch note:', e.message);
            }
        }

        dispatchAuthEvent('auth:roleChange', { role: norm });
        if (window.appState && typeof window.appState.notify === 'function') {
            window.appState.data.activeRole = norm;
            window.appState.notify();
        }
    }

    // Activate Worker profile on existing account (Strictly Backend Authenticated)
    async function activateWorkerProfile(profileData = {}) {
        if (!state.user) throw new Error('User must be logged in to activate profile');
        if (!state.token || typeof ApiClient === 'undefined' || !ApiClient.activateWorkerProfile) {
            throw new Error('Server connection required to activate worker profile');
        }

        const updatedProfile = await ApiClient.activateWorkerProfile(profileData);
        if (!updatedProfile) {
            throw new Error('Failed to activate worker profile on backend server');
        }

        state.user = Object.assign({}, state.user, updatedProfile, { hasWorkerProfile: true });
        state.activeRole = 'WORKER';
        state.user.activeRole = 'WORKER';

        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };
        storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));
        storage.setItem(STORAGE_KEY_ROLE, 'WORKER');

        // Sync to appState
        if (window.appState && window.appState.data) {
            window.appState.data.currentUser = state.user;
            if (window.appState.syncAllWorkersFromRegistry) {
                window.appState.syncAllWorkersFromRegistry();
            }
            window.appState.notify();
        }

        dispatchAuthEvent('auth:profileActivated', { role: 'WORKER', user: state.user });
        return state.user;
    }

    // Activate Provider profile on existing account (Strictly Backend Authenticated)
    async function activateProviderProfile(profileData = {}) {
        if (!state.user) throw new Error('User must be logged in to activate profile');
        if (!state.token || typeof ApiClient === 'undefined' || !ApiClient.activateProviderProfile) {
            throw new Error('Server connection required to activate provider profile');
        }

        const updatedProfile = await ApiClient.activateProviderProfile(profileData);
        if (!updatedProfile) {
            throw new Error('Failed to activate provider profile on backend server');
        }

        state.user = Object.assign({}, state.user, updatedProfile, { hasProviderProfile: true });
        state.activeRole = 'PROVIDER';
        state.user.activeRole = 'PROVIDER';

        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };
        storage.setItem(STORAGE_KEY_USER, JSON.stringify(state.user));
        storage.setItem(STORAGE_KEY_ROLE, 'PROVIDER');

        // Sync to appState
        if (window.appState && window.appState.data) {
            window.appState.data.currentUser = state.user;
            if (window.appState.syncAllProvidersFromRegistry) {
                window.appState.syncAllProvidersFromRegistry();
            }
            window.appState.notify();
        }

        dispatchAuthEvent('auth:profileActivated', { role: 'PROVIDER', user: state.user });
        return state.user;
    }

    // Check uniqueness (Frontend real-time validator)
    async function checkUnique(type, value) {
        if (!type || !value || !value.trim()) {
            return { available: true, message: 'Available' };
        }

        // Try backend check
        if (typeof ApiClient !== 'undefined' && ApiClient.checkUnique) {
            try {
                const res = await ApiClient.checkUnique(type, value.trim());
                if (res) return res;
            } catch (e) {
                console.info('Backend uniqueness check offline, falling back to local registry:', e.message);
            }
        }

        // Local DB check fallback
        const db = (window.SafeStorage ? window.SafeStorage.getJSON('kaamsetu_users_db', {}) : JSON.parse(localStorage.getItem('kaamsetu_users_db') || '{}'));
        const cleanVal = value.trim().toLowerCase();
        const field = type.trim().toLowerCase();

        if (field === 'username') {
            for (const k in db) {
                const u = db[k];
                if (u && u.username && u.username.toLowerCase() === cleanVal) {
                    return { available: false, field: 'username', message: 'Username already taken.' };
                }
            }
        } else if (field === 'email') {
            for (const k in db) {
                const u = db[k];
                if (u && u.email && u.email.toLowerCase() === cleanVal) {
                    return { available: false, field: 'email', message: 'Email already registered.' };
                }
            }
        } else if (field === 'mobile' || field === 'phone') {
            const rawDigits = value.replace(/\D/g, '');
            const tenDigits = rawDigits.length >= 10 ? rawDigits.slice(-10) : rawDigits;
            for (const k in db) {
                const u = db[k];
                if (u && u.mobile) {
                    const uDigits = u.mobile.replace(/\D/g, '');
                    if (uDigits.endsWith(tenDigits)) {
                        return { available: false, field: 'mobile', message: 'Mobile number already registered.' };
                    }
                }
            }
        }

        return { available: true, field: type, message: 'Available' };
    }

    // Set language preference
    function setLanguage(lang) {
        state.language = lang;
        const storage = window.SafeStorage || {
            setItem: (k, v) => { try { localStorage.setItem(k, typeof v === 'string' ? v : JSON.stringify(v)); } catch (e) {} }
        };
        storage.setItem(STORAGE_KEY_LANG, lang);
        if (typeof i18n !== 'undefined' && i18n.setLanguage) {
            i18n.setLanguage(lang);
        }
    }

    function dispatchAuthEvent(eventName, detail) {
        if (typeof window !== 'undefined') {
            window.dispatchEvent(new CustomEvent(eventName, { detail }));
        }
    }

    return {
        init,
        register,
        loginWithPassword,
        verifyEmail,
        resendEmailVerification,
        changePassword,
        sendOtp,
        verifyOtp,
        sendEmailOtp,
        verifyEmailOtp,
        logout,
        clearSession,
        switchRole,
        activateWorkerProfile,
        activateProviderProfile,
        checkUnique,
        setLanguage,
        getToken: () => state.token,
        getCurrentUser: () => state.user,
        getActiveRole: () => state.activeRole,
        getLanguage: () => state.language,
        isAuthenticated: () => state.isAuthenticated,
        hasRole: (role) => state.user && (state.user.role === role || state.activeRole === role),
        hasWorkerProfile: () => Boolean(state.user && (state.user.hasWorkerProfile || state.user.role === 'WORKER')),
        hasProviderProfile: () => Boolean(state.user && (state.user.hasProviderProfile || state.user.role === 'PROVIDER'))
    };
})();

// Auto-initialize on script load
if (typeof window !== 'undefined') {
    AuthManager.init();
}

if (typeof module !== 'undefined' && module.exports) {
    module.exports = AuthManager;
}
