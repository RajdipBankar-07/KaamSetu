/**
 * 🌾 KaamSetu (कामसेतू) - Hierarchical Dependent Location Selector Component
 * Controls cascading: Country -> State -> District -> Taluka/Sub-District -> Village
 * Features:
 * - Complete 36 Indian States and Union Territories
 * - Nationwide District & Sub-District hierarchy
 * - Searchable Village Selection with instant real-time filtering within selected Taluka
 * - Cascading dependency rules and strict reset rules
 * - Multilingual translation (English, Marathi, Hindi)
 * - Mobile responsive 320px–480px touch controls
 */

class LocationSelectorComponent {
  /**
   * @param {Object} options
   * @param {string} options.containerId - DOM element ID to render the component into
   * @param {string} options.prefix - Unique prefix for input IDs (e.g. 'reg', 'profile', 'job')
   * @param {Object} [options.initialValues] - { countryId, stateId, districtId, talukaId, villageId }
   * @param {Function} [options.onChange] - Callback fired on any selection change
   */
  constructor(options) {
    this.containerId = options.containerId;
    this.prefix = options.prefix || 'loc';
    this.onChangeCallback = options.onChange || null;
    this.initialValues = options.initialValues || {
      countryId: 'IN',
      stateId: 'state-mh',
      districtId: 'dist-pune',
      talukaId: 'tal-shirur',
      villageId: 'vil-ranjangaon'
    };

    this.state = {
      countries: [],
      states: [],
      districts: [],
      talukas: [],
      villages: [],
      selectedCountry: '',
      selectedState: '',
      selectedDistrict: '',
      selectedTaluka: '',
      selectedVillage: '',
      villageSearchQuery: '',
      loadingField: null,
      errorField: null
    };

    this.init();
  }

  getLang() {
    return window.i18n?.currentLang || 'mr';
  }

  t(key, fallback) {
    return window.i18n?.t(key) || fallback;
  }

  getLocalizedName(item) {
    if (!item) return '';
    const lang = this.getLang();
    if (lang === 'mr' && item.nameMr) return `${item.nameMr} (${item.name})`;
    if (lang === 'hi' && item.nameHi) return `${item.nameHi} (${item.name})`;
    return item.name || '';
  }

  getLocalizedCountryName() {
    const lang = this.getLang();
    if (lang === 'mr') return 'भारत (India)';
    if (lang === 'hi') return 'भारत (India)';
    return 'India (भारत)';
  }

  getFilteredVillages() {
    if (!this.state.villageSearchQuery || !this.state.villageSearchQuery.trim()) {
      return this.state.villages;
    }
    const q = this.state.villageSearchQuery.toLowerCase().trim();
    return this.state.villages.filter(v => 
      (v.name && v.name.toLowerCase().includes(q)) ||
      (v.nameEn && v.nameEn.toLowerCase().includes(q)) ||
      (v.nameMr && v.nameMr.toLowerCase().includes(q)) ||
      (v.nameHi && v.nameHi.toLowerCase().includes(q)) ||
      (v.pinCode && v.pinCode.includes(q))
    );
  }

  async init() {
    this.renderSkeleton();
    await this.loadCountries();
    
    // Always default Country to 'IN' (India)
    const countryToSelect = this.initialValues.countryId || 'IN';
    this.state.selectedCountry = countryToSelect;
    await this.loadStates(countryToSelect);

    if (this.initialValues.stateId) {
      this.state.selectedState = this.initialValues.stateId;
      await this.loadDistricts(this.initialValues.stateId);

      if (this.initialValues.districtId) {
        this.state.selectedDistrict = this.initialValues.districtId;
        await this.loadTalukas(this.initialValues.districtId);

        if (this.initialValues.talukaId || this.initialValues.subDistrictId) {
          const subId = this.initialValues.talukaId || this.initialValues.subDistrictId;
          this.state.selectedTaluka = subId;
          await this.loadVillages(subId);

          if (this.initialValues.villageId) {
            this.state.selectedVillage = this.initialValues.villageId;
          }
        }
      }
    }
    this.render();
  }

