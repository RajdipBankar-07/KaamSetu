package com.kaamsetu.modules.application.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.application.dto.ApplyJobRequest;
import com.kaamsetu.modules.application.entity.ApplicationEntity;
import com.kaamsetu.modules.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Application Module", description = "Job applications and candidate review endpoints")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Worker applies to an open job")
    public ResponseEntity<ApiResponse<ApplicationEntity>> apply(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID jobId,
            @RequestBody(required = false) ApplyJobRequest request) {
        ApplicationEntity app = applicationService.applyToJob(principal.getId(), jobId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(app, "application.submittedSuccessfully", "Application submitted successfully"));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Get applications submitted by current worker")
    public ResponseEntity<ApiResponse<List<ApplicationEntity>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationEntity> apps = applicationService.getWorkerApplications(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(apps));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Get applicants for a specific job (Provider only)")
    public ResponseEntity<ApiResponse<List<ApplicationEntity>>> getJobApplicants(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID jobId) {
        List<ApplicationEntity> apps = applicationService.getJobApplications(principal.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.ok(apps));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Update application status (e.g. SHORTLISTED, REJECTED)")
    public ResponseEntity<ApiResponse<ApplicationEntity>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam String status) {
        ApplicationEntity updated = applicationService.updateApplicationStatus(principal.getId(), id, status);
        return ResponseEntity.ok(ApiResponse.ok(updated, "application.statusUpdated", "Application status updated"));
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Worker withdraws submitted application")
    public ResponseEntity<ApiResponse<ApplicationEntity>> withdraw(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        ApplicationEntity updated = applicationService.withdrawApplication(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(updated, "application.withdrawn", "Application withdrawn successfully"));
    }
}
