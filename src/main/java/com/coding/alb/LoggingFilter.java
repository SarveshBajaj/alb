package com.coding.alb;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Component
@Order(1) // chain filtering, I want 1st filter to be this - higher ordering means later.
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cast the request to HttpServletRequest to access HTTP-specific methods
        if (request instanceof HttpServletRequest httpRequest) {

            System.out.println("Received request from " + httpRequest.getRemoteAddr());
            System.out.println(httpRequest.getMethod() + " " + httpRequest.getRequestURI() + " " + httpRequest.getProtocol());

            httpRequest.getParameterMap().forEach((key, values) -> {
                System.out.println("  " + key + ": " + Arrays.toString(values));
            });
            System.out.println("Host: " + httpRequest.getHeader("Host"));
            System.out.println("User-Agent: " + httpRequest.getHeader("User-Agent"));
            System.out.println("Accept: " + httpRequest.getHeader("Accept"));
        }

        // Continue with the next filter in the chain
        chain.doFilter(request, response);
    }
}