  renderSkeleton() {
    const container = document.getElementById(this.containerId);
    if (!container) return;
    container.innerHTML = `
      <div class="location-selector-wrapper" style="display: flex; flex-direction: column; gap: 0.65rem;">
        <div style="font-size: 0.78rem; color: #64748b; font-style: italic;">
          ${this.t('location.loading', 'ठिकाणे लोड होत आहेत...')}
        </div>
      </div>
    `;
  }

  render() {
    const container = document.getElementById(this.containerId);
    if (!container) return;

    const p = this.prefix;
    const isStateDisabled = false; // State is directly selectable since Country is automatically India
    const isDistrictDisabled = !this.state.selectedState;
    const isTalukaDisabled = !this.state.selectedDistrict;
    const isVillageDisabled = !this.state.selectedTaluka;

    container.innerHTML = `
      <div class="location-selector-wrapper" style="display: flex; flex-direction: column; gap: 0.65rem; background: #f8fafc; border: 1.5px solid #e2e8f0; border-radius: 12px; padding: 0.85rem 0.95rem; box-sizing: border-box;">
        
        <!-- Header Badge -->
        <div style="display: flex; justify-content: space-between; align-items: center; padding-bottom: 0.4rem; border-bottom: 1px dashed #cbd5e1;">
          <span style="font-size: 0.82rem; font-weight: 800; color: #0d6840; display: flex; align-items: center; gap: 0.3rem;">
            <span>📍</span> <span>स्थान तपशील (Location Details)</span>
          </span>
          <span style="font-size: 0.72rem; font-weight: 700; background: #ecfdf5; color: #047857; padding: 2px 8px; border-radius: 9999px; border: 1px solid #a7f3d0;">
            🇮🇳 All India Active
          </span>
        </div>

        <!-- Row 1: Country (Fixed India) & State (Selectable) -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.6rem;">
          <div>
            <label class="form-label" style="font-size: 0.78rem; font-weight: 700; color: #1e293b; margin-bottom: 0.2rem; display: block;">
              🌍 ${this.t('location.country', 'देश (Country)')} *
            </label>
            <div style="display: flex; align-items: center; gap: 0.5rem; height: 44px; padding: 0.5rem 0.75rem; background: #ffffff; border: 1.5px solid #cbd5e1; border-radius: 8px; font-weight: 700; color: #0f172a; font-size: 0.88rem; box-sizing: border-box; user-select: none;">
              <span style="font-size: 1.2rem; line-height: 1;">🇮🇳</span>
              <span style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${this.getLocalizedCountryName()}</span>
            </div>
            <input type="hidden" id="${p}-country-select" value="IN">
          </div>

          <div>
            <label class="form-label" style="font-size: 0.78rem; font-weight: 700; color: #1e293b; margin-bottom: 0.2rem; display: block;">
              🏛️ ${this.t('location.state', 'राज्य (State / UT)')} *
            </label>
            <select id="${p}-state-select" class="form-input location-select" style="font-size: 0.88rem; height: 44px; min-height: 44px; border-radius: 8px; width: 100%;">
              <option value="">-- ${this.t('location.selectState', 'राज्य निवडा (Select State)')} --</option>
              ${this.state.states.map(s => `
                <option value="${s.id}" ${this.state.selectedState === s.id ? 'selected' : ''}>
                  ${this.getLocalizedName(s)}
                </option>
              `).join('')}
            </select>
          </div>
        </div>

        <!-- Row 2: District & Taluka/Sub-District -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 0.6rem;">
          <div>
            <label class="form-label" style="font-size: 0.78rem; font-weight: 700; color: #1e293b; margin-bottom: 0.2rem; display: block;">
              📍 ${this.t('location.district', 'जिल्हा (District)')} *
            </label>
            <select id="${p}-district-select" class="form-input location-select" ${isDistrictDisabled ? 'disabled' : ''} style="font-size: 0.88rem; height: 44px; min-height: 44px; border-radius: 8px; width: 100%; ${isDistrictDisabled ? 'background: #f8fafc; border: 1.5px solid #e2e8f0; color: #94a3b8; cursor: not-allowed; opacity: 0.75;' : 'background: #ffffff; border: 1.5px solid #cbd5e1;'}">
              <option value="">-- ${isDistrictDisabled ? this.t('location.selectStateFirst', 'प्रथम राज्य निवडा') : this.t('location.selectDistrict', 'जिल्हा निवडा (Select District)')} --</option>
              ${this.state.districts.map(d => `
                <option value="${d.id}" ${this.state.selectedDistrict === d.id ? 'selected' : ''}>
                  ${this.getLocalizedName(d)}
                </option>
              `).join('')}
            </select>
          </div>

          <div>
            <label class="form-label" style="font-size: 0.78rem; font-weight: 700; color: #1e293b; margin-bottom: 0.2rem; display: block;">
              🏙️ ${this.t('location.taluka', 'तालुका / तहसील (Taluka)')} *
            </label>
            <select id="${p}-taluka-select" class="form-input location-select" ${isTalukaDisabled ? 'disabled' : ''} style="font-size: 0.88rem; height: 44px; min-height: 44px; border-radius: 8px; width: 100%; ${isTalukaDisabled ? 'background: #f8fafc; border: 1.5px solid #e2e8f0; color: #94a3b8; cursor: not-allowed; opacity: 0.75;' : 'background: #ffffff; border: 1.5px solid #cbd5e1;'}">
              <option value="">-- ${isTalukaDisabled ? this.t('location.selectDistrictFirst', 'प्रथम जिल्हा निवडा') : this.t('location.selectTaluka', 'तालुका निवडा (Select Taluka)')} --</option>
              ${this.state.talukas.map(t => `
                <option value="${t.id}" ${this.state.selectedTaluka === t.id ? 'selected' : ''}>
                  ${this.getLocalizedName(t)}
                </option>
              `).join('')}
            </select>
          </div>
        </div>

        <!-- Row 3: Village with Searchable Filtering -->
        <div>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.2rem;">
            <label class="form-label" style="font-size: 0.78rem; font-weight: 700; color: #1e293b; margin: 0;">
              🌾 ${this.t('location.village', 'गाव (Village)')} *
            </label>
            ${this.state.loadingField === 'villages' ? `
              <span style="font-size: 0.72rem; color: #0d6840; font-weight: 700;">⏳ लोड होत आहे...</span>
            ` : ''}
          </div>

          ${!isVillageDisabled && this.state.villages.length > 3 ? `
            <div style="margin-bottom: 0.35rem;">
              <input id="${p}-village-search" type="text" class="form-input" placeholder="🔍 ${this.t('location.searchVillage', 'गाव शोधा...')}" value="${this.state.villageSearchQuery || ''}" style="font-size: 0.85rem; height: 38px; min-height: 38px; padding: 0.35rem 0.65rem; border-radius: 8px; background: #ffffff; width: 100%;">
            </div>
          ` : ''}

          <select id="${p}-village-select" class="form-input location-select" ${isVillageDisabled ? 'disabled' : ''} style="font-size: 0.88rem; font-weight: 600; height: 44px; min-height: 44px; border-radius: 8px; width: 100%; ${isVillageDisabled ? 'background: #f8fafc; border: 1.5px solid #e2e8f0; color: #94a3b8; cursor: not-allowed; opacity: 0.75;' : 'background: #ffffff; border: 1.5px solid #cbd5e1; color: #0f172a;'}">
            <option value="">-- ${isVillageDisabled ? this.t('location.selectTalukaFirst', 'प्रथम तालुका निवडा') : this.t('location.selectVillage', 'गाव निवडा (Select Village)')} --</option>
            ${this.getFilteredVillages().map(v => `
              <option value="${v.id}" ${this.state.selectedVillage === v.id ? 'selected' : ''}>
                ${this.getLocalizedName(v)} ${v.pinCode ? `[${v.pinCode}]` : ''}
              </option>
            `).join('')}
          </select>

          <!-- Empty or Error Feedback (Only show when Taluka is actively selected) -->
          ${Boolean(this.state.selectedTaluka) && this.state.villages.length === 0 && !this.state.loadingField ? `
            <div style="font-size: 0.75rem; color: #b45309; margin-top: 0.25rem; font-weight: 600;">
              ⚠️ ${this.t('location.emptyVillages', 'या तालुक्यासाठी गावे उपलब्ध नाहीत')}
            </div>
          ` : ''}

          ${this.state.errorField ? `
            <div style="font-size: 0.75rem; color: #dc2626; margin-top: 0.25rem; display: flex; align-items: center; gap: 0.4rem;">
              <span>❌ ${this.t('location.errorLoading', 'ठिकाणे लोड करण्यात त्रुटी.')}</span>
              <button type="button" class="btn btn-outline" style="font-size: 0.7rem; padding: 0.15rem 0.4rem;" onclick="window.activeLocationSelectors['${this.containerId}']?.retryLastAction()">
                🔄 ${this.t('location.retry', 'पुन्हा प्रयत्न करा')}
              </button>
            </div>
          ` : ''}
        </div>
      </div>
    `;

    this.bindEvents();
  }

