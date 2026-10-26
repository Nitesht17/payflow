package com.payflow.payflow.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this annotation to any controller method to enforce rate limiting.
 * Uses Redis to track request counts per authenticated user.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int maxRequests() default 5;
    int windowSeconds() default 60;
}
