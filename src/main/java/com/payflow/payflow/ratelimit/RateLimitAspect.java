package com.payflow.payflow.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    public RateLimitAspect(StringRedisTemplate redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userKey = (authentication != null) ? authentication.getName() : getClientIp();

        String redisKey = "rate:" + userKey + ":" + joinPoint.getSignature().getName();

        String currentCountStr = redisTemplate.opsForValue().get(redisKey);
        int currentCount = (currentCountStr != null) ? Integer.parseInt(currentCountStr) : 0;

        if (currentCount == 0) {
            // First request — set counter to 1 with TTL
            redisTemplate.opsForValue().set(redisKey, "1",
                    Duration.ofSeconds(rateLimit.windowSeconds()));
        } else if (currentCount < rateLimit.maxRequests()) {
            // Within limit — increment counter
            redisTemplate.opsForValue().increment(redisKey);
        } else {
            // Limit exceeded — reject
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded. Max " + rateLimit.maxRequests() +
                            " requests per " + rateLimit.windowSeconds() + " seconds.");
        }

        return joinPoint.proceed();
    }

    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getRemoteAddr();
    }
}