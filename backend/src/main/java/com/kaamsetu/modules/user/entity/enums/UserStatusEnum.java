package com.kaamsetu.modules.user.entity.enums;

/**
 * Account Status Lifecycle Matrix for KaamSetu V1
 * New accounts start as PENDING until reviewed & APPROVED by Admin.
 */
public enum UserStatusEnum {
    PENDING,
    APPROVED,
    ACTIVE,
    REJECTED,
    DEACTIVATED,
    SUSPENDED,
    BANNED
}
