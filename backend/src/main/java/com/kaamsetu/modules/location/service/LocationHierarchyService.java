package com.kaamsetu.modules.location.service;

import com.kaamsetu.modules.location.entity.*;
import com.kaamsetu.modules.location.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationHierarchyService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final SubDistrictRepository subDistrictRepository;
    private final TalukaRepository talukaRepository;
    private final VillageRepository villageRepository;

    @Transactional(readOnly = true)
    public List<CountryEntity> getCountries() {
        return countryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<StateEntity> getStatesByCountryId(String countryId) {
        if (countryId == null || countryId.trim().isEmpty()) {
            return List.of();
        }
        return stateRepository.findByCountryIdOrderByNameAsc(countryId.trim());
    }

    @Transactional(readOnly = true)
    public List<DistrictEntity> getDistrictsByStateId(String stateId) {
        if (stateId == null || stateId.trim().isEmpty()) {
            return List.of();
        }
        return districtRepository.findByStateIdOrderByNameAsc(stateId.trim());
    }

    @Transactional(readOnly = true)
    public List<SubDistrictEntity> getSubDistrictsByDistrictId(String districtId) {
        if (districtId == null || districtId.trim().isEmpty()) {
            return List.of();
        }
        return subDistrictRepository.findByDistrictIdOrderByNameAsc(districtId.trim());
    }

    @Transactional(readOnly = true)
    public List<TalukaEntity> getTalukasByDistrictId(String districtId) {
        if (districtId == null || districtId.trim().isEmpty()) {
            return List.of();
        }
        return talukaRepository.findByDistrictIdOrderByNameAsc(districtId.trim());
    }

    @Transactional(readOnly = true)
    public List<VillageEntity> getVillagesBySubDistrictId(String subDistrictId, String search) {
        if (subDistrictId == null || subDistrictId.trim().isEmpty()) {
            return List.of();
        }
        String cleanId = subDistrictId.trim();
        if (search != null && !search.trim().isEmpty()) {
            return villageRepository.searchVillages(cleanId, search.trim());
        }
        return villageRepository.findBySubDistrictIdOrTalukaId(cleanId);
    }

    @Transactional(readOnly = true)
    public List<VillageEntity> getVillagesByTalukaId(String talukaId) {
        return getVillagesBySubDistrictId(talukaId, null);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFullHierarchy() {
        Map<String, Object> result = new HashMap<>();
        result.put("countries", countryRepository.findAll());
        result.put("states", stateRepository.findAll());
        result.put("districts", districtRepository.findAll());
        result.put("subDistricts", subDistrictRepository.findAll());
        result.put("villages", villageRepository.findAll());
        return result;
    }

    /**
     * Strict validation of the 5-level location hierarchy.
     * Prevents mismatched country, state, district, sub-district, and village IDs.
     */
    @Transactional(readOnly = true)
    public void validateHierarchy(String countryId, String stateId, String districtId, String subDistrictOrTalukaId, String villageId) {
        if (countryRepository == null) return;
        if (countryId != null && !countryId.trim().isEmpty()) {
            CountryEntity country = countryRepository.findById(countryId.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Country ID: " + countryId));

            if (stateId != null && !stateId.trim().isEmpty()) {
                StateEntity state = stateRepository.findById(stateId.trim())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid State ID: " + stateId));

                if (!state.getCountryId().equalsIgnoreCase(country.getId()) && !state.getCountryId().equalsIgnoreCase(country.getCode())) {
                    throw new IllegalArgumentException("State '" + state.getName() + "' does not belong to Country '" + country.getName() + "'");
                }

                if (districtId != null && !districtId.trim().isEmpty()) {
                    DistrictEntity district = districtRepository.findById(districtId.trim())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid District ID: " + districtId));

                    if (!district.getStateId().equalsIgnoreCase(state.getId())) {
                        throw new IllegalArgumentException("District '" + district.getName() + "' does not belong to State '" + state.getName() + "'");
                    }

                    if (subDistrictOrTalukaId != null && !subDistrictOrTalukaId.trim().isEmpty()) {
                        String cleanSubId = subDistrictOrTalukaId.trim();
                        boolean matchesSubDist = subDistrictRepository.findById(cleanSubId)
                                .map(sd -> sd.getDistrictId().equalsIgnoreCase(district.getId()))
                                .orElse(false);

                        boolean matchesTaluka = talukaRepository.findById(cleanSubId)
                                .map(t -> t.getDistrictId().equalsIgnoreCase(district.getId()))
                                .orElse(false);

                        if (!matchesSubDist && !matchesTaluka) {
                            throw new IllegalArgumentException("Sub-District/Taluka ID '" + cleanSubId + "' does not belong to District '" + district.getName() + "'");
                        }

                        if (villageId != null && !villageId.trim().isEmpty()) {
                            VillageEntity village = villageRepository.findById(villageId.trim())
                                    .orElseThrow(() -> new IllegalArgumentException("Invalid Village ID: " + villageId));

                            boolean villageMatch = (village.getSubDistrictId() != null && village.getSubDistrictId().equalsIgnoreCase(cleanSubId)) ||
                                                   (village.getTalukaId() != null && village.getTalukaId().equalsIgnoreCase(cleanSubId));

                            if (!villageMatch) {
                                throw new IllegalArgumentException("Village '" + village.getName() + "' does not belong to Sub-District/Taluka '" + cleanSubId + "'");
                            }
                        }
                    }
                }
            }
        }
    }
}
