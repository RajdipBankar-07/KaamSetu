package com.kaamsetu.modules.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "actor_user_id", columnDefinition = "CHAR(36)")
    private UUID actorUserId;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;

    @Column(name = "entity_id", columnDefinition = "CHAR(36)")
    private UUID entityId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "old_state", columnDefinition = "JSON")
    private String oldState;

    @Column(name = "new_state", columnDefinition = "JSON")
    private String newState;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getActorId() { return actorUserId; }
    public void setActorId(UUID actorId) { this.actorUserId = actorId; }

    public String getAction() { return actionType; }
    public void setAction(String action) { this.actionType = action; }

    public String getEntityType() { return entityName; }
    public void setEntityType(String entityType) { this.entityName = entityType; }

    public String getDetails() { return newState; }
    public void setDetails(String details) { this.newState = details; }

    public static class AuditLogEntityBuilder {
        public AuditLogEntityBuilder actorId(UUID actorId) {
            this.actorUserId = actorId;
            return this;
        }

        public AuditLogEntityBuilder action(String action) {
            this.actionType = action;
            return this;
        }

        public AuditLogEntityBuilder entityType(String entityType) {
            this.entityName = entityType;
            return this;
        }

        public AuditLogEntityBuilder details(String details) {
            this.newState = details;
            return this;
        }
    }
}
