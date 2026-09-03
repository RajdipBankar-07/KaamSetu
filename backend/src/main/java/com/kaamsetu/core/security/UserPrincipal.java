package com.kaamsetu.core.security;

import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.entity.enums.LanguageCodeEnum;
import com.kaamsetu.modules.user.entity.enums.RoleEnum;
import com.kaamsetu.modules.user.entity.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String username;
    private final String fullName;
    private final String mobile;
    private final String password;
    private final RoleEnum role;
    private final LanguageCodeEnum languagePreference;
    private final UserStatusEnum status;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UserEntity user) {
        return create(user, user.getRole() == RoleEnum.WORKER, user.getRole() == RoleEnum.PROVIDER);
    }

    public static UserPrincipal create(UserEntity user, boolean hasWorkerProfile, boolean hasProviderProfile) {
        java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (user.getRole() == RoleEnum.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        if (hasWorkerProfile || user.getRole() == RoleEnum.WORKER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_WORKER"));
        }
        if (hasProviderProfile || user.getRole() == RoleEnum.PROVIDER) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PROVIDER"));
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getUsername() != null ? user.getUsername() : user.getMobile())
                .fullName(user.getFullName() != null ? user.getFullName() : user.getMobile())
                .mobile(user.getMobile())
                .password(user.getPasswordHash())
                .role(user.getRole())
                .languagePreference(user.getLanguagePreference())
                .status(user.getStatus())
                .authorities(authorities)
                .build();
    }

    @Override
    public String getUsername() {
        return username != null ? username : mobile;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatusEnum.SUSPENDED && status != UserStatusEnum.BANNED && status != UserStatusEnum.REJECTED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatusEnum.APPROVED || status == UserStatusEnum.ACTIVE;
    }
}
