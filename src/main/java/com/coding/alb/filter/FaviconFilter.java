package com.coding.alb.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class FaviconFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // If the request is for favicon.ico, just return without processing
        if (httpRequest.getRequestURI().equals("/favicon.ico")) {
            return;
        }

        chain.doFilter(request, response);  // Continue processing other requests
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Optional init
    }

    @Override
    public void destroy() {
        // Optional destroy
    }
}
