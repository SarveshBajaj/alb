package com.coding.alb.filter;

import com.coding.alb.service.RequestForwarderService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order // last to filter, therefore no fwding of chain reqd
public class RequestForwardingFilter implements Filter {

    @Autowired
    private RequestForwarderService requestForwarderService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        requestForwarderService.forwardRequest((HttpServletRequest)
                request, (HttpServletResponse) response);
    }
}
