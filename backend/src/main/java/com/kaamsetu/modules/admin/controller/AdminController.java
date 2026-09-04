package com.kaamsetu.modules.admin.controller;

import com.kaamsetu.core.common.ApiResponse;
import com.kaamsetu.core.security.UserPrincipal;
import com.kaamsetu.modules.admin.dto.AdminKpiResponse;
import com.kaamsetu.modules.admin.dto.AdminSendMessageRequest;
import com.kaamsetu.modules.admin.dto.ResolveReportRequest;
import com.kaamsetu.modules.admin.dto.UpdateUserTrustRequest;
import com.kaamsetu.modules.admin.entity.AuditLogEntity;
import com.kaamsetu.modules.admin.entity.ReportEntity;
import com.kaamsetu.modules.admin.service.AdminGovernanceService;
import com.kaamsetu.modules.user.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin & Governance Module", description = "Marketplace health KPIs, pending user approval, trust ladder enforcement, report resolution, and audit logs")
public class AdminController {

    private final AdminGovernanceService adminService;

    @GetMapping("/health/kpis")
    @Operation(summary = "Get real-time marketplace liquidity and operational vitality KPIs")
    public ResponseEntity<ApiResponse<AdminKpiResponse>> getMarketplaceKpis() {
        AdminKpiResponse kpis = adminService.getMarketplaceKpis();
        return ResponseEntity.ok(ApiResponse.ok(kpis));
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered platform users")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getAllUsers() {
        List<UserEntity> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/users/pending")
    @Operation(summary = "List all pending user registrations awaiting Admin approval")
    public ResponseEntity<ApiResponse<List<UserEntity>>> getPendingUsers() {
        List<UserEntity> pendingUsers = adminService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.ok(pendingUsers));
    }

