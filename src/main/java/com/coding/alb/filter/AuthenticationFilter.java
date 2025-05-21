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
import java.util.Arrays;
import java.util.List;

@Component
@Order(1) // Run after RateLimitingFilter but before FaviconFilter
@Slf4j
public class AuthenticationFilter implements Filter {

    @Value("${auth.api.key:default-api-key}")
    private String apiKey;
    
    @Value("${auth.excluded.paths:/favicon.ico,/health}")
    private String excludedPathsString;
    
    private List<String> excludedPaths;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest httpRequest) {
            String requestUri = httpRequest.getRequestURI();
            
            // Initialize excluded paths if not done yet
            if (excludedPaths == null) {
                excludedPaths = Arrays.asList(excludedPathsString.split(","));
            }
            
            // Skip authentication for excluded paths
            if (isExcludedPath(requestUri)) {
                chain.doFilter(request, response);
                return;
            }
            
            // Check for API key in header
            String providedApiKey = httpRequest.getHeader("X-API-Key");
            
            if (providedApiKey == null || !providedApiKey.equals(apiKey)) {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Authentication failed: Invalid or missing API key");
                log.warn("Authentication failed for request to {}", requestUri);
                return;
            }
            
            log.info("Request authenticated successfully: {}", requestUri);
        }
        
        // Continue with the filter chain if authenticated
        chain.doFilter(request, response);
    }
    
    private boolean isExcludedPath(String requestUri) {
        return excludedPaths.stream().anyMatch(requestUri::startsWith);
    }
}
