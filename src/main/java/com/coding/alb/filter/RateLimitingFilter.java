package com.coding.alb.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(0) // Highest priority - runs first
@Slf4j
public class RateLimitingFilter implements Filter {

    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    
    @Value("${rate.limit.max:100}")
    private int maxRequestsPerMinute;
    
    @Value("${rate.limit.window:60000}")
    private long timeWindowMs;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String clientIp = httpRequest.getRemoteAddr();
            
            if (isRateLimited(clientIp)) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(429);
                httpResponse.getWriter().write("Rate limit exceeded. Please try again later.");
                log.warn("Rate limit exceeded for IP: {}", clientIp);
                return;
            }
        }
        
        // Continue with the filter chain if not rate limited
        chain.doFilter(request, response);
    }
    
    private boolean isRateLimited(String clientIp) {
        long currentTime = System.currentTimeMillis();
        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, 
                k -> new RequestCounter(currentTime));
        
        // Reset counter if time window has passed
        if (currentTime - counter.getStartTime() > timeWindowMs) {
            counter.reset(currentTime);
        }
        
        // Increment and check if limit exceeded
        return counter.incrementAndGet() > maxRequestsPerMinute;
    }
    
    // Helper class to track request counts with timestamps
    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private long startTime;
        
        public RequestCounter(long startTime) {
            this.startTime = startTime;
        }
        
        public int incrementAndGet() {
            return count.incrementAndGet();
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public void reset(long newStartTime) {
            count.set(0);
            startTime = newStartTime;
        }
    }
}
