package com.kaamsetu.modules.worker.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.GenderEnum;
import com.kaamsetu.modules.user.repository.UserRepository;
import com.kaamsetu.modules.worker.dto.UpdateWorkerProfileRequest;
import com.kaamsetu.modules.worker.entity.WorkerEntity;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
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
@RequestMapping("/worker")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Worker Module", description = "Endpoints restricted strictly to WORKER and ADMIN roles")
public class WorkerController {

    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;
    private final com.kaamsetu.modules.job.repository.JobRepository jobRepository;
    private final com.kaamsetu.modules.application.repository.ApplicationRepository applicationRepository;
    private final com.kaamsetu.modules.assignment.repository.AssignmentRepository assignmentRepository;
    private final com.kaamsetu.modules.review.service.ReviewService reviewService;

    @GetMapping("/dashboard/stats")
    @Transactional(readOnly = true)
    @Operation(summary = "Calculate and return real-time database dashboard statistics for authenticated worker")
    public ResponseEntity<ApiResponse<com.kaamsetu.modules.worker.dto.WorkerDashboardStatsResponse>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        WorkerEntity worker = workerRepository.findByUserId(principal.getId())
                .orElseGet(() -> WorkerEntity.builder().userId(principal.getId()).build());

        long availableJobs = jobRepository.countByStatus("OPEN");
        long myApps = worker.getId() != null ? applicationRepository.countByWorkerId(worker.getId()) : 0;
        long completedJobs = worker.getId() != null ? assignmentRepository.countByWorkerIdAndStatus(worker.getId(), "COMPLETED") : 0;

        java.math.BigDecimal avgRating = reviewService.getAverageRating(principal.getId());
        long ratingCount = reviewService.getRatingCount(principal.getId());

        List<com.kaamsetu.modules.review.dto.PendingRatingDto> pendingRatings = reviewService.getPendingRatings(principal.getId());

