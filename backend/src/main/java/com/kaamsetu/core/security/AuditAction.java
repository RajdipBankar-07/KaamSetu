package com.kaamsetu.core.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Phase 11 Audit Logging: Annotation to automatically capture security audit events via Spring AOP.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditAction {
    String action();
    String entityType() default "GENERAL";
    String description() default "";
}
