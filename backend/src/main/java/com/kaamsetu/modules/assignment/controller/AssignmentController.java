package com.kaamsetu.modules.assignment.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.assignment.entity.AssignmentEntity;
import com.kaamsetu.modules.assignment.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Assignment Module", description = "Worker selection, confirmation, and assignment lifecycle endpoints")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/jobs/{jobId}/select/{workerId}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Provider selects a worker candidate for a job slot")
    public ResponseEntity<ApiResponse<AssignmentEntity>> selectWorker(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID jobId,
            @PathVariable UUID workerId) {
        AssignmentEntity assignment = assignmentService.selectWorker(principal.getId(), jobId, workerId);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.workerSelected", "Worker selected for job"));
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Worker confirms selection and secures job slot")
    public ResponseEntity<ApiResponse<AssignmentEntity>> confirmAssignment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        AssignmentEntity assignment = assignmentService.confirmAssignment(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.confirmedSuccessfully", "Assignment confirmed successfully"));
    }

    @PostMapping("/{id}/decline")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Worker declines selection and reopens job slot")
    public ResponseEntity<ApiResponse<AssignmentEntity>> declineAssignment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        AssignmentEntity assignment = assignmentService.declineAssignment(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.declinedSuccessfully", "Assignment declined"));
    }

    @GetMapping("/my/worker")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Get assignments for current worker")
    public ResponseEntity<ApiResponse<List<AssignmentEntity>>> getWorkerAssignments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AssignmentEntity> assignments = assignmentService.getWorkerAssignments(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(assignments));
    }

    @GetMapping("/my/provider")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Get assignments for current provider's jobs")
    public ResponseEntity<ApiResponse<List<AssignmentEntity>>> getProviderAssignments(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AssignmentEntity> assignments = assignmentService.getProviderAssignments(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(assignments));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Transition assignment to IN_PROGRESS")
    public ResponseEntity<ApiResponse<AssignmentEntity>> startWork(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        AssignmentEntity assignment = assignmentService.startWork(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.workStarted", "Work marked in progress"));
    }

    @PostMapping("/{id}/request-completion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Initiate completion request (COMPLETION_REQUESTED)")
    public ResponseEntity<ApiResponse<AssignmentEntity>> requestCompletion(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        AssignmentEntity assignment = assignmentService.requestCompletion(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.completionRequested", "Completion requested"));
    }

    @PostMapping("/{id}/confirm-completion")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Bilateral confirmation of job completion (COMPLETED)")
    public ResponseEntity<ApiResponse<AssignmentEntity>> confirmCompletion(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        AssignmentEntity assignment = assignmentService.confirmCompletion(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.completed", "Job marked completed"));
    }

    @PostMapping("/{id}/confirm-payment")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Provider confirms wage payment with overtime/additional breakdown")
    public ResponseEntity<ApiResponse<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity>> confirmPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(required = false) java.math.BigDecimal overtimeAmount,
            @RequestParam(required = false) java.math.BigDecimal additionalAmount,
            @RequestParam(defaultValue = "CASH") String paymentType) {
        com.kaamsetu.modules.assignment.entity.CompletionRecordEntity record = assignmentService.confirmPayment(principal.getId(), id, overtimeAmount, additionalAmount, paymentType);
        return ResponseEntity.ok(ApiResponse.ok(record, "payment.confirmed", "Payment confirmed successfully"));
    }

    @PostMapping("/{id}/attendance")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Record worker attendance for an assignment")
    public ResponseEntity<ApiResponse<com.kaamsetu.modules.assignment.entity.AttendanceEntity>> recordAttendance(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(required = false) String workDate,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "PRESENT") String status,
            @RequestParam(required = false) String remarks) {
        java.time.LocalDate date = workDate != null ? java.time.LocalDate.parse(workDate) : java.time.LocalDate.now();
        java.time.LocalTime sTime = startTime != null ? java.time.LocalTime.parse(startTime) : null;
        java.time.LocalTime eTime = endTime != null ? java.time.LocalTime.parse(endTime) : null;

        com.kaamsetu.modules.assignment.entity.AttendanceEntity attendance = assignmentService.recordAttendance(
                principal.getId(), id, date, sTime, eTime, status, remarks);
        return ResponseEntity.ok(ApiResponse.ok(attendance, "attendance.recorded", "Attendance recorded successfully"));
    }

    @GetMapping("/{id}/attendance")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get attendance history for an assignment")
    public ResponseEntity<ApiResponse<List<com.kaamsetu.modules.assignment.entity.AttendanceEntity>>> getAttendanceHistory(
            @PathVariable UUID id) {
        List<com.kaamsetu.modules.assignment.entity.AttendanceEntity> history = assignmentService.getAttendancesForAssignment(id);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/my/worker/completions")
    @PreAuthorize("hasAnyRole('WORKER', 'ADMIN')")
    @Operation(summary = "Get completion & payment records for current worker")
    public ResponseEntity<ApiResponse<List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity>>> getWorkerCompletions(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity> records = assignmentService.getWorkerCompletionRecords(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/my/provider/completions")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Get completion & payment records for current provider")
    public ResponseEntity<ApiResponse<List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity>>> getProviderCompletions(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity> records = assignmentService.getProviderCompletionRecords(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @GetMapping("/job/{jobId}/completions")
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Operation(summary = "Get completion & payment records for a specific job")
    public ResponseEntity<ApiResponse<List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity>>> getJobCompletions(
            @PathVariable UUID jobId) {
        List<com.kaamsetu.modules.assignment.entity.CompletionRecordEntity> records = assignmentService.getJobCompletionRecords(jobId);
        return ResponseEntity.ok(ApiResponse.ok(records));
    }

    @PostMapping("/{id}/acknowledge-payment")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Acknowledge off-platform wage payment (PAID)")
    public ResponseEntity<ApiResponse<AssignmentEntity>> acknowledgePayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "CASH") String paymentType) {
        AssignmentEntity assignment = assignmentService.acknowledgePayment(principal.getId(), id, paymentType);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "payment.acknowledged", "Payment acknowledged"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel assignment with mandatory reason")
    public ResponseEntity<ApiResponse<AssignmentEntity>> cancelAssignment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam String reason) {
        AssignmentEntity assignment = assignmentService.cancelAssignment(principal.getId(), id, reason);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.cancelled", "Assignment cancelled"));
    }

    @PostMapping("/{id}/no-show")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Report party no-show")
    public ResponseEntity<ApiResponse<AssignmentEntity>> reportNoShow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam String notes) {
        AssignmentEntity assignment = assignmentService.reportNoShow(principal.getId(), id, notes);
        return ResponseEntity.ok(ApiResponse.ok(assignment, "assignment.noShowReported", "No-show recorded"));
    }
}