  bindEvents() {
    const p = this.prefix;

    // 1. Country Change
    const countrySelect = document.getElementById(`${p}-country-select`);
    if (countrySelect) {
      countrySelect.addEventListener('change', async (e) => {
        const countryId = e.target.value;
        this.state.selectedCountry = countryId;
        // Cascade Reset: Country change resets State, District, Taluka, Village
        this.state.selectedState = '';
        this.state.selectedDistrict = '';
        this.state.selectedTaluka = '';
        this.state.selectedVillage = '';
        this.state.districts = [];
        this.state.talukas = [];
        this.state.villages = [];
        this.state.villageSearchQuery = '';
        
        if (countryId) {
          await this.loadStates(countryId);
        } else {
          this.state.states = [];
        }
        this.render();
        this.notifyChange();
      });
    }

    // 2. State Change
    const stateSelect = document.getElementById(`${p}-state-select`);
    if (stateSelect) {
      stateSelect.addEventListener('change', async (e) => {
        const stateId = e.target.value;
        this.state.selectedState = stateId;
        // Cascade Reset: State change resets District, Taluka, Village
        this.state.selectedDistrict = '';
        this.state.selectedTaluka = '';
        this.state.selectedVillage = '';
        this.state.talukas = [];
        this.state.villages = [];
        this.state.villageSearchQuery = '';

        if (stateId) {
          await this.loadDistricts(stateId);
        } else {
          this.state.districts = [];
        }
        this.render();
        this.notifyChange();
      });
    }

    // 3. District Change
    const districtSelect = document.getElementById(`${p}-district-select`);
    if (districtSelect) {
      districtSelect.addEventListener('change', async (e) => {
        const districtId = e.target.value;
        this.state.selectedDistrict = districtId;
        // Cascade Reset: District change resets Taluka and Village
        this.state.selectedTaluka = '';
        this.state.selectedVillage = '';
        this.state.villages = [];
        this.state.villageSearchQuery = '';

        if (districtId) {
          await this.loadTalukas(districtId);
        } else {
          this.state.talukas = [];
        }
        this.render();
        this.notifyChange();
      });
    }

    // 4. Taluka Change
    const talukaSelect = document.getElementById(`${p}-taluka-select`);
    if (talukaSelect) {
      talukaSelect.addEventListener('change', async (e) => {
        const talukaId = e.target.value;
        this.state.selectedTaluka = talukaId;
        // Cascade Reset: Taluka change resets Village
        this.state.selectedVillage = '';
        this.state.villageSearchQuery = '';

        if (talukaId) {
          await this.loadVillages(talukaId);
        } else {
          this.state.villages = [];
        }
        this.render();
        this.notifyChange();
      });
    }

    // 5. Village Change
    const villageSelect = document.getElementById(`${p}-village-select`);
    if (villageSelect) {
      villageSelect.addEventListener('change', (e) => {
        this.state.selectedVillage = e.target.value;
        this.notifyChange();
      });
    }

    // 6. Village Search Query Filter
    const villageSearch = document.getElementById(`${p}-village-search`);
    if (villageSearch) {
      villageSearch.addEventListener('input', (e) => {
        this.state.villageSearchQuery = e.target.value;
        const select = document.getElementById(`${p}-village-select`);
        if (select) {
          const filtered = this.getFilteredVillages();
          select.innerHTML = `
            <option value="">-- ${this.t('location.selectVillage', 'गाव निवडा (Select Village)')} --</option>
            ${filtered.map(v => `
              <option value="${v.id}" ${this.state.selectedVillage === v.id ? 'selected' : ''}>
                ${this.getLocalizedName(v)} ${v.pinCode ? `[${v.pinCode}]` : ''}
              </option>
            `).join('')}
          `;
        }
      });
    }
  }

