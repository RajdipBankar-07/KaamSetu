package com.kaamsetu.modules.user.entity.enums;

/**
 * 3-Role RBAC Authorization Context
 */
public enum RoleEnum {
    WORKER,
    PROVIDER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
