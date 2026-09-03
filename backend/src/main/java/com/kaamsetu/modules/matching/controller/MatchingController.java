package com.kaamsetu.modules.matching.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.matching.dto.JobMatchDto;
import com.kaamsetu.modules.matching.dto.WorkerMatchDto;
import com.kaamsetu.modules.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/matching")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Matching Module", description = "Proximity and skill matching recommendations")
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/jobs/recommended")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Get personalized recommended jobs ranked by proximity, skills, and urgency")
    public ResponseEntity<ApiResponse<List<JobMatchDto>>> getRecommendedJobs(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer maxRadiusKm) {
        List<JobMatchDto> list = matchingService.getRecommendedJobsForWorker(principal.getId(), maxRadiusKm);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/workers/recommended")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Get recommended nearby available candidates for provider requirement")
    public ResponseEntity<ApiResponse<List<WorkerMatchDto>>> getRecommendedWorkers(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer maxRadiusKm) {
        List<WorkerMatchDto> list = matchingService.getRecommendedWorkersForJob(principal.getId(), category, maxRadiusKm);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
