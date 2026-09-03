/**
 * KaamSetu V1 — Phase 11 Security, Privacy & Compliance Architecture
 * Client-Side Hardening, XSS Sanitization, Masked Privacy & Audit Export Engine
 */

(function(window) {
  'use strict';

  const SecurityManager = {
    /**
     * Sanitize user-provided text inputs against XSS injections
     */
    sanitizeText(input) {
      if (typeof input !== 'string') return input;
      return input
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#x27;')
        .replace(/\//g, '&#x2F;')
        .replace(/javascript:/gi, '')
        .replace(/data:/gi, '');
    },

    /**
     * Mask mobile number to preserve privacy until mutual confirmation
     * e.g., +91 98220 12345 -> +91 98*** **345
     */
    maskPhoneNumber(phone) {
      if (!phone) return '**********';
      const clean = phone.replace(/[^0-9]/g, '');
      if (clean.length >= 10) {
        const start = clean.substring(0, 2);
        const end = clean.substring(clean.length - 3);
        return `+91 ${start}*** ***${end}`;
      }
      return '🔒 फोन नंबर सुरक्षित (Masked)';
    },

    /**
     * Client-side Audit Log Export as CSV or JSON format
     */
    exportAuditLogs(format = 'json') {
      const logs = (window.appState && window.appState.data && window.appState.data.auditLogs) || [];
      if (logs.length === 0) {
        alert("कोणत्याही ऑडिट नोंदी उपलब्ध नाहीत. (No logs to export)");
        return;
      }

      let dataStr = '';
      let filename = `kaamsetu_audit_export_${Date.now()}.${format}`;
      let mimeType = '';

      if (format === 'csv') {
        mimeType = 'text/csv;charset=utf-8;';
        const headers = ['ID', 'Actor', 'Event', 'Target', 'Status', 'Time', 'IP', 'Details'];
        const rows = logs.map(l => [
          l.id,
          `"${l.actor || 'SYSTEM'}"`,
          `"${l.event || ''}"`,
          `"${(l.target || '').replace(/"/g, '""')}"`,
          `"${l.status || ''}"`,
          `"${l.time || ''}"`,
          `"${l.ip || '127.0.0.1'}"`,
          `"${(l.details || '').replace(/"/g, '""')}"`
        ].join(','));
        dataStr = [headers.join(','), ...rows].join('\n');
      } else {
        mimeType = 'application/json;charset=utf-8;';
        dataStr = JSON.stringify(logs, null, 2);
      }

      const blob = new Blob([dataStr], { type: mimeType });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.setAttribute('href', url);
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    },

    /**
     * Session Token Validation & Expiry Check
     */
    /**
     * Session Token Validation & Expiry Check
     */
    validateTokenSession() {
      const token = (window.SafeStorage ? window.SafeStorage.getItem('kaamsetu_token') : localStorage.getItem('kaamsetu_token'));
      if (!token) return { valid: false, expired: true };
      try {
        const payloadBase64 = token.split('.')[1];
        if (!payloadBase64) return { valid: false, expired: true };
        const decoded = JSON.parse(atob(payloadBase64));
        const now = Math.floor(Date.now() / 1000);
        if (decoded.exp && decoded.exp < now) {
          return { valid: false, expired: true };
        }
        return { valid: true, expired: false, user: decoded };
      } catch (e) {
        return { valid: false, expired: true };
      }
    }
  };

  /**
   * SafeStorage: Resilient LocalStorage manager with quota auto-recovery and memory fallback
   */
  const SafeStorage = (function() {
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

  window.SafeStorage = SafeStorage;
  window.SecurityManager = SecurityManager;
  window.exportAdminAuditLogs = function() {
    SecurityManager.exportAuditLogs('csv');
  };
})(window);
