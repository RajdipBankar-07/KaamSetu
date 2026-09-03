package com.kaamsetu.core.security;

import com.kaamsetu.modules.admin.entity.AuditLogEntity;
import com.kaamsetu.modules.admin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Phase 11 Security & Compliance: AOP Aspect that automatically audits sensitive actions.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(pointcut = "@annotation(auditAction)", returning = "result")
    public void logAuditAction(JoinPoint joinPoint, AuditAction auditAction, Object result) {
        try {
            UUID actorId = null;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal userPrincipal) {
                actorId = userPrincipal.getId();
            }

            String action = auditAction.action();
            String entityType = auditAction.entityType();
            String details = auditAction.description() + " [Method: " + joinPoint.getSignature().getName() + "]";

            AuditLogEntity logEntry = AuditLogEntity.builder()
                    .actorId(actorId)
                    .action(action)
                    .entityType(entityType)
                    .details(details)
                    .build();
            logEntry.setCreatedAt(Instant.now());

            auditLogRepository.save(logEntry);
            log.info("Security Audit Recorded: Action={}, Entity={}, Actor={}", action, entityType, actorId);
        } catch (Exception e) {
            log.error("Failed to record security audit log asynchronously", e);
        }
    }
}