  async loadCountries() {
    try {
      this.state.loadingField = 'countries';
      this.state.errorField = null;
      if (window.ApiClient && typeof window.ApiClient.getCountries === 'function') {
        const res = await window.ApiClient.getCountries();
        this.state.countries = res.length ? res : (window.locationMasterData?.countries || []);
      } else {
        this.state.countries = window.locationMasterData?.countries || [];
      }
    } catch (err) {
      console.warn('[LocationSelector] loadCountries fallback to master:', err);
      this.state.countries = window.locationMasterData?.countries || [];
    } finally {
      this.state.loadingField = null;
    }
  }

  async loadStates(countryId) {
    try {
      this.state.loadingField = 'states';
      this.state.errorField = null;
      if (window.ApiClient && typeof window.ApiClient.getStates === 'function') {
        const res = await window.ApiClient.getStates(countryId);
        this.state.states = res.length ? res : (window.locationMasterData?.states || []);
      } else {
        const master = window.locationMasterData?.states || [];
        this.state.states = master.filter(s => s.countryId === countryId);
      }
    } catch (err) {
      console.warn('[LocationSelector] loadStates fallback:', err);
      const master = window.locationMasterData?.states || [];
      this.state.states = master.filter(s => s.countryId === countryId);
    } finally {
      this.state.loadingField = null;
    }
  }

