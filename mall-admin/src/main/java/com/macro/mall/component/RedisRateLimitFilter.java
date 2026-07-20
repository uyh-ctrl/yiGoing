package com.macro.mall.component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

/** 对高风险公开接口做按 IP 的固定窗口限流，Redis 故障时自动放行。 */
@Component
public class RedisRateLimitFilter extends OncePerRequestFilter {
    private static final Map<String, Integer> LIMITS = Map.of(
            "/auth/login", 10,
            "/auth/register", 5,
            "/ai/customer-service/chat", 30
    );
    private final StringRedisTemplate redisTemplate;

    public RedisRateLimitFilter(StringRedisTemplate redisTemplate) { this.redisTemplate = redisTemplate; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Integer limit = LIMITS.get(request.getRequestURI());
        if (limit == null || "OPTIONS".equalsIgnoreCase(request.getMethod())) { chain.doFilter(request, response); return; }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr(); else ip = ip.split(",")[0].trim();
        try {
            String key = "easygoing:rate:" + request.getRequestURI() + ":" + ip + ":" + (System.currentTimeMillis() / 60_000);
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) redisTemplate.expire(key, Duration.ofMinutes(1));
            if (count != null && count > limit) {
                response.setStatus(429); response.setCharacterEncoding("UTF-8"); response.setContentType("application/json");
                response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁，请稍后重试\"}"); return;
            }
        } catch (DataAccessException ignored) { }
        chain.doFilter(request, response);
    }
}
