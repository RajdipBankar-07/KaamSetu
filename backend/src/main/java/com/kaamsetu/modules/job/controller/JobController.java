package com.kaamsetu.modules.job.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.job.dto.CreateJobRequest;
import com.kaamsetu.modules.job.entity.JobEntity;
import com.kaamsetu.modules.job.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Module", description = "Job posting, listing, and lifecycle endpoints")
public class JobController {

    private final JobService jobService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Post a new job requirement (Provider only)")
    public ResponseEntity<ApiResponse<JobEntity>> createJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateJobRequest request) {
        JobEntity created = jobService.createJob(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(created, "job.createdSuccessfully", "Job created successfully"));
    }

    @GetMapping
    @Operation(summary = "Search and list open jobs with optional category and location filters")
    public ResponseEntity<ApiResponse<List<JobEntity>>> getOpenJobs(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String taluka) {
        List<JobEntity> jobs = jobService.getOpenJobs(category, taluka);
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get jobs created by the authenticated provider with optional status filter")
    public ResponseEntity<ApiResponse<List<JobEntity>>> getMyJobs(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status) {
        List<JobEntity> jobs = jobService.getProviderJobs(principal.getId(), status);
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get all platform jobs (Admin only)")
    public ResponseEntity<ApiResponse<List<JobEntity>>> getAllJobs() {
        List<JobEntity> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(ApiResponse.ok(jobs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<ApiResponse<JobEntity>> getJobById(@PathVariable UUID id) {
        JobEntity job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.ok(job));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update job status (Provider/Admin only)")
    public ResponseEntity<ApiResponse<JobEntity>> updateJobStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam String status) {
        JobEntity updated = jobService.updateJobStatus(principal.getId(), id, status);
        return ResponseEntity.ok(ApiResponse.ok(updated, "job.statusUpdated", "Job status updated"));
    }
}