  async loadDistricts(stateId) {
    try {
      this.state.loadingField = 'districts';
      this.state.errorField = null;
      if (window.ApiClient && typeof window.ApiClient.getDistricts === 'function') {
        const res = await window.ApiClient.getDistricts(stateId);
        this.state.districts = res.length ? res : (window.locationMasterData?.districts || []).filter(d => d.stateId === stateId);
      } else {
        const master = window.locationMasterData?.districts || [];
        this.state.districts = master.filter(d => d.stateId === stateId);
      }
    } catch (err) {
      console.warn('[LocationSelector] loadDistricts fallback:', err);
      const master = window.locationMasterData?.districts || [];
      this.state.districts = master.filter(d => d.stateId === stateId);
    } finally {
      this.state.loadingField = null;
    }
  }

  async loadTalukas(districtId) {
    try {
      this.state.loadingField = 'talukas';
      this.state.errorField = null;
      if (window.ApiClient && typeof window.ApiClient.getSubDistricts === 'function') {
        const res = await window.ApiClient.getSubDistricts(districtId);
        this.state.talukas = res.length ? res : (window.locationMasterData?.subDistricts || window.locationMasterData?.talukas || []).filter(t => t.districtId === districtId);
      } else if (window.ApiClient && typeof window.ApiClient.getTalukas === 'function') {
        const res = await window.ApiClient.getTalukas(districtId);
        this.state.talukas = res.length ? res : (window.locationMasterData?.talukas || []).filter(t => t.districtId === districtId);
      } else {
        const master = window.locationMasterData?.subDistricts || window.locationMasterData?.talukas || [];
        this.state.talukas = master.filter(t => t.districtId === districtId);
      }
    } catch (err) {
      console.warn('[LocationSelector] loadTalukas fallback:', err);
      const master = window.locationMasterData?.subDistricts || window.locationMasterData?.talukas || [];
      this.state.talukas = master.filter(t => t.districtId === districtId);
    } finally {
      this.state.loadingField = null;
    }
  }

