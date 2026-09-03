/**
 * KaamSetu V1 — Phase 12 Hyperlocal Pilot Launch Architecture
 * Pune Rural Belt (Shirur, Purandar/Saswad, Khed/Chakan, Junnar/Alephata, Baramati, Bhor)
 * Village Kiosk Assisted Enrollment & Pilot SLA Governance Engine
 */

(function(window) {
  'use strict';

  const PilotManager = {
    district: "Pune Rural (पुणे ग्रामीण)",
    activeTalukas: [
      { id: "shirur", name: "Shirur", nameMr: "शिरूर", villages: ["Shikrapur (शिक्रापूर)", "Ranjangaon (रांजणगाव)", "Pabal (पाबळ)", "Mandavgan (मांडवगण)"] },
      { id: "purandar", name: "Purandar / Saswad", nameMr: "पुरंदर (सासवड)", villages: ["Saswad (सासवड)", "Jejuri (जेजुरी)", "Belsar (बेलसर)", "Dive (दिवे)"] },
      { id: "khed", name: "Khed / Chakan", nameMr: "खेड (चाकण)", villages: ["Chakan (चाकण)", "Rajgurunagar (राजगुरुनगर)", "Alandi (आळंदी)", "Mahalunge (म्हाळुंगे)"] },
      { id: "junnar", name: "Junnar / Alephata", nameMr: "जुन्नर (आळेफाटा)", villages: ["Alephata (आळेफाटा)", "Otur (ओतूर)", "Narayangaon (नारायणगाव)"] },
      { id: "baramati", name: "Baramati", nameMr: "बारामती", villages: ["Malegaon (माळेगाव)", "Supa (सुपा)", "Someshwar (सोमेश्वर)"] },
      { id: "bhor", name: "Bhor", nameMr: "भोर", villages: ["Nasrapur (नसरापूर)", "Kapurhol (कापूरहोळ)", "Bhor Rural (भोर ग्रामीण)"] }
    ],

    slaMilestones: {
      avgTimeToFirstApplyMin: 18.4,
      targetTimeToFirstApplyMin: 30.0,
      jobFillRatePct: 94.2,
      targetJobFillRatePct: 75.0,
      noShowRatePct: 1.2,
      maxNoShowRatePct: 5.0,
      repeatHiringRatePct: 71.5,
      targetRepeatHiringRatePct: 40.0
    },

    openVillageSelectorModal() {
      const modal = document.getElementById("generic-modal");
      const title = document.getElementById("modal-title");
      const body = document.getElementById("modal-body");

      title.innerText = "📍 पुणे ग्रामीण पायलट गाव निवडा (Select Village)";
      body.innerHTML = `
        <div style="display: flex; flex-direction: column; gap: 1rem;">
          <p style="font-size: 0.88rem; color: var(--text-muted);">
            पुणे जिल्ह्यातील ५-१० पायलट गावांसाठी थेट स्थानिक रोजगार यादी:
          </p>
          <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 0.5rem; max-height: 280px; overflow-y: auto;">
            ${this.activeTalukas.flatMap(t => t.villages.map(v => `
              <button class="btn btn-outline" style="font-size: 0.82rem; padding: 0.5rem; text-align: center; justify-content: center;" onclick="window.PilotManager.selectVillage('${v}')">
                📍 ${v}
              </button>
            `)).join('')}
          </div>
          <div style="display: flex; justify-content: flex-end; margin-top: 0.5rem;">
            <button class="btn btn-outline" onclick="closeModal()">मागे</button>
          </div>
        </div>
      `;
      modal.classList.add("active");
    },

    selectVillage(villageName) {
      if (window.appState) {
        window.appState.data.selectedPilotVillage = villageName;
        window.appState.notify();
      }
      closeModal();
      if (typeof showToast === 'function') {
        showToast(`📍 गाव निवडले: ${villageName} (स्थानिक कामे लोड झाली)`);
      }
    },

    openKioskAssistedRegistrationModal() {
      const modal = document.getElementById("generic-modal");
      const title = document.getElementById("modal-title");
      const body = document.getElementById("modal-body");

      title.innerText = "🏛️ ग्रामपंचायत / फील्ड एजंट जलद नोंदणी (Kiosk Mode)";
      body.innerHTML = `
        <div style="display: flex; flex-direction: column; gap: 0.85rem;">
          <p style="font-size: 0.82rem; color: var(--text-muted);">
            अल्प-शिक्षित कामगारांसाठी ग्रामपंचायत किंवा सेतू केंद्रावरून १-टॅप नोंदणी:
          </p>
          <div class="form-group">
            <label class="form-label">कामगाराचे नाव (Full Name) *</label>
            <input type="text" id="kiosk-worker-name" class="form-input" placeholder="">
          </div>
          <div class="form-group">
            <label class="form-label">मोबाईल नंबर (Mobile Number) *</label>
            <input type="tel" id="kiosk-worker-mobile" class="form-input" placeholder="">
          </div>
          <div class="form-group">
            <label class="form-label">मुख्य कामाचा प्रकार (Primary Skill) *</label>
            <select id="kiosk-worker-skill" class="form-input form-select">
              <option value="शेती काम (Agriculture)">🌾 शेती काम (खुरपणी/काढणी/फवारणी)</option>
              <option value="बांधकाम व गवंडी (Construction)">🧱 बांधकाम व गवंडी काम</option>
              <option value="ट्रॅक्टर / ड्रायव्हिंग (Driving)">🚜 ट्रॅक्टर / पिकअप ड्रायव्हर</option>
              <option value="घरकाम / स्वयंपाक (Household)">🧹 घरकाम व मदतनीस</option>
              <option value="स्थानिक दुरुस्ती (Repairs)">🔧 प्लंबिंग / इलेक्ट्रिशियन</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">पायलट गाव (Pilot Village) *</label>
            <select id="kiosk-worker-village" class="form-input form-select">
              <option value="शिक्रापूर (Shikrapur)">📍 शिक्रापूर (शिरूर)</option>
              <option value="सासवड (Saswad)">📍 सासवड (पुरंदर)</option>
              <option value="चाकण (Chakan)">📍 चाकण (खेड)</option>
              <option value="आळेफाटा (Alephata)">📍 आळेफाटा (जुन्नर)</option>
              <option value="माळेगाव (Malegaon)">📍 माळेगाव (बारामती)</option>
              <option value="नसरापूर (Nasrapur)">📍 नसरापूर (भोर)</option>
            </select>
          </div>
          <div style="display: flex; gap: 0.5rem; margin-top: 0.5rem;">
            <button class="btn btn-primary btn-block" onclick="window.PilotManager.submitKioskRegistration()">
              ✓ त्वरित नोंदणी करा व बॅज द्या
            </button>
            <button class="btn btn-outline" onclick="closeModal()">रद्द</button>
          </div>
        </div>
      `;
      modal.classList.add("active");
    },

    submitKioskRegistration() {
      const name = document.getElementById("kiosk-worker-name")?.value;
      const mobile = document.getElementById("kiosk-worker-mobile")?.value;
      const skill = document.getElementById("kiosk-worker-skill")?.value;
      const village = document.getElementById("kiosk-worker-village")?.value;

      if (!name || !mobile) {
        alert("कृपया नाव व मोबाईल नंबर टाका.");
        return;
      }

      if (window.appState && window.appState.data) {
        if (!window.appState.data.workers) window.appState.data.workers = [];
        const newWorker = {
          id: "w_kiosk_" + Date.now(),
          name,
          mobile: `+91 ${mobile}`,
          skills: [skill],
          village,
          taluka: village.split('(')[1]?.replace(')', '') || 'Pune',
          distance: "0.8 km",
          rating: 5.0,
          jobsCompleted: 0,
          verified: true,
          badge: "🏛️ ग्रामपंचायत प्रमाणित (Kiosk Verified)",
          trustStatus: "HEALTHY"
        };
        window.appState.data.workers.unshift(newWorker);
        window.appState.notify();
      }

      closeModal();
      if (typeof showToast === 'function') {
        showToast(`🎉 कामगार ${name} ची ग्रामपंचायत नोंदणी यशस्वी!`);
      }
    }
  };

  window.PilotManager = PilotManager;
  window.openPilotVillageSelectorModal = function() {
    PilotManager.openVillageSelectorModal();
  };
  window.openKioskAssistedRegistrationModal = function() {
    PilotManager.openKioskAssistedRegistrationModal();
  };
})(window);