    @PatchMapping("/users/{id}/approve")
    @Operation(summary = "Approve a pending user registration (transitions PENDING -> APPROVED)")
    public ResponseEntity<ApiResponse<UserEntity>> approveUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id) {
        UUID adminId = (principal != null && principal.getId() != null)
                ? principal.getId()
                : UUID.fromString("00000000-0000-0000-0000-000000000001");
        UserEntity user = adminService.approveUser(adminId, id);
        return ResponseEntity.ok(ApiResponse.ok(user, "admin.userApproved", "User registration approved successfully"));
    }

    @PatchMapping("/users/{id}/reject")
    @Operation(summary = "Reject a pending user registration (transitions PENDING -> REJECTED)")
    public ResponseEntity<ApiResponse<UserEntity>> rejectUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "Rejected by Admin") String reason) {
        UUID adminId = (principal != null && principal.getId() != null)
                ? principal.getId()
                : UUID.fromString("00000000-0000-0000-0000-000000000001");
        UserEntity user = adminService.rejectUser(adminId, id, reason);
        return ResponseEntity.ok(ApiResponse.ok(user, "admin.userRejected", "User registration rejected"));
    }

    @PatchMapping("/users/{id}/suspend")
    @Operation(summary = "Suspend user account")
    public ResponseEntity<ApiResponse<UserEntity>> suspendUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "Suspended by Admin") String reason) {
        UserEntity user = adminService.suspendUser(principal.getId(), id, reason);
        return ResponseEntity.ok(ApiResponse.ok(user, "admin.userSuspended", "User account suspended"));
    }

    @PatchMapping("/users/{id}/ban")
    @Operation(summary = "Permanently ban user account")
    public ResponseEntity<ApiResponse<UserEntity>> banUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "Banned by Admin") String reason) {
        UserEntity user = adminService.banUser(principal.getId(), id, reason);
        return ResponseEntity.ok(ApiResponse.ok(user, "admin.userBanned", "User account banned"));
    }

    @PatchMapping("/users/{id}/trust")
    @Operation(summary = "Update user trust ladder status (Healthy, Warning, Restricted, Suspended, Banned)")
    public ResponseEntity<ApiResponse<UserEntity>> updateUserTrust(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserTrustRequest request) {
        UserEntity updated = adminService.updateUserTrustStatus(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "admin.trustUpdated", "User trust status updated"));
    }

    @PatchMapping("/users/{id}/verify")
    @Operation(summary = "Toggle user verification badge")
    public ResponseEntity<ApiResponse<UserEntity>> toggleVerification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam boolean verified) {
        UserEntity updated = adminService.toggleUserVerification(principal.getId(), id, verified);
        return ResponseEntity.ok(ApiResponse.ok(updated, "admin.verificationUpdated", "Verification status updated"));
    }

    @GetMapping("/reports")
    @Operation(summary = "List all dispute and moderation reports")
    public ResponseEntity<ApiResponse<List<ReportEntity>>> getReports(@RequestParam(required = false) String status) {
        List<ReportEntity> reports = adminService.getAllReports(status);
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @PostMapping("/reports/{id}/resolve")
    @Operation(summary = "Resolve a dispute report and optionally apply enforcement actions")
    public ResponseEntity<ApiResponse<ReportEntity>> resolveReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody ResolveReportRequest request) {
        ReportEntity resolved = adminService.resolveReport(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(resolved, "admin.reportResolved", "Report resolved successfully"));
    }

    @DeleteMapping("/jobs/{id}")
    @Operation(summary = "Moderate and cancel a fraudulent or violating job")
    public ResponseEntity<ApiResponse<Map<String, String>>> moderateJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(defaultValue = "Violates terms") String reason) {
        adminService.deleteJobByAdmin(principal.getId(), id, reason);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("message", "Job moderated successfully")));
    }

    @PostMapping("/messages/send")
    @Operation(summary = "Send real-time direct message or broadcast notification to workers/providers")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AdminSendMessageRequest request) {
        adminService.sendAdminMessage(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "DELIVERED", "message", "Admin message dispatched successfully")));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get immutable security and governance audit trail")
    public ResponseEntity<ApiResponse<List<AuditLogEntity>>> getAuditLogs() {
        List<AuditLogEntity> logs = adminService.getAuditLogs();
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @GetMapping("/audit-logs/export")
    @Operation(summary = "Export audit logs as CSV or JSON for compliance")
    public void exportAuditLogs(
            @RequestParam(defaultValue = "csv") String format,
            HttpServletResponse response) throws IOException {

        List<AuditLogEntity> logs = adminService.getAuditLogs();

        if ("json".equalsIgnoreCase(format)) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kaamsetu_audit_logs.json");
            PrintWriter writer = response.getWriter();
            writer.write("[");
            for (int i = 0; i < logs.size(); i++) {
                AuditLogEntity l = logs.get(i);
                writer.printf("{\"id\":%d,\"actor\":\"%s\",\"action\":\"%s\",\"entity\":\"%s\",\"details\":\"%s\",\"time\":\"%s\"}%s",
                        l.getId(),
                        l.getActorUserId() != null ? l.getActorUserId() : "SYSTEM",
                        l.getActionType() != null ? l.getActionType() : "",
                        l.getEntityName() != null ? l.getEntityName() : "",
                        l.getNewState() != null ? l.getNewState().replace("\"", "\\\"") : "",
                        l.getCreatedAt(),
                        (i < logs.size() - 1 ? "," : ""));
            }
            writer.write("]");
            writer.flush();
        } else {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kaamsetu_audit_logs.csv");
            PrintWriter writer = response.getWriter();
            writer.println("ID,ActorID,Action,EntityType,Details,CreatedAt");
            for (AuditLogEntity l : logs) {
                writer.printf("%d,%s,\"%s\",\"%s\",\"%s\",%s%n",
                        l.getId(),
                        l.getActorUserId() != null ? l.getActorUserId() : "SYSTEM",
                        l.getActionType() != null ? l.getActionType() : "",
                        l.getEntityName() != null ? l.getEntityName() : "",
                        l.getNewState() != null ? l.getNewState().replace("\"", "'") : "",
                        l.getCreatedAt());
            }
            writer.flush();
        }
    }
}