  async loadVillages(talukaId) {
    if (!talukaId) {
      this.state.villages = [];
      this.state.loadingField = null;
      return;
    }
    try {
      this.state.loadingField = 'villages';
      this.state.errorField = null;
      if (window.ApiClient && typeof window.ApiClient.getVillages === 'function') {
        const res = await window.ApiClient.getVillages(talukaId, this.state.villageSearchQuery || '');
        if (Array.isArray(res) && res.length > 0) {
          this.state.villages = res;
        } else {
          const master = window.locationMasterData?.villages || [];
          this.state.villages = master.filter(v => v.subDistrictId === talukaId || v.talukaId === talukaId);
        }
      } else {
        const master = window.locationMasterData?.villages || [];
        this.state.villages = master.filter(v => v.subDistrictId === talukaId || v.talukaId === talukaId);
      }
    } catch (err) {
      console.warn('[LocationSelector] loadVillages error:', err);
      const master = window.locationMasterData?.villages || [];
      this.state.villages = master.filter(v => v.subDistrictId === talukaId || v.talukaId === talukaId);
    } finally {
      this.state.loadingField = null;
    }
  }

  async retryLastAction() {
    if (this.state.selectedTaluka) {
      await this.loadVillages(this.state.selectedTaluka);
      this.render();
    }
  }

  notifyChange() {
    const val = this.getLocationValue();
    if (typeof this.onChangeCallback === 'function') {
      this.onChangeCallback(val);
    }
  }

  getLocationValue() {
    const country = this.state.countries.find(c => c.id === this.state.selectedCountry);
    const state = this.state.states.find(s => s.id === this.state.selectedState);
    const district = this.state.districts.find(d => d.id === this.state.selectedDistrict);
    const taluka = this.state.talukas.find(t => t.id === this.state.selectedTaluka);
    const village = this.state.villages.find(v => v.id === this.state.selectedVillage);

    return {
      countryId: this.state.selectedCountry,
      stateId: this.state.selectedState,
      districtId: this.state.selectedDistrict,
      subDistrictId: this.state.selectedTaluka,
      talukaId: this.state.selectedTaluka,
      villageId: this.state.selectedVillage,
      country: country ? country.name : '',
      state: state ? state.name : '',
      district: district ? district.name : '',
      subDistrict: taluka ? taluka.name : '',
      taluka: taluka ? taluka.name : '',
      village: village ? this.getLocalizedName(village) : '',
      villageRawName: village ? village.name : ''
    };
  }

  async setLocationValue(loc) {
    if (!loc) return;
    if (loc.countryId) {
      this.state.selectedCountry = loc.countryId;
      await this.loadStates(loc.countryId);
    }
    if (loc.stateId) {
      this.state.selectedState = loc.stateId;
      await this.loadDistricts(loc.stateId);
    }
    if (loc.districtId) {
      this.state.selectedDistrict = loc.districtId;
      await this.loadTalukas(loc.districtId);
    }
    const subId = loc.subDistrictId || loc.talukaId;
    if (subId) {
      this.state.selectedTaluka = subId;
      await this.loadVillages(subId);
    }
    if (loc.villageId) {
      this.state.selectedVillage = loc.villageId;
    }
    this.render();
  }
}

// Global Registry for Active Location Selectors
window.activeLocationSelectors = window.activeLocationSelectors || {};

window.initHierarchicalLocationSelector = function(options) {
  const instance = new LocationSelectorComponent(options);
  window.activeLocationSelectors[options.containerId] = instance;
  return instance;
};

if (typeof module !== 'undefined' && module.exports) {
  module.exports = { LocationSelectorComponent };
}
