package com.kaamsetu.core.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 🌾 KaamSetu (कामसेतू) - Dedicated Server Availability & Health Gate Endpoint
 * Provides public readiness and liveness checks for the Next.js/Frontend shell.
 * 
 * Strict Security Rules:
 * 1. Publicly accessible without JWT.
 * 2. Tests database readiness with a fast timeout (2s).
 * 3. Returns HTTP 200 (UP) when backend and database are healthy.
 * 4. Returns HTTP 503 (DOWN) when database is unavailable.
 * 5. NEVER exposes sensitive information (credentials, passwords, stack traces, env variables).
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health & Server Availability Gate", description = "Public health endpoint for frontend access control and readiness verification")
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    private final DataSource dataSource;

    /**
     * Primary Health & Readiness Gate: GET /api/v1/health
     */
    @GetMapping
    @Operation(summary = "Check backend and database health status (Public)")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new LinkedHashMap<>();
        boolean isDbHealthy = checkDatabaseHealth();

        response.put("service", "kaamsetu-backend");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now().toEpochMilli());

        if (isDbHealthy) {
            response.put("status", "UP");
            response.put("database", "UP");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "DOWN");
            response.put("database", "DOWN");
            response.put("error", "Database connectivity unavailable");
            log.warn("🚨 [HealthController] Health check failed: Database connection is not available.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * Liveness Probe: GET /api/v1/health/live
     * Verifies that the Spring Boot process is alive.
     */
    @GetMapping("/live")
    @Operation(summary = "Liveness probe - confirms backend JVM process is alive")
    public ResponseEntity<Map<String, Object>> getLiveness() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "kaamsetu-backend");
        response.put("timestamp", Instant.now().toEpochMilli());
        return ResponseEntity.ok(response);
    }

    /**
     * Readiness Probe: GET /api/v1/health/ready
     * Verifies that the application is ready to serve database-backed traffic.
     */
    @GetMapping("/ready")
    @Operation(summary = "Readiness probe - confirms backend is ready to serve database traffic")
    public ResponseEntity<Map<String, Object>> getReadiness() {
        return getHealth();
    }

    /**
     * Verifies DataSource connectivity with a 2-second timeout without leaking connection details.
     */
    public boolean checkDatabaseHealth() {
        if (dataSource == null) {
            return false;
        }
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            log.error("Database health verification failed: {}", e.getMessage());
            return false;
        }
    }
}
