package com.kaamsetu.modules.location.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.modules.location.entity.*;
import com.kaamsetu.modules.location.service.LocationHierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Location Hierarchy", description = "Hierarchical Country -> State -> District -> Sub-District -> Village endpoints")
public class LocationController {

    private final LocationHierarchyService locationService;

    @GetMapping("/countries")
    @Operation(summary = "Get list of available countries")
    public ResponseEntity<ApiResponse<List<CountryEntity>>> getCountries() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getCountries()));
    }

    @GetMapping("/states")
    @Operation(summary = "Get states belonging to a specific Country")
    public ResponseEntity<ApiResponse<List<StateEntity>>> getStates(@RequestParam(required = false, defaultValue = "IN") String countryId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getStatesByCountryId(countryId)));
    }

    @GetMapping("/districts")
    @Operation(summary = "Get districts belonging to a specific State")
    public ResponseEntity<ApiResponse<List<DistrictEntity>>> getDistricts(@RequestParam(required = false) String stateId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getDistrictsByStateId(stateId)));
    }

    @GetMapping("/sub-districts")
    @Operation(summary = "Get sub-districts belonging to a specific District")
    public ResponseEntity<ApiResponse<List<SubDistrictEntity>>> getSubDistricts(@RequestParam(required = false) String districtId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getSubDistrictsByDistrictId(districtId)));
    }

    @GetMapping("/talukas")
    @Operation(summary = "Get talukas belonging to a specific District (alias)")
    public ResponseEntity<ApiResponse<List<TalukaEntity>>> getTalukas(@RequestParam(required = false) String districtId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getTalukasByDistrictId(districtId)));
    }

    @GetMapping("/villages")
    @Operation(summary = "Get villages belonging to a Sub-District/Taluka with search filtering")
    public ResponseEntity<ApiResponse<List<VillageEntity>>> getVillages(
            @RequestParam(required = false) String subDistrictId,
            @RequestParam(required = false) String talukaId,
            @RequestParam(required = false) String search) {
        String targetSubId = subDistrictId != null && !subDistrictId.trim().isEmpty() ? subDistrictId : talukaId;
        return ResponseEntity.ok(ApiResponse.ok(locationService.getVillagesBySubDistrictId(targetSubId, search)));
    }

    @GetMapping("/hierarchy")
    @Operation(summary = "Get full location hierarchy catalog for client-side offline caching")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFullHierarchy() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getFullHierarchy()));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate location hierarchy combination")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateHierarchy(@RequestBody Map<String, String> payload) {
        String countryId = payload.get("countryId");
        String stateId = payload.get("stateId");
        String districtId = payload.get("districtId");
        String subDistrictId = payload.get("subDistrictId") != null ? payload.get("subDistrictId") : payload.get("talukaId");
        String villageId = payload.get("villageId");

        locationService.validateHierarchy(countryId, stateId, districtId, subDistrictId, villageId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("valid", true, "message", "Location hierarchy is valid")));
    }
}
