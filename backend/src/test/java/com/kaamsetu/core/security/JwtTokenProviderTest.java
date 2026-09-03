package com.kaamsetu.core.security;

import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpirationMs = 3600000; // 1 hour
    private final long testRefreshExpirationMs = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(testSecret, testExpirationMs, testRefreshExpirationMs);
    }

    @Test
    @DisplayName("Should generate valid JWT token with user claims")
    void shouldGenerateValidToken() {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.builder()
                .id(userId)
                .mobile("+919822012345")
                .role(RoleEnum.WORKER)
                .languagePreference(LanguageCodeEnum.mr)
                .status(UserStatusEnum.ACTIVE)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_WORKER")))
                .build();

        String token = jwtTokenProvider.generateTokenFromPrincipal(principal, testExpirationMs);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals("WORKER", jwtTokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should reject tampered or invalid JWT token")
    void shouldRejectInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidpayload.invalidsignature";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void shouldGenerateRefreshToken() {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.builder()
                .id(userId)
                .mobile("+919423054321")
                .role(RoleEnum.PROVIDER)
                .languagePreference(LanguageCodeEnum.hi)
                .status(UserStatusEnum.ACTIVE)
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROVIDER")))
                .build();

        String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(refreshToken));
        assertEquals("PROVIDER", jwtTokenProvider.getRoleFromToken(refreshToken));
    }
}
