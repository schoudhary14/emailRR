package com.sctech.emailrequestreceiver.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sctech.emailrequestreceiver.dto.ExceptionResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component("customAuthenticationEntryPoint")
public class UnauthorizedHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto();
        exceptionResponseDto.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
        exceptionResponseDto.setErrorCode("Unauthorized");
        exceptionResponseDto.setMessage("You are not authorized to access this resource.");

        // Convert the DTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(exceptionResponseDto);

        response.getWriter().write(jsonResponse);
    }
}