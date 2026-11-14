package com.shopverse.product.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

// Runs BEFORE every request reaches a controller.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)          // run first, so every later log line has the id
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String id = request.getHeader(HEADER);                     // the client may send its own id
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString().substring(0, 8);    // otherwise make a short one
        }
        MDC.put("requestId", id);                                 // logs on THIS thread can now print it
        response.setHeader(HEADER, id);                           // give it back to the client
        try {
            chain.doFilter(request, response);                    // continue to the controller
        } finally {
            MDC.remove("requestId");                              // Tomcat reuses threads: always clean up
        }
    }
}