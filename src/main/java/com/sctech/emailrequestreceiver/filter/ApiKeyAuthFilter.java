package com.sctech.emailrequestreceiver.filter;

import com.sctech.emailrequestreceiver.constant.AppHeaders;
import com.sctech.emailrequestreceiver.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Autowired
    private ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String ApiRequestId;

        apiKeyService.validateApiKey(request)
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication);

        if(request.getHeader(AppHeaders.REQUEST_ID) == null || request.getHeader(AppHeaders.REQUEST_ID).isEmpty()){
            ApiRequestId = String.valueOf(UUID.randomUUID());
        }else{
            ApiRequestId = request.getHeader(AppHeaders.REQUEST_ID);
        }

        MDC.put(AppHeaders.REQUEST_ID,ApiRequestId);

        filterChain.doFilter(request, response);
        MDC.clear();
    }
}