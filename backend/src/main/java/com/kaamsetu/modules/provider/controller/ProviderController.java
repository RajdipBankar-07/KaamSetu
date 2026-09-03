package com.kaamsetu.modules.provider.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.provider.dto.UpdateProviderProfileRequest;
import com.kaamsetu.modules.provider.entity.ProviderEntity;
import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Provider Module", description = "Endpoints restricted strictly to PROVIDER and ADMIN roles")
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final com.kaamsetu.modules.job.repository.JobRepository jobRepository;
    private final com.kaamsetu.modules.application.repository.ApplicationRepository applicationRepository;
    private final com.kaamsetu.modules.assignment.repository.AssignmentRepository assignmentRepository;
    private final com.kaamsetu.modules.review.service.ReviewService reviewService;

    @GetMapping("/dashboard/stats")
    @Transactional(readOnly = true)
    @Operation(summary = "Calculate and return real-time database dashboard statistics for authenticated provider")
    public ResponseEntity<ApiResponse<com.kaamsetu.modules.provider.dto.ProviderDashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        ProviderEntity provider = providerRepository.findByUserId(principal.getId())
                .orElseGet(() -> ProviderEntity.builder().userId(principal.getId()).build());

        List<com.kaamsetu.modules.job.entity.JobEntity> myJobs = provider.getId() != null 
                ? jobRepository.findByProviderIdOrderByCreatedAtDesc(provider.getId())
                : java.util.Collections.emptyList();

        List<java.util.UUID> myJobIds = myJobs.stream().map(com.kaamsetu.modules.job.entity.JobEntity::getId).toList();

        long postedJobsCount = myJobs.size();
        long totalApps = myJobIds.isEmpty() ? 0 : applicationRepository.countByJobIdIn(myJobIds);
        long confirmedWorkers = provider.getId() != null 
                ? (assignmentRepository.countByProviderIdAndStatus(provider.getId(), "CONFIRMED")
                   + assignmentRepository.countByProviderIdAndStatus(provider.getId(), "IN_PROGRESS")
                   + assignmentRepository.countByProviderIdAndStatus(provider.getId(), "COMPLETED"))
                : 0;

        java.math.BigDecimal avgRating = reviewService.getAverageRating(principal.getId());
        long ratingCount = reviewService.getRatingCount(principal.getId());

        List<com.kaamsetu.modules.review.dto.PendingRatingDto> pendingRatings = reviewService.getPendingRatings(principal.getId());

        com.kaamsetu.modules.provider.dto.ProviderDashboardStatsResponse response = 
                com.kaamsetu.modules.provider.dto.ProviderDashboardStatsResponse.builder()
                        .postedJobsCount(postedJobsCount)
                        .totalApplicationsCount(totalApps)
                        .confirmedWorkersCount(confirmedWorkers)
                        .averageRating(avgRating)
                        .ratingsCount(ratingCount)
                        .trustIndex(provider.getTrustIndex())
                        .recentJobs(myJobs.stream().limit(5).toList())
                        .pendingRatings(pendingRatings)
                        .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    @Operation(summary = "Get provider profile details directly from database")
    public ResponseEntity<ApiResponse<ProviderEntity>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        ProviderEntity provider = providerRepository.findByUserId(principal.getId())
                .orElseGet(() -> {
                    UserEntity u = userRepository.findById(principal.getId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getId()));
                    ProviderEntity newProvider = ProviderEntity.builder()
                            .userId(u.getId())
                            .name(u.getFullName())
                            .businessName(u.getFullName())
                            .countryId(u.getCountryId())
                            .stateId(u.getStateId())
                            .districtId(u.getDistrictId())
                            .talukaId(u.getTalukaId())
                            .villageId(u.getVillageId())
                            .village(u.getVillage() != null ? u.getVillage() : "Shirur Rural")
                            .taluka(u.getVillage() != null ? u.getVillage() : "Shirur")
                            .district(u.getDistrict() != null ? u.getDistrict() : "Pune Rural")
                            .state(u.getState() != null ? u.getState() : "Maharashtra")
                            .country(u.getCountry() != null ? u.getCountry() : "India")
                            .build();
                    return providerRepository.save(newProvider);
                });

        UserEntity user = userRepository.findById(principal.getId()).orElse(null);
        if (user != null) {
            provider.setMobile(user.getMobile());
            provider.setEmail(user.getEmail());
            provider.setUsername(user.getUsername());
            provider.setGender(user.getGender() != null ? user.getGender().name() : "MALE");
        }

        return ResponseEntity.ok(ApiResponse.ok(provider));
    }

    @PutMapping("/profile")
    @Transactional
    @Operation(summary = "Update provider profile permanently in database for authenticated provider")
    public ResponseEntity<ApiResponse<ProviderEntity>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateProviderProfileRequest request) {

        UserEntity user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + principal.getId()));

        ProviderEntity provider = providerRepository.findByUserId(principal.getId())
                .orElseGet(() -> ProviderEntity.builder().userId(user.getId()).build());

        String targetName = request.getFullName() != null && !request.getFullName().isBlank() 
                ? request.getFullName().trim() 
                : (request.getName() != null && !request.getName().isBlank() ? request.getName().trim() : null);

        if (targetName != null) {
            provider.setName(targetName);
            user.setFullName(targetName);
        }
        if (request.getBusinessName() != null && !request.getBusinessName().isBlank()) {
            provider.setBusinessName(request.getBusinessName().trim());
        }
        if (request.getMobile() != null && !request.getMobile().isBlank()) {
            user.setMobile(request.getMobile().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getProviderType() != null) {
            provider.setProviderType(request.getProviderType());
        }
        if (request.getCountryId() != null) {
            provider.setCountryId(request.getCountryId());
            user.setCountryId(request.getCountryId());
        }
        if (request.getStateId() != null) {
            provider.setStateId(request.getStateId());
            user.setStateId(request.getStateId());
        }
        if (request.getDistrictId() != null) {
            provider.setDistrictId(request.getDistrictId());
            user.setDistrictId(request.getDistrictId());
        }
        if (request.getTalukaId() != null) {
            provider.setTalukaId(request.getTalukaId());
            user.setTalukaId(request.getTalukaId());
        }
        if (request.getVillageId() != null) {
            provider.setVillageId(request.getVillageId());
            user.setVillageId(request.getVillageId());
        }
        if (request.getCountry() != null) {
            provider.setCountry(request.getCountry());
            user.setCountry(request.getCountry());
        }
        if (request.getState() != null) {
            provider.setState(request.getState());
            user.setState(request.getState());
        }
        if (request.getDistrict() != null) {
            provider.setDistrict(request.getDistrict());
            user.setDistrict(request.getDistrict());
        }
        if (request.getTaluka() != null) {
            provider.setTaluka(request.getTaluka());
        }
        if (request.getVillage() != null) {
            provider.setVillage(request.getVillage().trim());
            user.setVillage(request.getVillage().trim());
        }
        if (request.getPincode() != null) {
            provider.setPincode(request.getPincode().trim());
        }

        if (request.getGender() != null && !request.getGender().isBlank()) {
            try {
                user.setGender(GenderEnum.valueOf(request.getGender().trim().toUpperCase()));
            } catch (Exception ignored) {}
        }

        userRepository.save(user);
        provider = providerRepository.save(provider);

        provider.setMobile(user.getMobile());
        provider.setEmail(user.getEmail());
        provider.setUsername(user.getUsername());
        provider.setGender(user.getGender() != null ? user.getGender().name() : "MALE");

        return ResponseEntity.ok(ApiResponse.ok(provider, "provider.profileUpdated", "Provider profile updated successfully in database"));
    }
}
