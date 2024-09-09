package com.coding.alb;

import com.coding.alb.service.RequestForwarderService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestForwardingFilter implements Filter {

    @Autowired
    private RequestForwarderService requestForwarderService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        requestForwarderService.forwardRequest((HttpServletRequest)
                request, (HttpServletResponse) response);
        // Optionally, continue with the filter chain if you want to perform additional processing
        chain.doFilter(request, response);
    }

    // ... other filter methods
}
