package com.kaamsetu.core.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Phase 11 Security Hardening: In-memory sliding window rate limiter for DDoS and brute-force protection.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 200;
    private static final int AUTH_MAX_REQUESTS_PER_MINUTE = 60;
    private static final int LOCALHOST_MAX_REQUESTS_PER_MINUTE = 10000;

    private final Map<String, RequestBucket> requestBuckets = new ConcurrentHashMap<>();

    private static class RequestBucket {
        long windowStart;
        AtomicInteger count;

        RequestBucket(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIp(request);
        String uri = request.getRequestURI();
        long now = System.currentTimeMillis();

        int limit;
        if (isLocalhost(clientIp)) {
            limit = LOCALHOST_MAX_REQUESTS_PER_MINUTE;
        } else {
            limit = uri.contains("/auth/") ? AUTH_MAX_REQUESTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;
        }

        RequestBucket bucket = requestBuckets.compute(clientIp, (key, existing) -> {
            if (existing == null || (now - existing.windowStart) > 60000L) {
                return new RequestBucket(now);
            } else {
                existing.count.incrementAndGet();
                return existing;
            }
        });

        if (bucket.count.get() > limit) {
            log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, uri);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Retry-After", "60");
            response.getWriter().write("{\"success\":false,\"errorCode\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Too many requests. Please try again in 1 minute.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLocalhost(String ip) {
        return ip == null ||
               ip.equals("127.0.0.1") ||
               ip.equals("0:0:0:0:0:0:0:1") ||
               ip.equals("::1") ||
               ip.equalsIgnoreCase("localhost");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr() != null ? request.getRemoteAddr() : "127.0.0.1";
    }
}