        com.kaamsetu.modules.worker.dto.WorkerDashboardStatsResponse response =
                com.kaamsetu.modules.worker.dto.WorkerDashboardStatsResponse.builder()
                        .availableJobsCount(availableJobs)
                        .myApplicationsCount(myApps)
                        .completedJobsCount(completedJobs)
                        .minDailyWage(worker.getMinDailyWage() != null ? worker.getMinDailyWage() : new java.math.BigDecimal("600.00"))
                        .trustIndex(worker.getTrustIndex() != null ? worker.getTrustIndex() : new java.math.BigDecimal("5.0"))
                        .averageRating(avgRating)
                        .ratingsCount(ratingCount)
                        .pendingRatings(pendingRatings)
                        .build();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/profile")
    @Transactional(readOnly = true)
    @Operation(summary = "Get worker profile details directly from database for authenticated worker")
    public ResponseEntity<ApiResponse<WorkerEntity>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        WorkerEntity worker = workerRepository.findByUserId(principal.getId())
                .orElseGet(() -> {
                    UserEntity u = userRepository.findById(principal.getId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + principal.getId()));
                    WorkerEntity newWorker = WorkerEntity.builder()
                            .userId(u.getId())
                            .fullName(u.getFullName())
                            .countryId(u.getCountryId())
                            .stateId(u.getStateId())
                            .districtId(u.getDistrictId())
                            .talukaId(u.getTalukaId())
                            .villageId(u.getVillageId())
                            .village(u.getVillage() != null ? u.getVillage() : "Ranjangaon")
                            .taluka(u.getVillage() != null ? u.getVillage() : "Shirur")
                            .district(u.getDistrict() != null ? u.getDistrict() : "Pune Rural")
                            .state(u.getState() != null ? u.getState() : "Maharashtra")
                            .country(u.getCountry() != null ? u.getCountry() : "India")
                            .build();
                    return workerRepository.save(newWorker);
                });

        UserEntity user = userRepository.findById(principal.getId()).orElse(null);
        if (user != null) {
            worker.setMobile(user.getMobile());
            worker.setEmail(user.getEmail());
            worker.setUsername(user.getUsername());
            worker.setGender(user.getGender() != null ? user.getGender().name() : "MALE");
        }

        return ResponseEntity.ok(ApiResponse.ok(worker));
    }

    @PutMapping("/profile")
    @Transactional
    @Operation(summary = "Update worker profile permanently in database for authenticated worker")
    public ResponseEntity<ApiResponse<WorkerEntity>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateWorkerProfileRequest request) {
        
        UserEntity user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + principal.getId()));

        WorkerEntity worker = workerRepository.findByUserId(principal.getId())
                .orElseGet(() -> WorkerEntity.builder().userId(user.getId()).build());

        // Update editable fields in WorkerEntity and UserEntity
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            worker.setFullName(request.getFullName().trim());
            user.setFullName(request.getFullName().trim());
        }
        if (request.getMobile() != null && !request.getMobile().isBlank()) {
            user.setMobile(request.getMobile().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getCountryId() != null) {
            worker.setCountryId(request.getCountryId());
            user.setCountryId(request.getCountryId());
        }
        if (request.getStateId() != null) {
            worker.setStateId(request.getStateId());
            user.setStateId(request.getStateId());
        }
        if (request.getDistrictId() != null) {
            worker.setDistrictId(request.getDistrictId());
            user.setDistrictId(request.getDistrictId());
        }
        if (request.getTalukaId() != null) {
            worker.setTalukaId(request.getTalukaId());
            user.setTalukaId(request.getTalukaId());
        }
        if (request.getVillageId() != null) {
            worker.setVillageId(request.getVillageId());
            user.setVillageId(request.getVillageId());
        }
        if (request.getCountry() != null) {
            worker.setCountry(request.getCountry());
            user.setCountry(request.getCountry());
        }
        if (request.getState() != null) {
            worker.setState(request.getState());
            user.setState(request.getState());
        }
        if (request.getDistrict() != null) {
            worker.setDistrict(request.getDistrict());
            user.setDistrict(request.getDistrict());
        }
        if (request.getTaluka() != null) {
            worker.setTaluka(request.getTaluka());
        }
        if (request.getVillage() != null) {
            worker.setVillage(request.getVillage().trim());
            user.setVillage(request.getVillage().trim());
        }
        if (request.getPincode() != null) {
            worker.setPincode(request.getPincode().trim());
        }
        if (request.getTravelRadiusKm() != null) {
            worker.setTravelRadiusKm(request.getTravelRadiusKm());
        }
        if (request.getMinDailyWage() != null) {
            worker.setMinDailyWage(request.getMinDailyWage());
        }
        if (request.getExperienceYears() != null) {
            worker.setExperienceYears(request.getExperienceYears());
        }
        if (request.getBio() != null) {
            worker.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            worker.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getAvailableToday() != null) {
            worker.setAvailableToday(request.getAvailableToday());
        }
        if (request.getSkills() != null) {
            worker.setSkills(request.getSkills().trim());
        }
        if (request.getAvailabilityDays() != null) {
            worker.setAvailabilityDays(request.getAvailabilityDays().trim());
        }

        if (request.getGender() != null && !request.getGender().isBlank()) {
            try {
                user.setGender(GenderEnum.valueOf(request.getGender().trim().toUpperCase()));
            } catch (Exception ignored) {}
        }

        // Persist both entities to PostgreSQL database
        userRepository.save(user);
        worker = workerRepository.save(worker);

        // Populate transient fields
        worker.setMobile(user.getMobile());
        worker.setEmail(user.getEmail());
        worker.setUsername(user.getUsername());
        worker.setGender(user.getGender() != null ? user.getGender().name() : "MALE");

        return ResponseEntity.ok(ApiResponse.ok(worker, "worker.profileUpdated", "Worker profile updated successfully in database"));
    }

    @PatchMapping("/availability/toggle")
    @Transactional
    @Operation(summary = "Toggle worker availability for today in database")
    public ResponseEntity<ApiResponse<WorkerEntity>> toggleAvailability(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam boolean isAvailable) {
        WorkerEntity worker = workerRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Worker profile not found"));
        worker.setAvailableToday(isAvailable);
        worker = workerRepository.save(worker);
        return ResponseEntity.ok(ApiResponse.ok(worker, "worker.availabilityUpdated", "Availability updated in database"));
    }
}
