/**
 * KaamSetu Reactive State Store & Lifecycle Controller
 */

function parseSkillsSafely(raw) {
  if (!raw) return ["cat.agriculture", "cat.construction"];
  let curr = raw;
  for (let i = 0; i < 5; i++) {
    if (typeof curr !== 'string') break;
    curr = curr.trim();
    if ((curr.startsWith('[') && curr.endsWith(']')) || (curr.startsWith('"') && curr.endsWith('"'))) {
      try { curr = JSON.parse(curr); } catch (e) { break; }
    } else {
      break;
    }
  }
  if (Array.isArray(curr)) {
    const list = [];
    for (let item of curr) {
      if (typeof item === 'string') {
        let clean = item.trim();
        for (let j = 0; j < 4; j++) {
          if (clean.startsWith('"') && clean.endsWith('"') && clean.length > 2) {
            try { clean = JSON.parse(clean); } catch (e) { break; }
          } else if (clean.startsWith('[') && clean.endsWith(']')) {
            try {
              const inner = JSON.parse(clean);
              if (Array.isArray(inner)) {
                list.push(...inner);
                clean = null;
                break;
              }
            } catch (e) { break; }
          } else {
            break;
          }
        }
        if (clean) list.push(clean);
      } else if (item) {
        list.push(item);
      }
    }
    const sanitized = list.map(s => String(s).replace(/^[\["'\\]+|[\]"'\\]+$/g, '').trim()).filter(Boolean);
    return sanitized.length > 0 ? sanitized : ["cat.agriculture", "cat.construction"];
  }
  if (typeof curr === 'string' && curr.length > 0) {
    const sanitized = curr.split(',').map(s => s.replace(/^[\["'\\]+|[\]"'\\]+$/g, '').trim()).filter(Boolean);
    return sanitized.length > 0 ? sanitized : ["cat.agriculture", "cat.construction"];
  }
  return ["cat.agriculture", "cat.construction"];
}

/**
 * SafeStorage helper fallback in state.js
 */
const SafeStorage = window.SafeStorage || (function() {
  const memFallback = {};

  function compactState(state) {
    if (!state || typeof state !== 'object') return state;
    const clean = {};
    for (const key in state) {
      if (Object.prototype.hasOwnProperty.call(state, key)) {
        const val = state[key];
        if (Array.isArray(val)) {
          if (key === 'auditLogs') clean[key] = val.slice(0, 30);
          else if (key === 'notifications') clean[key] = val.slice(0, 30);
          else if (key === 'adminMessages') clean[key] = val.slice(0, 30);
          else if (key === 'adminConversations') clean[key] = val.slice(0, 30);
          else if (key === 'moderationReports') clean[key] = val.slice(0, 30);
          else if (key === 'pendingUsers') clean[key] = val.slice(0, 40);
          else if (key === 'assignments') clean[key] = val.slice(0, 40);
          else if (key === 'jobs') clean[key] = val.slice(0, 50);
          else if (key === 'workers') clean[key] = val.slice(0, 60);
          else if (key === 'providers') clean[key] = val.slice(0, 40);
          else if (key === 'reviews') clean[key] = val.slice(0, 40);
          else clean[key] = val.slice(0, 50);
        } else {
          clean[key] = val;
        }
      }
    }
    return clean;
  }

  function recoverQuota(targetKey, targetVal) {
    console.warn(`[SafeStorage] Quota limit encountered on "${targetKey}". Performing cleanup...`);
    try {
      const purgeKeys = ['kaamsetu_temp', 'kaamsetu_cache', 'kaamsetu_audit_logs', 'kaamsetu_pilot_seed_applied'];
      purgeKeys.forEach(k => {
        try { localStorage.removeItem(k); } catch (e) {}
      });

      try {
        const usersDbStr = localStorage.getItem('kaamsetu_users_db');
        if (usersDbStr && usersDbStr.length > 50000) {
          const db = JSON.parse(usersDbStr);
          const keys = Object.keys(db);
          if (keys.length > 15) {
            const pruned = {};
            keys.slice(-15).forEach(k => { pruned[k] = db[k]; });
            localStorage.setItem('kaamsetu_users_db', JSON.stringify(pruned));
          }
        }
      } catch (e) {}

      if (targetKey === 'kaamsetu_state') {
        try {
          const stateObj = (typeof targetVal === 'string') ? JSON.parse(targetVal) : targetVal;
          const compact = compactState(stateObj);
          localStorage.setItem(targetKey, JSON.stringify(compact));
          return true;
        } catch (e) {}
      }

      localStorage.setItem(targetKey, typeof targetVal === 'string' ? targetVal : JSON.stringify(targetVal));
      return true;
    } catch (err2) {
      console.warn(`[SafeStorage] Secondary save for "${targetKey}" failed. Falling back to session/memory store.`, err2);
      try {
        sessionStorage.setItem(targetKey, typeof targetVal === 'string' ? targetVal : JSON.stringify(targetVal));
      } catch (e3) {
        memFallback[targetKey] = targetVal;
      }
      return false;
    }
  }

  return {
    compactState,
    getItem(key, fallback = null) {
      try {
        const val = localStorage.getItem(key);
        if (val !== null) return val;
      } catch (e) {}
      try {
        const sVal = sessionStorage.getItem(key);
        if (sVal !== null) return sVal;
      } catch (e) {}
      return (key in memFallback) ? memFallback[key] : fallback;
    },
    getJSON(key, fallback = null) {
      try {
        const raw = this.getItem(key, null);
        return raw ? JSON.parse(raw) : fallback;
      } catch (e) {
        return fallback;
      }
    },
    setItem(key, value) {
      const strVal = typeof value === 'string' ? value : JSON.stringify(value);
      memFallback[key] = strVal;
      try {
        localStorage.setItem(key, strVal);
        return true;
      } catch (err) {
        return recoverQuota(key, strVal);
      }
    },
    removeItem(key) {
      delete memFallback[key];
      try { localStorage.removeItem(key); } catch (e) {}
      try { sessionStorage.removeItem(key); } catch (e) {}
    }
  };
})();

if (typeof window !== 'undefined') {
  window.SafeStorage = SafeStorage;
}

class AppState {
  constructor() {
    let savedState = null;
    try {
      const raw = SafeStorage.getItem("kaamsetu_state");
      savedState = raw ? JSON.parse(raw) : null;
    } catch (e) {
      savedState = null;
    }

    const initData = (typeof window !== "undefined" && window.initialData) ? window.initialData : {};
    this.data = Object.assign({}, initData, savedState || {});

    // Robust Persistence: Restore Jobs from dedicated registry or savedState
    let loadedJobs = SafeStorage.getJSON("kaamsetu_jobs_registry", null);
    if (!Array.isArray(loadedJobs) || loadedJobs.length === 0) {
      if (savedState && Array.isArray(savedState.jobs) && savedState.jobs.length > 0) {
        loadedJobs = savedState.jobs;
      }
    }
    if (!Array.isArray(loadedJobs)) {
      loadedJobs = (initData.jobs && Array.isArray(initData.jobs)) ? [...initData.jobs] : [];
    }
    this.data.jobs = loadedJobs;

    // Restore Assignments from dedicated registry or savedState
    let loadedAssignments = SafeStorage.getJSON("kaamsetu_assignments_registry", null);
    if (!Array.isArray(loadedAssignments) || loadedAssignments.length === 0) {
      if (savedState && Array.isArray(savedState.assignments) && savedState.assignments.length > 0) {
        loadedAssignments = savedState.assignments;
      }
    }
    if (!Array.isArray(loadedAssignments)) {
      loadedAssignments = (initData.assignments && Array.isArray(initData.assignments)) ? [...initData.assignments] : [];
    }
    this.data.assignments = loadedAssignments;

    // Restore Workers & Providers
    if (!Array.isArray(this.data.workers)) {
      this.data.workers = (initData.workers && Array.isArray(initData.workers)) ? [...initData.workers] : [];
    }
    if (!Array.isArray(this.data.providers)) {
      this.data.providers = (initData.providers && Array.isArray(initData.providers)) ? [...initData.providers] : [];
    }
    if (!Array.isArray(this.data.notifications)) {
      this.data.notifications = (initData.notifications && Array.isArray(initData.notifications)) ? [...initData.notifications] : [];
    }

    if (!Array.isArray(this.data.pendingUsers) || (savedState === null && this.data.pendingUsers.length === 0)) {
      this.data.pendingUsers = [
        {
          id: "u_pend_1",
          username: "kailas_ghadge",
          fullName: "कैलास घाडगे (Kailas Ghadge)",
          mobile: "+91 98225 67890",
          email: "kailas.ghadge@kaamsetu.org",
          role: "WORKER",
          gender: "MALE",
          avatar: "👷‍♂️",
          village: "रांजणगाव (Ranjangaon)",
          taluka: "Shirur",
          district: "Pune Rural",
          state: "Maharashtra",
          country: "India",
          minDailyWage: 650,
          minWage: 650,
          travelRadiusKm: 15,
          experienceYears: 4,
          rating: 5.0,
          trustStatus: "PENDING",
          trust: "HEALTHY",
          skills: ["cat.agriculture", "cat.driving"],
          bio: "अनुभवी ट्रॅक्टर चालक व शेती कामाचे कारागीर. वेळेवर हजर राहण्याची खात्री.",
          mobileVerified: true,
          emailVerified: true,
          status: "PENDING",
          registrationDate: "आज सकाळी 09:15 AM"
        },
        {
          id: "u_pend_2",
          username: "dnyaneshwar_agro",
          fullName: "ज्ञानेश्वर थोरात (Dnyaneshwar Thorat)",
          mobile: "+91 94231 98765",
          email: "thorat.agro@kaamsetu.org",
          role: "PROVIDER",
          providerType: "FARMER",
          type: "provider.type.farmer",
          gender: "MALE",
          avatar: "👨‍🌾",
          businessName: "थोरात ॲग्रो फार्म्स (Thorat Agro)",
          village: "सासवड (Saswad)",
          taluka: "Saswad",
          district: "Pune Rural",
          state: "Maharashtra",
          country: "India",
          rating: 5.0,
          paymentReliability: 5.0,
          trustStatus: "PENDING",
          trust: "HEALTHY",
          facilities: ["पिण्याचे स्वच्छ पाणी", "दुपारचा चहा व सावली", "वेळेवर दैनिक मोबदला", "सुरक्षित कार्यस्थळ"],
          bio: "सासवड परिसरातील प्रगतिशील शेतकरी. भाजीपाला व कांदा उत्पादक. कामगारांना वेळेवर रोख मोबदला दिला जातो.",
          mobileVerified: true,
          emailVerified: true,
          status: "PENDING",
          registrationDate: "आज सकाळी 10:30 AM"
        }
      ];
    }
    if (!Array.isArray(this.data.auditLogs)) {
      this.data.auditLogs = (initData.auditLogs ? [...initData.auditLogs] : []);
    }
    if (!Array.isArray(this.data.notifications)) {
      this.data.notifications = (initData.notifications ? [...initData.notifications] : []);
    }
    if (!Array.isArray(this.data.assignments)) {
      this.data.assignments = (initData.assignments ? [...initData.assignments] : []);
    }
    const savedConvs = SafeStorage.getJSON("kaamsetu_conversations_registry");
    if (Array.isArray(savedConvs) && savedConvs.length > 0) {
      // Remove any duplicate legacy admin conversations from loaded array
      const seenAdmin = new Set();
      this.data.conversations = savedConvs.filter(c => {
        const isAdmin = c.participantId === 'admin_sys' || (c.id && c.id.startsWith('conv_admin'));
        if (isAdmin) {
          const ownerKey = String(c.ownerUsername || c.ownerId || 'sys').toLowerCase();
          if (seenAdmin.has(ownerKey)) return false;
          seenAdmin.add(ownerKey);
        }
        return true;
      });
    } else if (Array.isArray(savedState.conversations) && savedState.conversations.length > 0) {
      this.data.conversations = savedState.conversations;
    } else {
      this.data.conversations = [
        {
          id: "conv_admin_official",
          participantId: "admin_sys",
          participantName: "🛡️ प्रशासन (KaamSetu Admin)",
          avatar: "🛡️",
          jobTitle: "अधिकृत प्रशासकीय व मदत कक्ष (Official Admin Channel)",
          lastMessage: "कामसेतू अधिकृत मदत कक्षामध्ये आपले स्वागत आहे. येथे थेट संपर्क करू शकता.",
          lastMessageTime: "नेहमी उपलब्ध",
          unreadCount: 0,
          messages: [
            {
              id: "m_welcome_admin",
              sender: "THEM",
              text: "नमस्कार! कामसेतू अधिकृत मदत व प्रशासन कक्षामध्ये आपले स्वागत आहे. काही अडचण, पडताळणी किंवा प्रश्न असल्यास येथे थेट संदेश पाठवू शकता. आमची टीम आपल्याला मदत करेल.",
              time: "मदत कक्ष"
            }
          ]
        }
      ];
    }

    if (!Array.isArray(this.data.adminConversations) || this.data.adminConversations.length === 0) {
      this.data.adminConversations = [
        {
          id: "admin_thread_suresh",
          userId: "u_suresh",
          userName: "सुरेश जाधव (Suresh Jadhav)",
          userRole: "WORKER",
          userStatus: "PENDING",
          avatar: "👷",
          lastMessage: "माझे आधार कार्ड पडताळणी प्रलंबित आहे, कृपया तपासा.",
          lastMessageTime: "10:15 AM",
          unread: true,
          messages: [
            { id: "m_s1", sender: "USER", text: "नमस्कार ॲडमिन, माझे आधार कार्ड व खाते पडताळणी प्रलंबित आहे.", time: "10:15 AM" }
          ]
        },
        {
          id: "admin_thread_mahesh",
          userId: "u_mahesh",
          userName: "महेश पाटील (Mahesh Patil)",
          userRole: "PROVIDER",
          userStatus: "ACTIVE",
          avatar: "👤",
          lastMessage: "कांदा लागवड कामासाठी १० कामगारांची गरज आहे.",
          lastMessageTime: "काल",
          unread: false,
          messages: [
            { id: "m_m1", sender: "USER", text: "कांदा लागवड कामासाठी १० कामगारांची गरज आहे.", time: "काल" },
            { id: "m_m2", sender: "ADMIN", text: "आपली कामाची नोंदणी मंजूर केली आहे. परिसरातील कामगारांना सूचना पाठवली आहे.", time: "काल" }
          ]
        },
        {
          id: "admin_thread_ganesh",
          userId: "u_ganesh",
          userName: "गणेश शिंदे (Ganesh Shinde)",
          userRole: "WORKER",
          userStatus: "ACTIVE",
          avatar: "👷",
          lastMessage: "कुशल ट्रॅक्टर ड्रायव्हर कौशल्य अपडेट केले आहे.",
          lastMessageTime: "28 ऑगस्ट",
          unread: false,
          messages: [
            { id: "m_g1", sender: "USER", text: "मी माझे ट्रॅक्टर ड्रायव्हिंग कौशल्य व लायसन्स जोडले आहे.", time: "28 ऑगस्ट" },
            { id: "m_g2", sender: "ADMIN", text: "तपशील अद्यतनित केले आहेत. धन्यवाद!", time: "28 ऑगस्ट" }
          ]
        },
        {
          id: "admin_thread_sunita",
          userId: "u_sunita",
          userName: "सुनिता मोरे (Sunita More)",
          userRole: "PROVIDER",
          userStatus: "PENDING",
          avatar: "👤",
          lastMessage: "नवीन शेतकरी नोंदणी केली आहे.",
          lastMessageTime: "27 ऑगस्ट",
          unread: true,
          messages: [
            { id: "m_su1", sender: "USER", text: "नमस्कार, माझी शेतकरी खाते नोंदणी मंजूर करा.", time: "27 ऑगस्ट" }
          ]
        }
      ];
    }
    
    // Purge any admin self-threads from adminConversations
    if (Array.isArray(this.data.adminConversations)) {
      this.data.adminConversations = this.data.adminConversations.filter(c => 
        c.userRole !== 'ADMIN' && 
        c.userId !== 'admin' &&
        c.id !== 'admin_thread_admin' &&
        !(c.userName && (c.userName.includes("Super Admin") || c.userName.includes("Rajdip Bankar")))
      );
    }

    // Synchronize authenticated user profile from AuthManager / localStorage
    const storedUserJson = SafeStorage.getItem("kaamsetu_user_profile");
    let storedUser = null;
    try {
      storedUser = storedUserJson ? JSON.parse(storedUserJson) : null;
    } catch (e) {
      storedUser = null;
    }

    const hasToken = SafeStorage.getItem("kaamsetu_jwt_token");

    if (hasToken && storedUser) {
      this.syncUserToState(storedUser);
      this.currentRole = storedUser.role || SafeStorage.getItem("kaamsetu_role") || "WORKER";
      this.activeView = SafeStorage.getItem("kaamsetu_view") || (this.currentRole === "ADMIN" ? "admin" : "home");
    } else {
      this.data.currentUser = null;
      this.currentRole = "WORKER";
      this.activeView = "landing";
    }

    this.activeCategory = "all";
    this.activeTaluka = "All";
    this.workerSearchQuery = "";
    this.selectedRadius = 10;
    this.savedJobs = new Set(JSON.parse(SafeStorage.getItem("kaamsetu_saved_jobs") || "[]"));
    this.savedWorkers = new Set(JSON.parse(SafeStorage.getItem("kaamsetu_saved_workers") || "[]"));
    this.activeNotificationCategory = "all";
    this.listeners = [];

    // Backend Health Gate & Status State
    this.backendStatus = (typeof ApiClient !== "undefined" && ApiClient.getServerStatus) 
        ? ApiClient.getServerStatus() 
        : "BACKEND_CHECKING";
    this._reconnectTimer = null;

    // Global listener for auth state changes & backend status
    if (typeof window !== "undefined") {
      window.addEventListener("auth:login", (event) => {
        if (event.detail && event.detail.user) {
          this.setCurrentUser(event.detail.user);
          if (event.detail.activeRole) {
            this.setRole(event.detail.activeRole);
          }
        }
      });
      window.addEventListener("auth:logout", () => {
        this.setCurrentUser(null);
        this.setView("landing");
      });
      window.addEventListener("backend:status", (event) => {
        if (event.detail && event.detail.status) {
          const newStatus = event.detail.status;
          const oldStatus = this.backendStatus;
          this.backendStatus = newStatus;
          
          if (newStatus === "BACKEND_OFFLINE") {
            this.startAutoReconnect();
          } else if (newStatus === "BACKEND_ONLINE") {
            this.stopAutoReconnect();
          }

          if (oldStatus !== newStatus) {
            this.notify();
          }
        }
      });
    }
    this.syncAllWorkersFromRegistry();
    this.syncAllProvidersFromRegistry();
    this.syncAllPendingUsersFromRegistry();
  }

  setBackendStatus(status) {
    const prev = this.backendStatus;
    this.backendStatus = status;
    if (status === "BACKEND_OFFLINE") {
      this.startAutoReconnect();
    } else if (status === "BACKEND_ONLINE") {
      this.stopAutoReconnect();
    }
    if (prev !== status) {
      this.notify();
    }
  }

  isBackendOnline() {
    return this.backendStatus === "BACKEND_ONLINE";
  }

  startAutoReconnect(intervalMs = 2500) {
    if (this._reconnectTimer) return;
    const checkNow = async () => {
      if (typeof ApiClient !== "undefined" && ApiClient.checkHealth) {
        try {
          const res = await ApiClient.checkHealth(2500);
          if (res && (res.status === "BACKEND_ONLINE" || res.ok)) {
            console.log("🟢 [AppState] Auto-reconnect successful. Server is back ONLINE.");
            this.setBackendStatus("BACKEND_ONLINE");
            this.stopAutoReconnect();
            if (typeof window.showToast === "function") {
              window.showToast("🟢 सर्व्हर कनेक्शन पूर्ववत झाले! (Server Online)");
            }
            if (typeof window.refreshLiveStats === "function") {
              window.refreshLiveStats();
            }
            if (typeof window.renderApp === "function") {
              window.renderApp();
            }
          }
        } catch (e) {
          // Still offline, will retry next interval
        }
      }
    };
    // Run immediately once, then on interval
    checkNow();
    this._reconnectTimer = setInterval(checkNow, intervalMs);
  }

  stopAutoReconnect() {
    if (this._reconnectTimer) {
      clearInterval(this._reconnectTimer);
      this._reconnectTimer = null;
    }
  }

  syncAllPendingUsersFromRegistry() {
    try {
      if (!this.data) this.data = {};
      if (!Array.isArray(this.data.pendingUsers)) this.data.pendingUsers = [];
      const db = SafeStorage.getJSON('kaamsetu_users_db', {});
      const currentList = [];
      const seenKeys = new Set();

      const getKeys = (obj) => {
        if (!obj) return [];
        const keys = [];
        if (obj.id) keys.push(String(obj.id).toLowerCase());
        if (obj.backendId) keys.push(String(obj.backendId).toLowerCase());
        if (obj.username) keys.push(String(obj.username).toLowerCase());
        if (obj.name) keys.push(String(obj.name).toLowerCase());
        if (obj.fullName) keys.push(String(obj.fullName).toLowerCase());
        if (obj.mobile) {
          const digits = String(obj.mobile).replace(/\D/g, '');
          if (digits.length >= 10) keys.push(digits.slice(-10));
        }
        return keys;
      };

      const isSeen = (obj) => {
        const keys = getKeys(obj);
        return keys.some(k => seenKeys.has(k));
      };

      const markSeen = (obj) => {
        const keys = getKeys(obj);
        keys.forEach(k => seenKeys.add(k));
      };

      // 1. Keep existing pending users in state (if not approved/rejected)
      for (const pu of this.data.pendingUsers) {
        if (!pu) continue;
        if (pu.status === 'APPROVED' || pu.status === 'REJECTED') continue;
        if (isSeen(pu)) continue;
        markSeen(pu);
        currentList.push(pu);
      }

      // 2. Scan kaamsetu_users_db for any user registered with status === 'PENDING'
      for (const k in db) {
        const u = db[k];
        if (u && (u.status === 'PENDING' || u.trustStatus === 'PENDING')) {
          if (isSeen(u)) continue;

          const wage = u.minDailyWage || u.minWage || 650;
          const pItem = {
            id: u.id || `u_pend_${u.username || Date.now()}`,
            backendId: u.backendId || u.id,
            username: u.username || '',
            fullName: u.fullName || u.name || u.username || 'नवीन वापरकर्ता',
            name: u.fullName || u.name || u.username || 'नवीन वापरकर्ता',
            mobile: u.mobile || '+91 98000 00000',
            email: u.email || '',
            role: u.role || 'WORKER',
            providerType: u.providerType,
            type: u.type,
            businessName: u.businessName,
            gender: u.gender || 'MALE',
            avatar: u.avatar || (u.gender === 'FEMALE' ? (u.role === 'PROVIDER' ? '👩‍🌾' : '👷‍♀️') : (u.role === 'PROVIDER' ? '👨‍🌾' : '👷‍♂️')),
            village: u.village || 'रांजणगाव (Ranjangaon)',
            taluka: u.taluka || 'Shirur',
            district: u.district || 'Pune Rural',
            state: u.state || 'Maharashtra',
            country: u.country || 'India',
            minDailyWage: wage,
            minWage: wage,
            distanceKm: u.distanceKm !== undefined ? u.distanceKm : 2.4,
            travelRadiusKm: u.travelRadiusKm || 15,
            experienceYears: u.experienceYears || 4,
            rating: u.rating || 5.0,
            trustStatus: 'PENDING',
            trust: 'HEALTHY',
            skills: u.skills || ['cat.agriculture', 'cat.construction'],
            bio: u.bio || (u.role === 'WORKER' ? 'स्थानिक कामासाठी इच्छुक व प्रामाणिक कामगार.' : 'स्थानिक कामे उपलब्ध करून देणारे शेतकरी/नियोक्ता.'),
            facilities: u.facilities,
            mobileVerified: u.mobileVerified !== false,
            emailVerified: u.emailVerified !== false,
            status: 'PENDING',
            registrationDate: u.registrationDate || 'आज'
          };
          if (!isSeen(pItem)) {
            markSeen(pItem);
            currentList.unshift(pItem);
          }
        }
      }

      this.data.pendingUsers = currentList;
    } catch (e) {
      console.warn("syncAllPendingUsersFromRegistry error:", e);
    }
  }

  syncAllWorkersFromRegistry() {
    try {
      if (!this.data) this.data = {};
      const currentWorkers = Array.isArray(this.data.workers) ? this.data.workers : [];
      const db = SafeStorage.getJSON('kaamsetu_users_db', {});
      const registeredWorkers = [];
      const seenKeys = new Set();

      const getKeys = (obj) => {
        if (!obj) return [];
        const keys = [];
        if (obj.id) keys.push(String(obj.id).toLowerCase());
        if (obj.backendId) keys.push(String(obj.backendId).toLowerCase());
        if (obj.username) keys.push(String(obj.username).toLowerCase());
        if (obj.name) keys.push(String(obj.name).toLowerCase());
        if (obj.fullName) keys.push(String(obj.fullName).toLowerCase());
        if (obj.mobile) {
          const digits = String(obj.mobile).replace(/\D/g, '');
          if (digits.length >= 10) keys.push(digits.slice(-10));
        }
        return keys;
      };

      const isSeen = (obj) => {
        const keys = getKeys(obj);
        return keys.some(k => seenKeys.has(k));
      };

      const markSeen = (obj) => {
        const keys = getKeys(obj);
        keys.forEach(k => seenKeys.add(k));
      };

      const toWorkerItem = (u) => {
        if (!u) return null;
        const name = u.fullName || u.name || u.username;
        if (!name) return null;
        const rawSkills = u.skills || ["cat.agriculture", "cat.construction"];
        let skills = parseSkillsSafely(rawSkills);
        
        let days = { Mon: true, Tue: true, Wed: true, Thu: true, Fri: true, Sat: true, Sun: false };
        if (u.availability && u.availability.days) {
          days = Object.assign({}, days, u.availability.days);
        } else if (u.availabilityDays) {
          try {
            const parsed = typeof u.availabilityDays === 'string' ? JSON.parse(u.availabilityDays) : u.availabilityDays;
            days = Object.assign({}, days, parsed);
          } catch (e) {}
        }

        const wage = u.minDailyWage !== undefined ? u.minDailyWage : (u.minWage !== undefined ? u.minWage : 650);
        return {
          id: u.id || `w_${(u.username || name).replace(/\s+/g, '_').toLowerCase()}`,
          name: name,
          fullName: name,
          username: u.username || '',
          mobile: u.mobile || "+91 98220 00001",
          village: u.village || "रांजणगाव (Ranjangaon)",
          taluka: u.taluka || "Shirur",
          district: u.district || "Pune Rural",
          state: u.state || "Maharashtra",
          distanceKm: u.distanceKm !== undefined ? u.distanceKm : 2.4,
          skills: skills,
          experienceYears: u.experienceYears !== undefined ? u.experienceYears : 5,
          rating: u.rating || 4.8,
          trustStatus: u.trustStatus || u.trust || "HEALTHY",
          trust: u.trust || u.trustStatus || "HEALTHY",
          verified: u.verified !== undefined ? u.verified : true,
          dimensions: u.dimensions || { quality: 4.9, punctuality: 4.8, behavior: 5.0, reliability: 4.9 },
          travelRadiusKm: u.travelRadiusKm !== undefined ? u.travelRadiusKm : 15,
          minWage: wage,
          minDailyWage: wage,
          bio: u.bio || "स्थानिक कामासाठी अनुभवी व प्रामाणिक कामगार.",
          availability: { days: days },
          verifiedBadges: u.badges || ["📱 Mobile Verified", "📍 Location Verified", "⭐ Trusted Worker"]
        };
      };

      // 1. Process currentWorkers in memory, skipping any duplicates
      for (const w of currentWorkers) {
        if (!w) continue;
        if (isSeen(w)) continue;
        markSeen(w);
        w.distanceKm = w.distanceKm !== undefined ? w.distanceKm : 2.4;
        w.minDailyWage = w.minDailyWage !== undefined ? w.minDailyWage : (w.minWage !== undefined ? w.minWage : 650);
        w.minWage = w.minDailyWage;
        registeredWorkers.push(w);
      }

      // 2. Process registered workers from kaamsetu_users_db
      for (const k in db) {
        const u = db[k];
        if (!u) continue;
        if (u.status === 'PENDING' || u.trustStatus === 'PENDING') continue;

        if (u.role === 'WORKER' || (!u.role && (u.minDailyWage || u.travelRadiusKm))) {
          if (isSeen(u)) continue;
          const wItem = toWorkerItem(u);
          if (wItem && !isSeen(wItem)) {
            markSeen(wItem);
            registeredWorkers.push(wItem);
          }
        }
      }

      this.data.workers = registeredWorkers;
    } catch (e) {
      console.warn("syncAllWorkersFromRegistry warning:", e);
    }
  }

  syncAllProvidersFromRegistry() {
    try {
      if (!this.data) this.data = {};
      const currentProviders = Array.isArray(this.data.providers) ? this.data.providers : [];
      const db = SafeStorage.getJSON('kaamsetu_users_db', {});
      const registeredProviders = [];
      const seenKeys = new Set();

      const getKeys = (obj) => {
        if (!obj) return [];
        const keys = [];
        if (obj.id) keys.push(String(obj.id).toLowerCase());
        if (obj.backendId) keys.push(String(obj.backendId).toLowerCase());
        if (obj.username) keys.push(String(obj.username).toLowerCase());
        if (obj.name) keys.push(String(obj.name).toLowerCase());
        if (obj.fullName) keys.push(String(obj.fullName).toLowerCase());
        if (obj.mobile) {
          const digits = String(obj.mobile).replace(/\D/g, '');
          if (digits.length >= 10) keys.push(digits.slice(-10));
        }
        return keys;
      };

      const isSeen = (obj) => {
        const keys = getKeys(obj);
        return keys.some(k => seenKeys.has(k));
      };

      const markSeen = (obj) => {
        const keys = getKeys(obj);
        keys.forEach(k => seenKeys.add(k));
      };

      for (const p of currentProviders) {
        if (!p) continue;
        if (isSeen(p)) continue;
        markSeen(p);
        registeredProviders.push(p);
      }

      for (const k in db) {
        const u = db[k];
        if (!u) continue;
        if (u.status === 'PENDING' || u.trustStatus === 'PENDING') continue;
        if (u.role === 'PROVIDER' || u.role === 'EMPLOYER') {
          if (isSeen(u)) continue;
          const pName = u.fullName || u.name || u.username;
          if (!pName) continue;
          const pItem = {
            id: u.id || `p_${(u.username || pName).replace(/\s+/g, '_').toLowerCase()}`,
            name: pName,
            fullName: pName,
            username: u.username || '',
            businessName: u.businessName || `${pName} फार्म्स`,
            providerType: u.providerType || 'FARMER',
            type: u.type || 'provider.type.farmer',
            village: u.village || "सासवड",
            taluka: u.taluka || "Shirur",
            district: u.district || "Pune Rural",
            state: u.state || "Maharashtra",
            mobile: u.mobile || "+91 98000 00000",
            rating: u.rating || 5.0,
            trustStatus: u.trustStatus || u.trust || "HEALTHY",
            trust: u.trust || u.trustStatus || "HEALTHY",
            verified: u.verified !== undefined ? u.verified : true
          };
          if (!isSeen(pItem)) {
            markSeen(pItem);
            registeredProviders.push(pItem);
          }
        }
      }

      this.data.providers = registeredProviders;
    } catch (e) {
      console.warn("syncAllProvidersFromRegistry warning:", e);
    }
  }

  syncUserToState(user) {
    if (!user) {
      this.data.currentUser = null;
      return;
    }
    const existing = this.data.currentUser || {};

    // --- Restore persisted local preferences (availability, skills) from localStorage ---
    let savedLocal = {};
    try {
      const db = SafeStorage.getJSON('kaamsetu_users_db', {});
      const tryKeys = [
        String(user.username || '').trim().toLowerCase(),
        String(user.name || user.fullName || '').trim().toLowerCase(),
        String(user.mobile || '').replace(/\D/g, ''),
        String(user.email || '').toLowerCase()
      ];
      for (const k of tryKeys) {
        if (k && db[k]) { savedLocal = db[k]; break; }
      }
    } catch (e) {}
    // --- End restore ---

    // Parse skills from Array, JSON string (backend/storage), or fallback
    let rawSkills = user.skills || savedLocal.skills || existing.skills;
    let resolvedSkills = parseSkillsSafely(rawSkills);

    // Parse availability from object, JSON string, or fallback
    let rawAvail = user.availability || savedLocal.availability || existing.availability;
    let resolvedAvail = {
      days: { Mon: true, Tue: true, Wed: true, Thu: true, Fri: true, Sat: true, Sun: false },
      slots: ["Morning", "Afternoon", "Full Day"]
    };
    if (rawAvail && typeof rawAvail === 'object' && rawAvail.days) {
      resolvedAvail = rawAvail;
    } else if (typeof user.availabilityDays === 'string' && user.availabilityDays.trim().startsWith('{')) {
      try {
        resolvedAvail = {
          days: JSON.parse(user.availabilityDays),
          slots: ["Morning", "Afternoon", "Full Day"]
        };
      } catch (e) {}
    }

    const displayName = user.fullName || user.name || user.username || existing.name || "User";
    this.data.currentUser = {
      id: user.id || existing.id || "u_" + Date.now(),
      name: displayName,
      fullName: displayName,
      username: user.username || existing.username || "",
      role: user.role || this.currentRole || "WORKER",
      mobile: user.mobile || existing.mobile || "+91 98000 00000",
      email: user.email || existing.email || "",
      village: user.village || existing.village || "रांजणगाव (Ranjangaon)",
      taluka: user.taluka || existing.taluka || "Shirur",
      district: user.district || existing.district || "Pune Rural",
      state: user.state || existing.state || "Maharashtra",
      country: user.country || existing.country || "India",
      villageId: user.villageId || existing.villageId || "vil-ranjangaon",
      talukaId: user.talukaId || existing.talukaId || "tal-shirur",
      districtId: user.districtId || existing.districtId || "dist-pune",
      stateId: user.stateId || existing.stateId || "state-mh",
      countryId: user.countryId || existing.countryId || "IN",
      gender: user.gender || existing.gender || "MALE",
      avatar: user.avatar || (user.gender === "FEMALE" ? (user.role === "PROVIDER" ? "👩" : "👷‍♀️") : (user.role === "PROVIDER" ? "👨" : (user.role === "ADMIN" ? "🛡️" : "👷‍♂️"))),
      // Skills: 100% persistent across re-login
      skills: resolvedSkills,
      travelRadiusKm: Number(user.travelRadiusKm) || savedLocal.travelRadiusKm || existing.travelRadiusKm || 10,
      minDailyWage: Number(user.minDailyWage || user.minWage) || savedLocal.minDailyWage || existing.minDailyWage || 600,
      rating: Number(user.rating) || existing.rating || 4.8,
      trustIndex: user.trustIndex || existing.trustIndex || "98%",
      badges: user.badges || existing.badges || ["📱 Mobile Verified", "📍 Location Verified", "⭐ Trusted Worker"],
      // Availability: 100% persistent across re-login
      availability: resolvedAvail
    };

    // Synchronize permanent user-admin two-way chat history
    this.syncUserChatHistory(this.data.currentUser);
  }

  // Permanent two-way user-admin chat synchronization
  syncUserChatHistory(user) {
    if (!user) return;
    // An Admin communicates with users, and should never have a self-thread in adminConversations!
    if (user.role === 'ADMIN' || user.username === 'admin' || (user.name && user.name.includes('Admin')) || (user.fullName && user.fullName.includes('Admin'))) {
      return;
    }
    try {
      if (!this.data.conversations) this.data.conversations = [];
      if (!this.data.adminConversations) this.data.adminConversations = [];

      const keys = [user.id, user.username, user.name, user.fullName].filter(Boolean).map(k => String(k).toLowerCase());
      const normalize = s => (s || '').toLowerCase().replace(/[()_\-\s]/g, '');

      // 1. Locate existing admin thread for this user
      let adminThread = this.data.adminConversations.find(c => {
        if (c.userId && keys.includes(String(c.userId).toLowerCase())) return true;
        if (c.id && (keys.includes(String(c.id).toLowerCase()) || keys.some(k => c.id === 'admin_thread_' + k))) return true;
        if (c.userName) {
          const normAdmin = normalize(c.userName);
          for (const k of keys) {
            const normK = normalize(k);
            if (normAdmin.includes(normK) || normK.includes(normAdmin)) return true;
          }
        }
        return false;
      });

      // 2. Locate backup chats from persistent SafeStorage
      const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db', {});
      let persistedMsgs = null;
      for (const k of keys) {
        const normK = normalize(k);
        if (chatsDb[normK] && Array.isArray(chatsDb[normK]) && chatsDb[normK].length > 0) {
          persistedMsgs = chatsDb[normK];
          break;
        }
      }

      // 3. Populate adminThread if missing
      const dName = user.fullName || user.name || user.username || "वापरकर्ता";
      const dRole = user.role || "WORKER";
      if (!adminThread) {
        adminThread = {
          id: "admin_thread_" + (user.username || user.id || normalize(dName)),
          userId: user.id || "u_" + (user.username || normalize(dName)),
          userName: dName,
          userRole: dRole,
          userStatus: user.status || "ACTIVE",
          avatar: dRole === "WORKER" ? "👷" : (dRole === "ADMIN" ? "🛡️" : "👤"),
          lastMessage: "नवीन संभाषण सुरू झाले.",
          lastMessageTime: "आत्ताच",
          unread: false,
          messages: []
        };
        this.data.adminConversations.unshift(adminThread);
      }

      if (persistedMsgs && persistedMsgs.length > 0) {
        if (!adminThread.messages || adminThread.messages.length < persistedMsgs.length) {
          adminThread.messages = persistedMsgs.map(m => ({
            id: m.id || ("m_" + Date.now()),
            sender: (m.sender === "ME" || m.sender === "USER") ? "USER" : "ADMIN",
            text: m.text,
            time: m.time || "आत्ताच"
          }));
          const lastM = adminThread.messages[adminThread.messages.length - 1];
          if (lastM) {
            adminThread.lastMessage = lastM.text;
            adminThread.lastMessageTime = lastM.time;
          }
        }
      }

      // 4. Update the user-side official admin conversation specifically for THIS user
      const userAdminConvId = `conv_admin_${uKey}`;
      let userConv = this.data.conversations.find(c => c.id === userAdminConvId || (c.id && c.id.startsWith('conv_admin') && normalize(c.ownerUsername) === uKey));

      // Remove any other duplicate admin conversations (leave peer conversations intact)
      this.data.conversations = this.data.conversations.filter(c => {
        if (c.id === userAdminConvId) return false;
        if (c.participantId === 'admin_sys' || (c.id && c.id.startsWith('conv_admin'))) {
          return false;
        }
        return true;
      });

      if (!userConv) {
        userConv = {
          id: userAdminConvId,
          ownerId: user.id,
          ownerUsername: uKey,
          participantId: "admin_sys",
          participantName: "🛡️ प्रशासन (KaamSetu Admin)",
          avatar: "🛡️",
          jobTitle: "अधिकृत प्रशासकीय व मदत कक्ष (Official Admin Channel)",
          lastMessage: "कामसेतू अधिकृत मदत कक्षामध्ये आपले स्वागत आहे.",
          lastMessageTime: "नेहमी उपलब्ध",
          unreadCount: 0,
          participantIds: [user.id, uKey, "admin_sys", "admin", "प्रशासन"],
          messages: []
        };
      } else {
        userConv.id = userAdminConvId;
        userConv.ownerId = user.id;
        userConv.ownerUsername = uKey;
        userConv.participantId = "admin_sys";
        userConv.participantName = "🛡️ प्रशासन (KaamSetu Admin)";
        userConv.avatar = "🛡️";
        userConv.jobTitle = "अधिकृत प्रशासकीय व मदत कक्ष (Official Admin Channel)";
      }

      if (adminThread && Array.isArray(adminThread.messages) && adminThread.messages.length > 0) {
        userConv.messages = adminThread.messages.map(m => ({
          id: m.id,
          sender: (m.sender === "USER" || m.sender === "ME") ? "ME" : "THEM",
          text: m.text,
          time: m.time || "आत्ताच"
        }));
        const lastM = userConv.messages[userConv.messages.length - 1];
        if (lastM) {
          userConv.lastMessage = lastM.text;
          userConv.lastMessageTime = lastM.time;
        }
      } else if (!userConv.messages || userConv.messages.length === 0) {
        userConv.messages = [
          {
            id: "m_welcome_admin",
            sender: "THEM",
            text: "नमस्कार! कामसेतू अधिकृत मदत व प्रशासन कक्षामध्ये आपले स्वागत आहे. काही अडचण, पडताळणी किंवा प्रश्न असल्यास येथे थेट संदेश पाठवू शकता. आमची टीम आपल्याला मदत करेल.",
            time: "मदत कक्ष"
          }
        ];
      }

      this.data.conversations.unshift(userConv);

      // 5. Update the persistent user chats database
      const uKeyFinal = normalize(user.username || user.name || user.fullName || user.id);
      if (uKeyFinal && userConv.messages) {
        chatsDb[uKeyFinal] = userConv.messages;
        SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
      }
    } catch (err) {
      console.warn("syncUserChatHistory error:", err);
    }
  }

  setCurrentUser(user) {
    this.syncUserToState(user);
    this.notify();
  }

  subscribe(listener) {
    this.listeners.push(listener);
  }

  clearUserConversation(convId) {
    if (!convId) return;
    const conv = (this.data.conversations || []).find(c => c.id === convId);
    if (conv) {
      conv.messages = [];
      conv.lastMessage = "चॅट इतिहास साफ केला आहे.";
      conv.lastMessageTime = "आत्ताच";
    }
    try {
      const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db') || {};
      const cUser = this.data.currentUser || (typeof AuthManager !== 'undefined' && AuthManager.getCurrentUser()) || {};
      const uKey = String(cUser.username || cUser.name || cUser.fullName || cUser.id || '').toLowerCase().trim();
      if (uKey && chatsDb[uKey]) {
        delete chatsDb[uKey];
        SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
      }
    } catch (e) {}

    this.notify();
  }

  clearAdminThread(threadId) {
    if (!threadId) return;
    const thread = (this.data.adminConversations || []).find(t => t.id === threadId);
    if (thread) {
      thread.messages = [];
      thread.lastMessage = "चॅट साफ केला.";
      thread.lastMessageTime = "आत्ताच";
      thread.unread = false;
    }
    this.notify();
  }

  notify() {
    try {
      if (Array.isArray(this.data.jobs)) {
        SafeStorage.setItem("kaamsetu_jobs_registry", this.data.jobs);
      }
      if (Array.isArray(this.data.assignments)) {
        SafeStorage.setItem("kaamsetu_assignments_registry", this.data.assignments);
      }
      if (Array.isArray(this.data.conversations)) {
        SafeStorage.setItem("kaamsetu_conversations_registry", this.data.conversations);
      }
      if (Array.isArray(this.data.adminConversations)) {
        SafeStorage.setItem("kaamsetu_admin_conversations_registry", this.data.adminConversations);
      }
      const compacted = SafeStorage.compactState(this.data);
      SafeStorage.setItem("kaamsetu_state", compacted);
      SafeStorage.setItem("kaamsetu_role", this.currentRole || "WORKER");
      SafeStorage.setItem("kaamsetu_view", this.activeView || "landing");
      SafeStorage.setItem("kaamsetu_saved_jobs", JSON.stringify([...this.savedJobs]));
      SafeStorage.setItem("kaamsetu_saved_workers", JSON.stringify([...this.savedWorkers]));
    } catch (e) {
      console.warn("[AppState.notify] Storage note:", e);
    }
    this.listeners.forEach(fn => {
      try {
        fn(this);
      } catch (err) {
        console.error("[AppState.notify] Listener error:", err);
      }
    });
  }

  setRole(role) {
    this.currentRole = role || "WORKER";
    this.activeView = (role === "ADMIN") ? "admin" : "home";
    this.notify();
  }

  setView(view) {
    this.activeView = view;
    this.notify();
  }

  setCategory(cat) {
    this.activeCategory = cat;
    this.notify();
  }

  setTaluka(taluka) {
    this.activeTaluka = taluka;
    this.notify();
  }

  setWorkerSearchQuery(query) {
    this.workerSearchQuery = query || "";
    this.notify();
  }

  setRadius(km) {
    this.selectedRadius = km;
    this.notify();
  }

  setNotificationCategory(category) {
    this.activeNotificationCategory = category;
    this.notify();
  }

  markNotificationRead(notifId) {
    const notif = this.data.notifications.find(n => n.id === notifId);
    if (notif) {
      notif.unread = false;
      this.notify();
    }
  }

  markAllNotificationsRead() {
    this.data.notifications.forEach(n => n.unread = false);
    this.notify();
  }

  // --- ADMIN TO USER MESSAGING & BROADCAST DISPATCHER ---
  sendAdminDirectMessage({ targetUserId, targetUserName, targetRole, title, message, category = "safety" }) {
    if (!this.data.adminMessages) this.data.adminMessages = [];
    if (!this.data.notifications) this.data.notifications = [];
    if (!this.data.auditLogs) this.data.auditLogs = [];
    if (!this.data.conversations) this.data.conversations = [];

    const msgId = "adm_msg_" + Date.now();
    const nowTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const nowIso = new Date().toISOString().replace('T', ' ').substring(0, 19);

    const adminMsg = {
      id: msgId,
      type: "DIRECT",
      targetUserId: targetUserId || null,
      targetUserName: targetUserName || "User",
      targetRole: targetRole || "ALL",
      title: title,
      body: message,
      category: category,
      timestamp: "आत्ताच (Just now)",
      timeIso: nowIso,
      dismissed: false,
      sender: "🛡️ प्रशासन (KaamSetu Admin)"
    };

    this.data.adminMessages.unshift(adminMsg);

    // 1. Dispatch real-time notification
    this.data.notifications.unshift({
      id: "notif_" + msgId,
      category: category === "direct" ? "messages" : "safety",
      title: `🛡️ ${title}`,
      message: message,
      time: "आत्ताच (Just now)",
      unread: true,
      targetUserId: targetUserId,
      targetUserName: targetUserName,
      targetRole: targetRole,
      adminMessageId: msgId
    });

    // 2. Add or update 2-way conversation in Chat Hub
    let conv = this.data.conversations.find(c => c.participantName === "🛡️ प्रशासन (KaamSetu Admin)" || c.id.startsWith("conv_admin"));
    if (!conv) {
      conv = {
        id: "conv_admin_" + (targetUserId || Date.now()),
        participantId: "admin_sys",
        participantName: "🛡️ प्रशासन (KaamSetu Admin)",
        avatar: "🛡️",
        jobTitle: "अधिकृत प्रशासकीय सूचना (Official Admin Channel)",
        lastMessage: message,
        lastMessageTime: nowTime,
        unreadCount: 1,
        messages: []
      };
      this.data.conversations.unshift(conv);
    }
    if (!Array.isArray(conv.messages)) conv.messages = [];
    
    // Filter out any stale mock auto-replies
    conv.messages = conv.messages.filter(m => !m.id.startsWith("m_rep_"));

    const formattedText = (title && title.toLowerCase() !== message.toLowerCase() ? `📢 ${title}\n\n${message}` : message);
    conv.messages.push({
      id: "m_" + Date.now(),
      sender: "THEM",
      text: formattedText,
      time: nowTime
    });
    conv.lastMessage = message;
    conv.lastMessageTime = "आत्ताच";
    conv.unreadCount = (conv.unreadCount || 0) + 1;

    // 3. Log Immutable Audit Trail
    this.data.auditLogs.unshift({
      id: "aud_" + Date.now(),
      actor: "admin_sys",
      event: "ADMIN_DIRECT_MESSAGE_SENT",
      target: `${targetUserName} (${targetRole || 'USER'})`,
      status: "DELIVERED",
      time: nowIso,
      ip: "127.0.0.1",
      details: `${title} -> ${message.substring(0, 50)}...`
    });

    this.notify();
    return adminMsg;
  }

  sendAdminBroadcast({ target = "ALL", title, message, category = "safety" }) {
    if (!this.data.adminMessages) this.data.adminMessages = [];
    if (!this.data.notifications) this.data.notifications = [];
    if (!this.data.auditLogs) this.data.auditLogs = [];
    if (!this.data.conversations) this.data.conversations = [];

    const msgId = "adm_bc_" + Date.now();
    const nowTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const nowIso = new Date().toISOString().replace('T', ' ').substring(0, 19);

    const adminMsg = {
      id: msgId,
      type: "BROADCAST",
      targetRole: target, // ALL, WORKERS, PROVIDERS
      title: title,
      body: message,
      category: category,
      timestamp: "आत्ताच (Just now)",
      timeIso: nowIso,
      dismissed: false,
      sender: "🛡️ प्रशासन (KaamSetu Admin)"
    };

    this.data.adminMessages.unshift(adminMsg);

    // Dispatch real-time notification
    this.data.notifications.unshift({
      id: "notif_" + msgId,
      category: "safety",
      title: `📢 ${title}`,
      message: message,
      time: "आत्ताच (Just now)",
      unread: true,
      targetRole: target,
      adminMessageId: msgId
    });

    // Also sync to Admin conversation thread
    let conv = this.data.conversations.find(c => c.participantName === "🛡️ प्रशासन (KaamSetu Admin)" || c.id.startsWith("conv_admin"));
    if (!conv) {
      conv = {
        id: "conv_admin_broadcast",
        participantId: "admin_sys",
        participantName: "🛡️ प्रशासन (KaamSetu Admin)",
        avatar: "🛡️",
        jobTitle: "अधिकृत प्रशासकीय सूचना (Official Admin Channel)",
        lastMessage: message,
        lastMessageTime: nowTime,
        unreadCount: 1,
        messages: []
      };
      this.data.conversations.unshift(conv);
    }
    if (!Array.isArray(conv.messages)) conv.messages = [];
    conv.messages = conv.messages.filter(m => !m.id.startsWith("m_rep_"));
    conv.messages.push({
      id: "m_bc_" + Date.now(),
      sender: "THEM",
      text: `📢 ${title}\n\n${message}`,
      time: nowTime
    });
    conv.lastMessage = message;
    conv.lastMessageTime = "आत्ताच";
    conv.unreadCount = (conv.unreadCount || 0) + 1;

    // Log Immutable Audit Trail
    this.data.auditLogs.unshift({
      id: "aud_" + Date.now(),
      actor: "admin_sys",
      event: "ADMIN_BROADCAST_SENT",
      target: target,
      status: "BROADCASTED",
      time: nowIso,
      ip: "127.0.0.1",
      details: `${title} -> ${message.substring(0, 50)}...`
    });

    this.notify();
    return adminMsg;
  }

  dismissAdminMessage(msgId) {
    if (!this.data.adminMessages) return;
    const msg = this.data.adminMessages.find(m => m.id === msgId);
    if (msg) {
      msg.dismissed = true;
      this.notify();
    }
  }

  getAdminMessagesForUser(user, role) {
    const list = this.data.adminMessages || [];
    const uName = (user?.name || user?.fullName || user?.username || "").toLowerCase();
    const uId = user?.id;
    const userRole = (role || user?.role || "").toUpperCase();

    return list.filter(m => {
      if (m.dismissed) return false;
      if (m.type === "BROADCAST") {
        if (m.targetRole === "ALL") return true;
        if (m.targetRole === "WORKERS" && (userRole === "WORKER" || userRole === "ROLE_WORKER")) return true;
        if (m.targetRole === "PROVIDERS" && (userRole === "PROVIDER" || userRole === "ROLE_PROVIDER")) return true;
        return false;
      }
      if (m.type === "DIRECT") {
        if (uId && m.targetUserId && String(m.targetUserId) === String(uId)) return true;
        if (m.targetUserName && uName && (m.targetUserName.toLowerCase().includes(uName) || uName.includes(m.targetUserName.toLowerCase()))) return true;
        if (m.targetRole && m.targetRole !== "ALL" && m.targetRole !== userRole) return false;
        return true;
      }
      return false;
    });
  }

  recordUserReplyToAdmin({ userName, userRole, userId, text, time }) {
    if (userRole === 'ADMIN' || userId === 'admin' || (userName && userName.includes('Admin'))) {
      return;
    }
    if (!this.data.adminConversations) this.data.adminConversations = [];
    if (!this.data.notifications) this.data.notifications = [];
    if (!this.data.auditLogs) this.data.auditLogs = [];

    const nowTime = time || new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const nowIso = new Date().toISOString().replace('T', ' ').substring(0, 19);
    const key = (userId || userName || "user").toString();
    const normalize = str => (str || '').toLowerCase().replace(/[()_\-\s]/g, '');
    const cleanUserName = userName || "वापरकर्ता";

    let adminThread = this.data.adminConversations.find(c => 
      (c.userId && key && (c.userId === key || normalize(c.userId) === normalize(key))) ||
      (c.id && key && (c.id === key || c.id === ("admin_thread_" + key) || normalize(c.id) === normalize(key))) ||
      (c.userName && cleanUserName && (
        c.userName === cleanUserName || 
        normalize(c.userName).includes(normalize(cleanUserName)) || 
        normalize(cleanUserName).includes(normalize(c.userName))
      ))
    );

    if (!adminThread) {
      adminThread = {
        id: "admin_thread_" + (normalize(key) || Date.now()),
        userId: key,
        userName: cleanUserName,
        userRole: userRole || "WORKER",
        userStatus: "ACTIVE",
        avatar: userRole === "WORKER" ? "👷" : "👤",
        lastMessage: text,
        lastMessageTime: nowTime,
        unread: true,
        messages: []
      };
      this.data.adminConversations.unshift(adminThread);
    } else {
      // Move active thread to top
      const idx = this.data.adminConversations.indexOf(adminThread);
      if (idx > 0) {
        this.data.adminConversations.splice(idx, 1);
        this.data.adminConversations.unshift(adminThread);
      }
    }

    if (!Array.isArray(adminThread.messages)) adminThread.messages = [];
    const newMsg = {
      id: "m_u_" + Date.now(),
      sender: "USER",
      text: text,
      time: nowTime
    };
    adminThread.messages.push(newMsg);
    adminThread.lastMessage = text;
    adminThread.lastMessageTime = nowTime;
    adminThread.unread = true;

    // Permanently save to user chats DB in SafeStorage
    try {
      const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db', {});
      const uKeys = [
        normalize(cleanUserName),
        normalize(userId),
        normalize(adminThread.userName),
        normalize(adminThread.userId)
      ].filter(Boolean);

      const userMsgItem = {
        id: newMsg.id,
        sender: "ME",
        text: text,
        time: nowTime
      };

      uKeys.forEach(k => {
        const curList = Array.isArray(chatsDb[k]) ? chatsDb[k] : [];
        curList.push(userMsgItem);
        chatsDb[k] = curList;
      });
      SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
    } catch (e) {}

    // Dispatch Admin Notification for new incoming message
    this.data.notifications.unshift({
      id: "notif_admin_in_" + Date.now(),
      category: "messages",
      title: `💬 नवीन संदेश: ${cleanUserName} (${userRole || 'USER'})`,
      message: text,
      time: "आत्ताच (Just now)",
      unread: true,
      targetRole: "ADMIN",
      userSender: cleanUserName
    });

    this.notify();
    return adminThread;
  }

  sendAdminReplyFromInbox(targetUserId, replyText) {
    if (!this.data.adminConversations) this.data.adminConversations = [];
    if (!this.data.conversations) this.data.conversations = [];

    const nowTime = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const normalize = str => (str || '').toLowerCase().replace(/[()_\-\s]/g, '');

    let adminThread = this.data.adminConversations.find(c => 
      c.userId === targetUserId || 
      c.id === targetUserId ||
      (c.id && targetUserId && (c.id === `admin_thread_${targetUserId}` || targetUserId === `admin_thread_${c.userId}`)) ||
      (c.userName && targetUserId && (
        c.userName === targetUserId ||
        normalize(c.userName).includes(normalize(targetUserId)) ||
        normalize(targetUserId).includes(normalize(c.userName))
      ))
    );

    if (!adminThread && this.data.adminConversations.length > 0) {
      adminThread = this.data.adminConversations[0];
    }
    if (!adminThread) return;

    if (!Array.isArray(adminThread.messages)) adminThread.messages = [];
    const newAdminMsg = {
      id: "m_adm_rep_" + Date.now(),
      sender: "ADMIN",
      text: replyText,
      time: nowTime
    };
    adminThread.messages.push(newAdminMsg);
    adminThread.lastMessage = replyText;
    adminThread.lastMessageTime = nowTime;
    adminThread.unread = false;

    // Permanently save to user chats DB in SafeStorage
    try {
      const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db', {});
      const uKeys = [
        normalize(adminThread.userName),
        normalize(adminThread.userId),
        normalize(targetUserId)
      ].filter(Boolean);

      const adminMsgItem = {
        id: newAdminMsg.id,
        sender: "THEM",
        text: replyText,
        time: nowTime
      };

      uKeys.forEach(k => {
        const curList = Array.isArray(chatsDb[k]) ? chatsDb[k] : [];
        curList.push(adminMsgItem);
        chatsDb[k] = curList;
      });
      SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
    } catch (e) {}

    // Push into user's active chat stream
    let userConv = this.data.conversations.find(c => c.participantName && (c.participantName.includes("प्रशासन") || c.participantName.includes("Admin") || c.id.startsWith("conv_admin")));
    if (!userConv) {
      userConv = {
        id: "conv_admin_official",
        participantId: "admin_sys",
        participantName: "🛡️ प्रशासन (KaamSetu Admin)",
        avatar: "🛡️",
        jobTitle: "अधिकृत प्रशासकीय व मदत कक्ष (Official Admin Channel)",
        messages: []
      };
      this.data.conversations.unshift(userConv);
    }
    if (!Array.isArray(userConv.messages)) userConv.messages = [];
    userConv.messages.push({
      id: newAdminMsg.id,
      sender: "THEM",
      text: replyText,
      time: nowTime
    });
    userConv.lastMessage = replyText;
    userConv.lastMessageTime = "आत्ताच";
    userConv.unreadCount = (userConv.unreadCount || 0) + 1;

    // Add user notification
    this.data.notifications.unshift({
      id: "notif_rep_" + Date.now(),
      category: "messages",
      title: "🛡️ प्रशासनाकडून उत्तर (Admin Reply)",
      message: replyText,
      time: "आत्ताच (Just now)",
      unread: true,
      targetUserId: adminThread.userId,
      targetUserName: adminThread.userName
    });

    this.notify();
  }

  // --------------------------------------------------------------------------
  // PERMANENT CLEAR / DELETE CHAT METHODS (Dual-Side)
  // --------------------------------------------------------------------------
  clearAdminThread(threadId) {
    if (!this.data.adminConversations) return;
    const normalize = str => (str || '').toLowerCase().replace(/[()_\-\s]/g, '');

    let thread = this.data.adminConversations.find(c => 
      c.id === threadId ||
      c.userId === threadId ||
      (c.userName && normalize(c.userName) === normalize(threadId))
    );

    if (thread) {
      thread.messages = [];
      thread.lastMessage = "संभाषण साफ केले (Chat Cleared)";
      thread.lastMessageTime = "आत्ताच";
      thread.unread = false;

      // Also clear persistent user chats DB
      try {
        const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db', {});
        const uKeys = [
          normalize(thread.userName),
          normalize(thread.userId),
          normalize(thread.id),
          normalize(threadId)
        ].filter(Boolean);

        uKeys.forEach(k => {
          delete chatsDb[k];
        });
        SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
      } catch (e) {}

      // If active user is currently viewing this, clear user conversation as well
      if (this.data.currentUser) {
        const curNorm = normalize(this.data.currentUser.username || this.data.currentUser.name || this.data.currentUser.fullName || this.data.currentUser.id);
        const threadNorm = normalize(thread.userName || thread.userId);
        if (curNorm === threadNorm || curNorm.includes(threadNorm) || threadNorm.includes(curNorm)) {
          let userConv = (this.data.conversations || []).find(c => c.participantName && (c.participantName.includes("प्रशासन") || c.participantName.includes("Admin") || c.id.startsWith("conv_admin")));
          if (userConv) {
            userConv.messages = [
              {
                id: "m_welcome_admin",
                sender: "THEM",
                text: "नमस्कार! कामसेतू अधिकृत मदत व प्रशासन कक्षामध्ये आपले स्वागत आहे. काही अडचण, पडताळणी किंवा प्रश्न असल्यास येथे थेट संदेश पाठवू शकता.",
                time: "मदत कक्ष"
              }
            ];
            userConv.lastMessage = "संभाषण साफ केले";
            userConv.lastMessageTime = "आत्ताच";
          }
        }
      }

      this.notify();
    }
  }

  clearUserConversation(convId = "conv_admin_official") {
    if (!this.data.conversations) this.data.conversations = [];
    const normalize = str => (str || '').toLowerCase().replace(/[()_\-\s]/g, '');

    let conv = this.data.conversations.find(c => c.id === convId || c.participantName.includes("प्रशासन") || c.id.startsWith("conv_admin"));
    if (conv) {
      conv.messages = [
        {
          id: "m_welcome_admin",
          sender: "THEM",
          text: "नमस्कार! कामसेतू अधिकृत मदत व प्रशासन कक्षामध्ये आपले स्वागत आहे. काही अडचण, पडताळणी किंवा प्रश्न असल्यास येथे थेट संदेश पाठवू शकता.",
          time: "मदत कक्ष"
        }
      ];
      conv.lastMessage = "संभाषण साफ केले";
      conv.lastMessageTime = "आत्ताच";
      conv.unreadCount = 0;
    }

    const cUser = this.data.currentUser;
    if (cUser) {
      const uKeys = [
        normalize(cUser.username),
        normalize(cUser.name),
        normalize(cUser.fullName),
        normalize(cUser.id)
      ].filter(Boolean);

      // Clear from user chats DB
      try {
        const chatsDb = SafeStorage.getJSON('kaamsetu_user_chats_db', {});
        uKeys.forEach(k => { delete chatsDb[k]; });
        SafeStorage.setItem('kaamsetu_user_chats_db', JSON.stringify(chatsDb));
      } catch (e) {}

      // Clear from admin thread as well
      if (this.data.adminConversations) {
        let adminThread = this.data.adminConversations.find(c => {
          if (c.userId && uKeys.includes(normalize(c.userId))) return true;
          if (c.id && uKeys.includes(normalize(c.id))) return true;
          if (c.userName) {
            const normAdmin = normalize(c.userName);
            return uKeys.some(k => normAdmin.includes(k) || k.includes(normAdmin));
          }
          return false;
        });
        if (adminThread) {
          adminThread.messages = [];
          adminThread.lastMessage = "वापरकर्त्याने चॅट साफ केले.";
          adminThread.lastMessageTime = "आत्ताच";
          adminThread.unread = false;
        }
      }
    }

    this.notify();
  }

  toggleSaveJob(jobId) {
    if (this.savedJobs.has(jobId)) {
      this.savedJobs.delete(jobId);
    } else {
      this.savedJobs.add(jobId);
    }
    this.notify();
  }

  toggleSaveWorker(workerId) {
    if (this.savedWorkers.has(workerId)) {
      this.savedWorkers.delete(workerId);
    } else {
      this.savedWorkers.add(workerId);
    }
    this.notify();
  }

  calculateJobMatch(job) {
    const user = this.data.currentUser;
    let score = 40;
    const isCatMatch = user.skills && user.skills.includes(job.category);
    if (isCatMatch) score += 25;
    const wageSatisfied = !user.minDailyWage || job.dailyWage >= user.minDailyWage;
    if (wageSatisfied) score += 15;
    if (job.distanceKm <= 5.0) score += 10;
    else if (job.distanceKm <= 10.0) score += 5;
    if (job.urgent) score += 10;
    return Math.min(score, 100);
  }

  // --- JOB LIFECYCLE STATE MACHINE ACTIONS ---

  // 1. Worker Applies to Job
  async applyToJob(jobId) {
    const job = this.data.jobs.find(j => j.id === jobId);
    if (!job || (job.status !== "OPEN" && job.status !== "APPLIED")) return false;

    const user = this.data.currentUser || (typeof AuthManager !== 'undefined' && AuthManager.getCurrentUser()) || {};
    const uId = user.id || user.userId || user.username || "w_worker";
    const uName = user.fullName || user.name || user.username || "कामगार";
    const uUser = user.username || "";

    // Check if already applied
    const existing = this.data.assignments.find(a => a.jobId === jobId && (
      a.workerId === uId || 
      (uUser && (a.workerUsername === uUser || a.workerId === uUser)) ||
      (a.workerName && a.workerName.toLowerCase() === uName.toLowerCase())
    ));
    if (existing) return false;

    // Check if capacity is already reached
    const existingAsgs = this.data.assignments.filter(a => a.jobId === jobId && (
      a.status === "APPLIED" || a.status === "SELECTED" || a.status === "CONFIRMED" || a.status === "IN_PROGRESS" || a.status === "COMPLETED"
    ));
    const reqWorkers = Number(job.workersRequired) || 1;
    if (existingAsgs.length >= reqWorkers) {
      job.status = "FILLED";
      this.notify();
      return false;
    }

    const newAsg = {
      id: "asg_" + Date.now(),
      jobId: job.id,
      workerId: uId,
      workerName: uName,
      workerUsername: uUser,
      providerId: job.providerId,
      providerName: job.providerName,
      jobTitle: job.title,
      status: "APPLIED",
      agreedWage: job.dailyWage,
      paymentType: "CASH",
      paymentStatus: "PENDING",
      paymentConfirmedByWorker: false
    };

    this.data.assignments.unshift(newAsg);

    // Update job applied & confirmed count
    const totalApps = existingAsgs.length + 1;
    job.workersConfirmed = totalApps;
    job.workersApplied = totalApps;

    // If required workers capacity is filled (e.g. 2/2), transition status to FILLED
    if (totalApps >= reqWorkers) {
      job.status = "FILLED";
    }

    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.applyToJob) {
        await ApiClient.applyToJob(jobId);
      }
    } catch (e) {
      console.info("Backend apply sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
    return true;
  }

  // 2. Provider Selects Worker (moves to SELECTED, initiates confirmation window)
  async selectWorker(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "SELECTED";
    asg.timeoutSeconds = 3600 * 24; // 24 hour confirmation timer

    this.data.notifications.unshift({
      id: "notif_" + Date.now(),
      category: "selection",
      title: "काम निवड (Selection Alert)",
      message: `${asg.providerName} यांनी '${asg.jobTitle}' कामासाठी निवड केली आहे. कृपया पुष्टी करा.`,
      time: "आत्ताच (Just now)",
      unread: true,
      actionId: asg.id
    });

    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.selectWorker && asg.jobId && asg.workerId) {
        await ApiClient.selectWorker(asg.jobId, asg.workerId);
      }
    } catch (e) {
      console.info("Backend selectWorker sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 3. Worker Confirms Assignment (moves to CONFIRMED, checks auto-FILLED rule)
  async confirmAssignment(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg || asg.status !== "SELECTED") return;

    asg.status = "CONFIRMED";

    // Update job confirmed worker count
    const job = this.data.jobs.find(j => j.id === asg.jobId);
    if (job) {
      job.workersConfirmed = (job.workersConfirmed || 0) + 1;
      // Auto-FILLED Rule Check
      if (job.workersConfirmed >= job.workersRequired) {
        job.status = "FILLED";
      }
    }

    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.confirmAssignment && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.confirmAssignment(asgId);
      }
    } catch (e) {
      console.info("Backend confirmAssignment sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 4. Worker Declines Assignment (moves to DECLINED, frees capacity slot)
  async declineAssignment(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "DECLINED";
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.declineAssignment && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.declineAssignment(asgId);
      }
    } catch (e) {
      console.info("Backend declineAssignment sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 5. Start Work (moves to IN_PROGRESS)
  async startWork(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "IN_PROGRESS";
    const job = this.data.jobs.find(j => j.id === asg.jobId);
    if (job && job.status !== "IN_PROGRESS") {
      job.status = "IN_PROGRESS";
    }
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.startWork && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.startWork(asgId);
      }
    } catch (e) {
      console.info("Backend startWork sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 6. Request Completion (moves to COMPLETION_REQUESTED)
  async requestCompletion(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "COMPLETION_REQUESTED";
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.requestCompletion && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.requestCompletion(asgId);
      }
    } catch (e) {
      console.info("Backend requestCompletion sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 7. Bilateral Confirm Completion (moves to COMPLETED, unlocks rating & payment)
  async confirmCompletion(asgId) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "COMPLETED";
    asg.completedAt = new Date().toISOString();

    const job = this.data.jobs.find(j => j.id === asg.jobId);
    if (job) {
      const allDone = this.data.assignments.filter(a => a.jobId === job.id).every(a => a.status === "COMPLETED");
      if (allDone) {
        job.status = "COMPLETED";
      }
    }
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.confirmCompletion && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.confirmCompletion(asgId);
      }
    } catch (e) {
      console.info("Backend confirmCompletion sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 8. Payment Receipt Acknowledgment & Provider Confirmation (Cash / UPI)
  async acknowledgePayment(asgId, paymentType = "CASH") {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.paymentStatus = "PAID";
    asg.paymentType = paymentType;
    asg.paymentConfirmedByWorker = true;
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.acknowledgePayment && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.acknowledgePayment(asgId, paymentType);
      }
    } catch (e) {
      console.info("Backend acknowledgePayment sync note:", e.message);
    }
  }

  async confirmPayment(asgId, overtimeAmount = 0, additionalAmount = 0, paymentType = "CASH") {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (asg) {
      asg.paymentStatus = "PAID";
      asg.overtimeAmount = Number(overtimeAmount) || 0;
      asg.additionalAmount = Number(additionalAmount) || 0;
      asg.totalAmount = (Number(asg.basePayment || asg.agreedWage) || 600) + asg.overtimeAmount + asg.additionalAmount;
      asg.paymentType = paymentType;
      this.notify();
    }

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.confirmPayment && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.confirmPayment(asgId, { overtimeAmount, additionalAmount, paymentType });
      }
    } catch (e) {
      console.info("Backend confirmPayment sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 9. Cancellation with Reason
  async cancelAssignment(asgId, reason) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "CANCELLED";
    asg.cancellationReason = reason;
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.cancelAssignment && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.cancelAssignment(asgId, reason);
      }
    } catch (e) {}
  }

  // 10. Report No-Show
  async reportNoShow(asgId, notes) {
    const asg = this.data.assignments.find(a => a.id === asgId);
    if (!asg) return;

    asg.status = "NO_SHOW";
    asg.cancellationReason = "No-Show: " + notes;
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.reportNoShow && asgId && !asgId.startsWith('asg_')) {
        await ApiClient.reportNoShow(asgId, notes);
      }
    } catch (e) {}
  }

  // 11. Submit Multi-Metric Review & Dynamic Trust Recalculation
  async submitReview(reviewData) {
    if (!this.data.reviews) {
      this.data.reviews = [];
    }
    const newRev = {
      id: "rev_" + Date.now(),
      ...reviewData,
      createdAt: new Date().toISOString()
    };
    this.data.reviews.push(newRev);

    // Update user rating
    if (this.data.currentUser) {
      this.data.currentUser.rating = Math.min(5.0, ((this.data.currentUser.rating || 4.8) + reviewData.rating) / 2);
    }
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.submitReview) {
        await ApiClient.submitReview({
          assignmentId: reviewData.assignmentId,
          jobId: reviewData.jobId,
          revieweeId: reviewData.revieweeId,
          rating: reviewData.rating,
          punctualityRating: reviewData.punctualityRating || reviewData.rating,
          qualityRating: reviewData.qualityRating || reviewData.rating,
          behaviorRating: reviewData.behaviorRating || reviewData.rating,
          workManagementRating: reviewData.workManagementRating || reviewData.rating,
          paymentExperienceRating: reviewData.paymentExperienceRating || reviewData.rating,
          timeManagementRating: reviewData.timeManagementRating || reviewData.rating,
          reliabilityRating: reviewData.reliabilityRating || reviewData.rating,
          skillRating: reviewData.skillRating || reviewData.rating,
          overallExperienceRating: reviewData.overallExperienceRating || reviewData.rating,
          reviewText: reviewData.reviewText || reviewData.comment || "उत्कृष्ट काम व सहकार्य"
        });
      }
    } catch (e) {
      console.info("Backend submitReview sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }


  // 8. Post New Job (Provider Action)
  async postJob(newJob) {
    const cur = this.data.currentUser || (typeof AuthManager !== 'undefined' && AuthManager.getCurrentUser()) || {};
    const pId = cur.id || cur.userId || cur.username || "p_1";
    const pName = cur.fullName || cur.name || cur.username || (this.currentRole === "PROVIDER" ? "महेश पाटील (Mahesh Patil)" : "बाळासाहेब पाटील");
    const pUser = cur.username || "";

    const job = {
      id: "job_" + Date.now(),
      title: newJob.title,
      category: newJob.category,
      workType: newJob.workType || newJob.category,
      workModel: newJob.workModel || (newJob.recurring ? "RECURRING" : "ONETIME"),
      providerId: pId,
      providerName: pName,
      providerUsername: pUser,
      creatorId: pId,
      creatorUsername: pUser,
      countryId: newJob.countryId || "IN",
      stateId: newJob.stateId || "state-mh",
      districtId: newJob.districtId || "dist-pune",
      talukaId: newJob.talukaId || "tal-shirur",
      villageId: newJob.villageId || "vil-ranjangaon",
      village: newJob.village || "रांजणगाव (Ranjangaon)",
      taluka: newJob.taluka || "Shirur",
      district: newJob.district || "Pune Rural",
      state: newJob.state || "Maharashtra",
      country: newJob.country || "India",
      distanceKm: 2.1,
      dailyWage: Number(newJob.dailyWage) || 600,
      paymentUnit: newJob.paymentUnit || "PER_DAY",
      overtimeAvailable: Boolean(newJob.overtimeAvailable),
      overtimeRate: Number(newJob.overtimeRate) || 0,
      additionalPaymentConditions: newJob.additionalPaymentConditions || null,
      startTime: newJob.startTime || "08:00:00",
      endTime: newJob.endTime || "17:00:00",
      workingHours: Number(newJob.workingHours) || 8,
      lunchBreak: Number(newJob.lunchBreak) || 60,
      teaBreak: Number(newJob.teaBreak) || 15,
      otherBreak: Number(newJob.otherBreak) || 0,
      facilities: newJob.facilities || "",
      facilityDetails: newJob.facilityDetails || "",
      workersRequired: Number(newJob.workersRequired) || 2,
      workersConfirmed: 0,
      urgent: Boolean(newJob.urgent),
      priority: newJob.urgent ? "URGENT" : "NORMAL",
      status: "OPEN",
      recurring: Boolean(newJob.recurring),
      startDate: newJob.startDate || "आज (Today)",
      deadline: newJob.deadline || null,
      durationDays: Number(newJob.durationDays) || 1,
      desc: newJob.desc || "गावातील स्थानिक काम."
    };

    this.data.jobs.unshift(job);
    this.notify();

    try {
      if (typeof ApiClient !== 'undefined' && ApiClient.createJob) {
        const backendPayload = {
          title: newJob.title,
          category: newJob.category,
          workModel: job.workModel,
          description: newJob.desc || newJob.description || "गावातील स्थानिक काम.",
          dailyWage: job.dailyWage,
          paymentUnit: job.paymentUnit,
          overtimeAvailable: job.overtimeAvailable,
          overtimeRate: job.overtimeRate,
          additionalPaymentConditions: job.additionalPaymentConditions,
          startTime: job.startTime,
          endTime: job.endTime,
          workingHours: job.workingHours,
          lunchBreak: job.lunchBreak,
          teaBreak: job.teaBreak,
          otherBreak: job.otherBreak,
          facilities: job.facilities,
          facilityDetails: job.facilityDetails,
          workersRequired: job.workersRequired,
          priority: job.priority,
          startDate: newJob.startDate && newJob.startDate.includes('-') ? newJob.startDate : null,
          deadline: newJob.deadline && newJob.deadline.includes('-') ? newJob.deadline : null,
          durationDays: job.durationDays,
          village: newJob.village || cur.village || "रांजणगाव (Ranjangaon)",
          taluka: newJob.taluka || cur.taluka || "Shirur",
          district: newJob.district || cur.district || "Pune Rural",
          state: newJob.state || cur.state || "Maharashtra",
          country: newJob.country || cur.country || "India"
        };
        const created = await ApiClient.createJob(backendPayload);
        if (created && created.id) {
          job.id = created.id;
          job.backendId = created.id;
        }
      }
    } catch (e) {
      console.info("Backend createJob sync note:", e.message);
    }

    if (typeof window !== 'undefined' && typeof window.refreshLiveStats === 'function') {
      window.refreshLiveStats();
    }
  }

  // 8b. Hierarchical Match Calculation
  calculateJobMatch(job) {
    if (!job || !this.data.currentUser) return 85;
    const worker = this.data.currentUser;
    let score = 40; // Base presence
    
    // Wage match
    if (!worker.minDailyWage || job.dailyWage >= worker.minDailyWage) score += 20;
    
    // Urgent priority bonus
    if (job.urgent || job.priority === "URGENT") score += 10;
    
    // Hierarchical location affinity
    if (worker.villageId && job.villageId && worker.villageId === job.villageId) {
      score += 30; // Exact same village (Highest local relevance)
    } else if (worker.village && job.village && worker.village === job.village) {
      score += 30;
    } else if (worker.talukaId && job.talukaId && worker.talukaId === job.talukaId) {
      score += 20; // Same taluka
    } else if (worker.taluka && job.taluka && worker.taluka === job.taluka) {
      score += 20;
    } else if (worker.districtId && job.districtId && worker.districtId === job.districtId) {
      score += 10; // Same district
    } else {
      score += 5;
    }

    return Math.min(score, 100);
  }

  // 9. Universal Report Submission
  submitReport(report) {
    this.data.moderationReports.unshift({
      id: "rep_" + Date.now(),
      reporterName: this.data.currentUser.name,
      reportedEntity: report.entity,
      category: report.category,
      reason: report.reason,
      status: "PENDING_REVIEW",
      timestamp: new Date().toISOString().replace('T', ' ').substring(0, 16)
    });
    this.notify();
  }

  // 10. Update Worker Availability Calendar
  updateAvailability(day, isAvail) {
    if (this.data.currentUser.availability && this.data.currentUser.availability.days && this.data.currentUser.availability.days[day] !== undefined) {
      this.data.currentUser.availability.days[day] = isAvail;
      this.syncUserToGlobalDb();

      // Also persist to current session profile
      const storedUserJson = SafeStorage.getItem("kaamsetu_user_profile");
      let storedUser = storedUserJson ? JSON.parse(storedUserJson) : {};
      Object.assign(storedUser, this.data.currentUser);
      SafeStorage.setItem("kaamsetu_user_profile", JSON.stringify(storedUser));

      // Asynchronously sync to backend database if authenticated
      if (typeof ApiClient !== 'undefined' && ApiClient.updateWorkerProfile && typeof AuthManager !== 'undefined' && AuthManager.isAuthenticated && AuthManager.isAuthenticated()) {
        try {
          ApiClient.updateWorkerProfile({
            availabilityDays: JSON.stringify(this.data.currentUser.availability.days)
          }).catch(err => console.info("Backend availability sync note:", err.message));
        } catch (e) {}
      }

      this.notify();
    }
  }

  // 11. Update Worker Profile (Location, Wage, Radius, Skills)
  updateWorkerProfile(profile) {
    if (!this.data.currentUser) {
      this.data.currentUser = {};
    }
    if (profile.fullName) {
      this.data.currentUser.name = profile.fullName;
      this.data.currentUser.fullName = profile.fullName;
    }
    if (profile.mobile) this.data.currentUser.mobile = profile.mobile;
    if (profile.email) this.data.currentUser.email = profile.email;
    if (profile.countryId) this.data.currentUser.countryId = profile.countryId;
    if (profile.stateId) this.data.currentUser.stateId = profile.stateId;
    if (profile.districtId) this.data.currentUser.districtId = profile.districtId;
    if (profile.talukaId) this.data.currentUser.talukaId = profile.talukaId;
    if (profile.villageId) this.data.currentUser.villageId = profile.villageId;
    if (profile.village) this.data.currentUser.village = profile.village;
    if (profile.taluka) this.data.currentUser.taluka = profile.taluka;
    if (profile.district) this.data.currentUser.district = profile.district;
    if (profile.state) this.data.currentUser.state = profile.state;
    if (profile.country) this.data.currentUser.country = profile.country;
    if (profile.minDailyWage !== undefined) this.data.currentUser.minDailyWage = Number(profile.minDailyWage);
    if (profile.travelRadiusKm !== undefined) this.data.currentUser.travelRadiusKm = Number(profile.travelRadiusKm);
    if (profile.experienceYears !== undefined) this.data.currentUser.experienceYears = Number(profile.experienceYears);
    if (profile.bio !== undefined) this.data.currentUser.bio = profile.bio;
    if (profile.skills) {
      this.data.currentUser.skills = Array.isArray(profile.skills) ? profile.skills : [profile.skills];
    }
    if (profile.gender) this.data.currentUser.gender = profile.gender;
    if (profile.avatar) this.data.currentUser.avatar = profile.avatar;
    else if (profile.gender) {
      this.data.currentUser.avatar = profile.gender === 'FEMALE' ? '👷‍♀️' : '👷‍♂️';
    }

    // Permanently sync to localStorage user profile
    const storedUserJson = SafeStorage.getItem("kaamsetu_user_profile");
    let storedUser = storedUserJson ? JSON.parse(storedUserJson) : {};
    Object.assign(storedUser, this.data.currentUser);
    SafeStorage.setItem("kaamsetu_user_profile", JSON.stringify(storedUser));

    // Permanently persist to global user database so edits survive logout & re-login
    this.syncUserToGlobalDb();

    // Asynchronously sync to backend database if authenticated
    if (typeof ApiClient !== 'undefined' && ApiClient.updateWorkerProfile && typeof AuthManager !== 'undefined' && AuthManager.isAuthenticated && AuthManager.isAuthenticated()) {
      try {
        ApiClient.updateWorkerProfile({
          skills: JSON.stringify(this.data.currentUser.skills || []),
          availabilityDays: JSON.stringify(this.data.currentUser.availability?.days || {}),
          minDailyWage: this.data.currentUser.minDailyWage,
          travelRadiusKm: this.data.currentUser.travelRadiusKm,
          experienceYears: this.data.currentUser.experienceYears,
          bio: this.data.currentUser.bio
        }).catch(err => console.info("Backend profile sync note:", err.message));
      } catch (e) {}
    }

    this.notify();
  }

  // 12. Update Job Provider Profile (Name, Business Name, Location, Bio, Type)
  updateProviderProfile(profile) {
    if (!this.data.currentUser) {
      this.data.currentUser = {};
    }
    if (profile.fullName) {
      this.data.currentUser.name = profile.fullName;
      this.data.currentUser.fullName = profile.fullName;
    }
    if (profile.name) {
      this.data.currentUser.name = profile.name;
    }
    if (profile.businessName) {
      this.data.currentUser.businessName = profile.businessName;
    }
    if (profile.mobile) this.data.currentUser.mobile = profile.mobile;
    if (profile.email) this.data.currentUser.email = profile.email;
    if (profile.providerType) this.data.currentUser.providerType = profile.providerType;
    if (profile.gender) this.data.currentUser.gender = profile.gender;
    if (profile.avatar) this.data.currentUser.avatar = profile.avatar;
    else if (profile.gender) {
      this.data.currentUser.avatar = profile.gender === 'FEMALE' ? '👩' : '👨';
    }
    if (profile.countryId) this.data.currentUser.countryId = profile.countryId;
    if (profile.stateId) this.data.currentUser.stateId = profile.stateId;
    if (profile.districtId) this.data.currentUser.districtId = profile.districtId;
    if (profile.talukaId) this.data.currentUser.talukaId = profile.talukaId;
    if (profile.villageId) this.data.currentUser.villageId = profile.villageId;
    if (profile.village) this.data.currentUser.village = profile.village;
    if (profile.taluka) this.data.currentUser.taluka = profile.taluka;
    if (profile.district) this.data.currentUser.district = profile.district;
    if (profile.state) this.data.currentUser.state = profile.state;
    if (profile.country) this.data.currentUser.country = profile.country;
    if (profile.bio !== undefined) this.data.currentUser.bio = profile.bio;
    if (profile.facilities) this.data.currentUser.facilities = Array.isArray(profile.facilities) ? profile.facilities : [profile.facilities];

    // Permanently sync to localStorage user profile
    const storedUserJson = SafeStorage.getItem("kaamsetu_user_profile");
    let storedUser = storedUserJson ? JSON.parse(storedUserJson) : {};
    Object.assign(storedUser, this.data.currentUser);
    SafeStorage.setItem("kaamsetu_user_profile", JSON.stringify(storedUser));

    // Permanently persist to global user database
    this.syncUserToGlobalDb();

    this.notify();
  }

  // Helper to persist user across logout/login in local persistent registry
  syncUserToGlobalDb() {
    try {
      if (!this.data.currentUser) return;
      const u = this.data.currentUser;
      const db = SafeStorage.getJSON('kaamsetu_users_db', {});
      const uKey = String(u.username || u.name || u.fullName || '').trim().toLowerCase();
      if (uKey) db[uKey] = Object.assign({}, db[uKey] || {}, u);
      if (u.id) db[u.id] = Object.assign({}, db[u.id] || {}, u);
      if (u.mobile) {
        const clean = String(u.mobile).replace(/\D/g, '');
        if (clean) db[clean] = Object.assign({}, db[clean] || {}, u);
      }
      if (u.email) {
        db[u.email.toLowerCase()] = Object.assign({}, db[u.email.toLowerCase()] || {}, u);
      }
      SafeStorage.setItem('kaamsetu_users_db', JSON.stringify(db));

      // Also synchronize live matching worker in this.data.workers
      if (this.data.workers && Array.isArray(this.data.workers)) {
        const matchIdx = this.data.workers.findIndex(w => 
          w.id === u.id ||
          (u.username && (w.id === `w_${u.username}` || w.name.toLowerCase().includes(u.username.toLowerCase()))) ||
          w.name.toLowerCase().includes(uKey) ||
          uKey.includes(w.name.toLowerCase()) ||
          (u.mobile && w.mobile && String(u.mobile).replace(/\D/g,'') === String(w.mobile).replace(/\D/g,''))
        );
        if (matchIdx !== -1) {
          const w = this.data.workers[matchIdx];
          if (u.skills) w.skills = u.skills;
          if (u.availability) w.availability = u.availability;
          if (u.minDailyWage !== undefined) w.minWage = u.minDailyWage;
          if (u.travelRadiusKm !== undefined) w.travelRadiusKm = u.travelRadiusKm;
          if (u.experienceYears !== undefined) w.experienceYears = u.experienceYears;
          if (u.village) w.village = u.village;
          if (u.mobile) w.mobile = u.mobile;
          if (u.fullName || u.name) w.name = u.fullName || u.name;
        }
      }
    } catch (e) {}
  }

  // 13. Toggle Worker Skill Chip
  toggleWorkerSkill(skillKey) {
    if (!this.data.currentUser.skills) {
      this.data.currentUser.skills = [];
    }
    const idx = this.data.currentUser.skills.indexOf(skillKey);
    if (idx >= 0) {
      if (this.data.currentUser.skills.length > 1) {
        this.data.currentUser.skills.splice(idx, 1);
      }
    } else {
      this.data.currentUser.skills.push(skillKey);
    }
    this.syncSkillsPersistent();
  }

  // 14. Add New Custom Skill (Permanent across Re-login)
  addWorkerSkill(newSkill) {
    if (!newSkill || !String(newSkill).trim()) return false;
    const cleanSkill = String(newSkill).trim();
    if (!this.data.currentUser.skills) {
      this.data.currentUser.skills = [];
    }
    if (!this.data.currentUser.skills.includes(cleanSkill)) {
      this.data.currentUser.skills.push(cleanSkill);
      this.syncSkillsPersistent();
      return true;
    }
    return false;
  }

  // 15. Delete / Remove Skill (Permanent across Re-login)
  removeWorkerSkill(skillKey) {
    if (!this.data.currentUser.skills) return;
    const idx = this.data.currentUser.skills.indexOf(skillKey);
    if (idx >= 0) {
      this.data.currentUser.skills.splice(idx, 1);
      this.syncSkillsPersistent();
    }
  }

  // Helper to sync skills to localStorage and backend database
  syncSkillsPersistent() {
    this.syncUserToGlobalDb();

    const storedUserJson = SafeStorage.getItem("kaamsetu_user_profile");
    let storedUser = storedUserJson ? JSON.parse(storedUserJson) : {};
    Object.assign(storedUser, this.data.currentUser);
    SafeStorage.setItem("kaamsetu_user_profile", JSON.stringify(storedUser));

    // Asynchronously push to backend database
    if (typeof ApiClient !== 'undefined' && ApiClient.updateWorkerProfile && typeof AuthManager !== 'undefined' && AuthManager.isAuthenticated && AuthManager.isAuthenticated()) {
      try {
        ApiClient.updateWorkerProfile({
          skills: JSON.stringify(this.data.currentUser.skills || [])
        }).catch(err => console.info("Skills backend sync note:", err.message));
      } catch (e) {}
    }
  }

  clearAllJobs() {
    this.data.jobs = [];
    this.data.assignments = [];
    this.save();
    this.notify();
    if (typeof window !== 'undefined' && typeof window.renderApp === 'function') {
      window.renderApp();
    }
  }
}

window.clearAllJobs = function() {
  if (window.appState) {
    window.appState.clearAllJobs();
  }
};

window.appState = new AppState();
